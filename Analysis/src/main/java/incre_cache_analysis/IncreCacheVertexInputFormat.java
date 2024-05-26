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
import java.util.regex.Matcher;

import cache_data.CacheVertexValue;
import data.CommonWrite;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class IncreCacheVertexInputFormat extends TextVertexInputFormat<IntWritable, CacheVertexValue, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile("\t");
    JedisPoolConfig config = new JedisPoolConfig();
    public static JedisPool pool; 

    @Override
    public TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
        config.setMaxTotal(1000);
        config.setMaxIdle(10); //最大空闲连接数
        config.setMaxWaitMillis(50 * 1000); //获取Jedis连接的最大等待时间（50秒）
        config.setTestOnBorrow(true); //在获取Jedis连接时，自动检验连接是否可用
        config.setTestOnReturn(true);  //在将连接放回池中前，自动检验连接是否有效
        config.setTestWhileIdle(true);  //自动测试池中的空闲连接是否都是可用连接
        pool = new JedisPool(config, "localhost", 6379);
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
//            String value_str = null;
//            Jedis jedis = null;
//            try {
//                jedis = pool.getResource();
//                value_str = jedis.get(tokens[0]);
//            } catch (Exception e) {
//                /// LOGGER.error("jedis set error:", e);
//                System.out.println("jedis set error: STEP preprocessing output");
//            } finally {
//                if (null != jedis)
//                    jedis.close(); // release resouce to the pool
//                else{
//                    CommonWrite.method2("\nId:" + tokens[0] + ", jedis is null");
//                }
//
//            }

//            if(value_str == null){
//                CommonWrite.method2("\nId:" + tokens[0] + " value is null, entry : " + String.valueOf(eFlag));
//                return null;
//            }
//            else{
//                /// CommonWrite.method2("\nId:" + tokens[0] + ", nFlag: " + String.valueOf(nFlag)
//                ///                                         + ", eFlag: " + String.valueOf(eFlag));
//                CommonWrite.method2("\nId:" + tokens[0] + " value: " + value_str);
//            }

//            int index = value_str.indexOf('S');
//            String stmt_str = null;
//            if(index == -1) // id --> stmt → get only stmt
//            {
//                stmt_str = value_str;
//            }
//            else{ // id --> stmt+fact → get only stmt
//                stmt_str = value_str.substring(0, index-1);
//            }

            String stmt_str = null;
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                stmt_str = jedis.get(tokens[0]+"f");
            } catch (Exception e) {
                /// LOGGER.error("jedis set error:", e);
                System.out.println("jedis set error: STEP preprocessing output");
            } finally {
                if (null != jedis)
                    jedis.close(); // release resouce to the pool
                else{
                    CommonWrite.method2("\nId:" + tokens[0] + ", jedis is null");
                }
            }

            if (stmt_str == null){
                return new CacheVertexValue("", false, eFlag);
            } else {
                return new CacheVertexValue(stmt_str, false, eFlag);
            }

//            if(nFlag){ // UA1
//                if(index == -1){ // case : new added node, only stmt in redis
//                    return new CacheVertexValue(stmt_str, false, eFlag);
//                }
//                else{ // case : PU or node influenced by new added node/edge, get fact in redis
//                    if(value_str.charAt(index+3) == '1'){
//                        String fact_str = value_str.substring(index+5);
//                        return new CacheVertexValue(stmt_str, false, fact_str, eFlag);
//                    }
//                    else{
//                        return new CacheVertexValue(stmt_str, false, eFlag);
//                    }
//                }
//            }
//            else{ // PC0
//                return new CacheVertexValue(stmt_str, false, eFlag);
//            }
            

            // StringBuilder stmt = new StringBuilder();
            // int i = 1;
            // for(; i < tokens.length; i++){
            //     /// CommonWrite.method2("token-idx: "+ String.valueOf(i) + ": " + tokens[i]);
            //     if(tokens[i].equals("S:")){
            //         break;
            //     }
            //     else{
            //         stmt.append(tokens[i]);
            //         stmt.append('\t');
            //     }
            // }

            // StringBuilder fact = new StringBuilder();
            // if(tokens[i+1].equals("0")){
            //     return new CacheVertexValue(stmt.toString(), false, false);
            // }
            // else{
            //     i= i + 2;
            //     for(; i < tokens.length; i++){
            //         fact.append(tokens[i]);
            //         fact.append('\t');
            //     }
            //     /// CommonWrite.method2(fact.toString());
            //     return new CacheVertexValue(stmt.toString(), false, fact.toString(), false);
            // }
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
