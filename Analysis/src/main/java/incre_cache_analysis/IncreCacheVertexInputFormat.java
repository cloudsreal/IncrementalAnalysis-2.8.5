package incre_cache_analysis;

import com.google.common.collect.ImmutableList;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.util.regex.Pattern;

import cache_data.CacheVertexValue;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class IncreCacheVertexInputFormat extends TextVertexInputFormat<IntWritable, CacheVertexValue, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile("\t");
    JedisPoolConfig config = new JedisPoolConfig();
//    public static JedisPool pool = new JedisPool("localhost", 6379);
    public static JedisPool pool;

    @Override
    public TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
        config.setMaxTotal(3000);
        config.setMaxIdle(2000); //最大空闲连接数
        config.setMaxWaitMillis(50 * 1000); //获取Jedis连接的最大等待时间（50秒）
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        String host = "r-bp1bf7htsdwzpgil6l.redis.rds.aliyuncs.com";
        int port = 6379;
        pool = new JedisPool(config, host, port);
        return new IncreCacheVertexReader();
    }

    public class IncreCacheVertexReader extends TextVertexReaderFromEachLineProcessed<String[]>
    {
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

            if(nFlag){ // UN
                String fact_str = null;
                Jedis fact_jedis = null;
                try {
                    fact_jedis = pool.getResource();
                    fact_str = fact_jedis.get(tokens[0]+"f");
                } catch (Exception e) {
                    /// LOGGER.error("jedis set error:", e);
                    System.out.println("jedis set error: STEP preprocessing output");
                } finally {
                    if (null != fact_jedis)
                        fact_jedis.close(); // release resouce to the pool
//                    else{
//                        CommonWrite.method2("\nId:" + tokens[0] + ", jedis is null");
//                    }
                }
                if(fact_str == null || fact_str.isEmpty() || fact_str.charAt(3) == '0' ){ // case : 1) new added node, only stmt in redis 2) Fact: 0
                    cacheVertexValue = new CacheVertexValue(eFlag);
                } else { // case : PU or node influenced by new added node/edge, get fact in redis
                    cacheVertexValue = new CacheVertexValue(fact_str.substring(5), eFlag); //S:\t1\t...
                }
            } else {
                cacheVertexValue = new CacheVertexValue(eFlag);
            }

            if(eFlag) {
                String stmt_str = null;
                Jedis stmt_jedis = null;
                try {
                    stmt_jedis = pool.getResource();
                    stmt_str = stmt_jedis.get(tokens[0]+"s");
                } catch (Exception e) {
                    System.out.println("jedis set error: STEP preprocessing output");
                } finally {
                    if (null != stmt_jedis)
                        stmt_jedis.close(); // release resouce to the pool
//                    else {
//                        CommonWrite.method2("\nId:" + tokens[0] + ", jedis is null");
//                    }
                }
                cacheVertexValue.setStmts(stmt_str, false);
            }

            return cacheVertexValue;
        }

        @Override
        protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException
        {
            return ImmutableList.of();
        }

        @Override
        public void close() throws IOException {
            super.close();
            pool.close();
        }
    }
}