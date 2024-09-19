package incre_cache_analysis;

import com.google.common.collect.ImmutableList;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cache_data.CacheVertexValue;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

public class IncreCacheVertexInputFormat extends TextVertexInputFormat<IntWritable, CacheVertexValue, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile("\t");
    JedisPoolConfig config = new JedisPoolConfig();
    public static JedisPool pool;
//    public static JedisPool pool = new JedisPool("localhost", 6379);
    LinkedList<String> redis_res = new LinkedList<String>();
    Iterator<String> iter;

    private int batchCount = 0;
    private Pipeline pipeline = null;
    private Jedis jedis = null;

    @Override
    public TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
        config.setMaxTotal(300);
        config.setMaxIdle(200); //最大空闲连接数
//        config.setMaxWaitMillis(30 * 1000); //获取Jedis连接的最大等待时间（50秒）
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
//        String host = "r-bp1r9wn09qjvy0wnyz.redis.rds.aliyuncs.com";
        String host = "r-bp15hcijabhr8nk0nq.redis.rds.aliyuncs.com";
        int port = 6379;
        pool = new JedisPool(config, host, port);
        return new IncreCacheVertexReader();
    }

    public class IncreCacheVertexReader extends TextVertexReaderFromEachLineProcessed<String[]>
    {
        RecordReader<LongWritable, Text> pre_lineRecordReader = null;

        @Override
        public void initialize(InputSplit inputSplit, TaskAttemptContext context)
               throws IOException, InterruptedException {
            super.initialize(inputSplit, context);

            pre_lineRecordReader = textInputFormat.createRecordReader(inputSplit, context);
            pre_lineRecordReader.initialize(inputSplit, context);

            while(pre_lineRecordReader.nextKeyValue()){
                Text pre_line = pre_lineRecordReader.getCurrentValue();
                /// CommonWrite.method2(pre_line.toString());
                String[] tokens = SEPARATOR.split(pre_line.toString());

                boolean nFlag = false;
                boolean eFlag = false;
                if(tokens[1].charAt(0) == '1') nFlag = true;
                if(tokens[2].charAt(0) == '1') eFlag = true;

//                if(eFlag && nFlag){ // m2
                if(nFlag) { // m1
                    if (pipeline == null) {
                        jedis = pool.getResource();
                        pipeline = jedis.pipelined();
                    }
                    pipeline.get(tokens[0]+"f");
                    batchCount++;
                }

                if(batchCount > 1000){
                    getFactBatch();
                }
            }

            getFactBatch();
            pool.close();
            pre_lineRecordReader.close();
            iter = redis_res.iterator();
        }

        public void getFactBatch(){
            if (pipeline != null) {
                try {
                    List<Object> result = new ArrayList<Object>();
                    result = pipeline.syncAndReturnAll();

                    LinkedList<String> tail_strList = 
                        result.stream().map(obj -> obj == null ? "S:\t0" : obj.toString())
                              .collect(Collectors.toCollection(LinkedList::new));

                    redis_res.addAll(tail_strList);
                } catch (Exception e) {
                    System.out.println("Pipeline sync error: " + e.getMessage());
                } finally {
                    pipeline.close();
                    pipeline = null;
                    jedis.close();
                    jedis = null;
                    batchCount = 0;
                }
            }
            if (jedis != null) {
               jedis.close(); // Return Jedis instance to pool
               jedis = null;
            }
       }

        @Override
        protected String[] preprocessLine(Text line) {
            String[] tokens = SEPARATOR.split(line.toString());
            return tokens;
        }

        @Override
        protected IntWritable getId(String[] tokens) {
            int id = Integer.parseInt(tokens[0]);
            /// CommonWrite.method2("\nId:" + tokens[0]);
            return new IntWritable(id);
        }

        @Override
        protected CacheVertexValue getValue(String[] tokens) {
            boolean nFlag = false;
            boolean eFlag = false;
            if(tokens[1].charAt(0) == '1') nFlag = true;
            if(tokens[2].charAt(0) == '1') eFlag = true;

            CacheVertexValue cacheVertexValue;
            
            StringBuilder stmt = new StringBuilder();
            for (int i = 3; i < tokens.length; i++) {
                stmt.append(tokens[i]).append("\t");
            }

//            if(eFlag && nFlag) { // m2
            if(nFlag) { // m1
                String fact_str = iter.next();
                if(fact_str == null || fact_str.isEmpty() || fact_str.charAt(3) == '0' ){ // case : 1) new added node, only stmt in redis 2) Fact: 0
                    cacheVertexValue = new CacheVertexValue(eFlag);
                } else { // case : PU or node influenced by new added node/edge, get fact in redis
                    cacheVertexValue = new CacheVertexValue(fact_str.substring(5), eFlag); //S:\t1\t...
                }
            } else {
                cacheVertexValue = new CacheVertexValue(eFlag);
            }

            cacheVertexValue.setStmts(stmt.toString(), false);

            return cacheVertexValue;
        }

        @Override
        protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException
        {
            return ImmutableList.of();
        }



    }
}