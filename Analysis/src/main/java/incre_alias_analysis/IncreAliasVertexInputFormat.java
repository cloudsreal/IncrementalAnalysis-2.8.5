package incre_alias_analysis;

import alias_data.AliasVertexValue;

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
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

public class IncreAliasVertexInputFormat extends TextVertexInputFormat<IntWritable, AliasVertexValue, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile("\t");
    JedisPoolConfig config = new JedisPoolConfig();
    public static JedisPool pool;
    //  public static JedisPool pool = new JedisPool("localhost", 6379);
    LinkedList<String> redis_res = new LinkedList<String>();
    Iterator<String> iter;
    private int batchCount = 0;
    private Pipeline pipeline = null;
    private Jedis jedis = null;

    @Override
    public TextVertexInputFormat<IntWritable, AliasVertexValue, NullWritable>.TextVertexReader createVertexReader(
      InputSplit split, TaskAttemptContext context) throws IOException {
      
        config.setMaxTotal(300);
        config.setMaxIdle(200); //最大空闲连接数
        /// config.setMaxWaitMillis(30 * 1000); //获取Jedis连接的最大等待时间（50秒）
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
//        String host = "r-bp1r9wn09qjvy0wnyz.redis.rds.aliyuncs.com";
        String host = "r-bp15hcijabhr8nk0nq.redis.rds.aliyuncs.com";
        int port = 6379;
        pool = new JedisPool(config, host, port);
        return new AliasVertexReader();
  }

    public class AliasVertexReader extends TextVertexReaderFromEachLineProcessed<String[]> 
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

                if(eFlag && nFlag){ // m2
//                if(nFlag) { // m1
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
                        result.stream().map(obj -> obj == null ? "GS:0\tF:0" : obj.toString()) //GS:0	F:1
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
            return new IntWritable(id);
        }

        @Override
        protected AliasVertexValue getValue(String[] tokens) {
            boolean nFlag = false;
            boolean eFlag = false;
            if(tokens[1].charAt(0) == '1') nFlag = true;
            if(tokens[2].charAt(0) == '1') eFlag = true;

            AliasVertexValue aliasVertexValue;

            if(eFlag && nFlag) { // m2
//            if(nFlag) { // m1
                String value_str = iter.next();
                int gs_index = -1;
                gs_index = value_str.indexOf('G'); //GS:\tF:\t
                int f_index = value_str.indexOf('F');
                String gs_str = value_str.substring(gs_index + 3, f_index - 1);
                String fact_str = value_str.substring(f_index + 2);
                aliasVertexValue = new AliasVertexValue(gs_str, fact_str, eFlag);
            } else {
                aliasVertexValue = new AliasVertexValue("0", eFlag);
            }

            // if(nFlag){ // UA1 : can use GS and Fact
            //     String value_str = null;
            //     Jedis value_jedis = null;
            //     try {
            //         value_jedis = pool.getResource();
            //         value_str = value_jedis.get(tokens[0]+"f");
            //     } catch (Exception e) {
            //         System.out.println("jedis set error: STEP preprocessing output");
            //     } finally {
            //         if (null != value_jedis)
            //             value_jedis.close(); // release resouce to the pool
            //     }
            //     int gs_index = -1;
            //     if(value_str != null) {
            //         gs_index = value_str.indexOf('G'); //GS:\tF:\t
            //     }
            //     if(gs_index == -1){  // new added node, only stmt in redis
            //         aliasVertexValue = new AliasVertexValue("0" /*gs is "0"*/,eFlag);
            //     }
            //     else{ // id --> stmt+gs+fact
            //         int f_index = value_str.indexOf('F');
            //         String gs_str = value_str.substring(gs_index + 3, f_index - 1);
            //         String fact_str = value_str.substring(f_index + 2);
            //         aliasVertexValue =  new AliasVertexValue(gs_str, fact_str, eFlag);
            //     }
            // } else { // PC
            //     aliasVertexValue =  new AliasVertexValue("0" /*gs is "0"*/, eFlag);
            // }

            // if(eFlag) { // get stmts from redis for entrys
            //     String stmt_str = null;
            //     Jedis stmt_jedis = null;
            //     try {
            //         stmt_jedis = pool.getResource();
            //         stmt_str = stmt_jedis.get(tokens[0]+"s");
            //     } catch (Exception e) {
            //         System.out.println("jedis set error: STEP preprocessing output");
            //     } finally {
            //         if (null != stmt_jedis)
            //             stmt_jedis.close(); // release resouce to the pool
            //     }
            //     aliasVertexValue.setStmts(stmt_str);
            // }

            StringBuilder stmt = new StringBuilder();
            for (int i = 3; i < tokens.length; i++) {
                stmt.append(tokens[i]).append("\t");
            }

            aliasVertexValue.setStmts(stmt.toString());

            return aliasVertexValue;
        }

        @Override
        protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException {
        return ImmutableList.of();
        }

        @Override
        public void close() throws IOException {
        super.close();
        pool.close();
        }
    }
}
