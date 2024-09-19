package put_proc;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.regex.Pattern;

public class PutVertexOutputFormat extends TextVertexOutputFormat<IntWritable, PutVertexValue, NullWritable> {
    
    private static final Pattern SEPARATOR = Pattern.compile("\t");

    private static final int BATCH_SIZE = 500;
    private int batchCount = 0;

    public JedisPoolConfig config = new JedisPoolConfig();
    private Pipeline pipeline = null;
    private Jedis jedis = null;
    JedisPool pool = null;

    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        // config.setMaxIdle(800);
        // config.setMaxTotal(1000);

//        config.setMaxIdle(1000);
//        config.setMaxTotal(1200);
//        config.setMaxWait(Duration.ofMillis(1000));
//        //  config.setTestOnBorrow(false);
//        //  config.setTestOnReturn(false);
//        config.setTestOnBorrow(true); // 在借用连接时测试连接有效性
//        config.setTestOnReturn(true); // 在归还连接时测试连接有效性
//        config.setTestWhileIdle(true); // 在空闲时测试连接有效性
//        String host = "r-bp1bf7htsdwzpgil6l.redis.rds.aliyuncs.com";
//        int port = 6379;
//        pool = new JedisPool(config, host, port);

        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, PutVertexValue, NullWritable> vertex)
        {
            // method1 , to HDFS
            PutVertexValue value = vertex.getValue();

            if (value == null)
                return null;

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(vertex.getId()).append("\t");
            if(value.getFact() != null)
                stringBuilder.append(value.getFact()).append("\t");

            return new Text(stringBuilder.toString());

            // method2, to redis

//            if(value == null || value.getFact() == null){
//                return null;
//            }
//
//            String str = value.getFact();
//            int sIndex = str.indexOf("S");
//            int gsIndex = str.indexOf("GS");
//
//            Matcher m = SEPARATOR.matcher(str);
//            if(m.find()){
//                String factPart;
//
//                if(gsIndex == -1){ // cache
//                    factPart = str.substring(sIndex).trim();
//                } else { // alias
//                    factPart = str.substring(gsIndex).trim();
//                }
//
//                if (pipeline == null) {
//                    jedis = pool.getResource();
//                    pipeline = jedis.pipelined();
//                }
//
//                pipeline.set(vertex.getId().get()+ "f", factPart);
//
//                if (batchCount >= BATCH_SIZE) {
//                    if (pipeline != null) {
//                        try {
//                            pipeline.sync();
//                        } catch (Exception e) {
//                            System.out.println("Pipeline sync error: " + e.getMessage());
//                        } finally {
//                            pipeline.close();
//                            pipeline = null;
//                            jedis.close();
//                            jedis = null;
//                            batchCount = 0;
//                        }
//                    }
//                }
//            }
//            return null;
        }

//        @Override
//        public void close() throws IOException {
//            super.close();
//            if (pipeline != null) {
//                pipeline.sync();
//                pipeline.close();
//            }
//            if (jedis != null) {
//                jedis.close(); // Return Jedis instance to pool
//            }
//
//            pool.close();
//        }

        @Override
        public void close(TaskAttemptContext context) throws IOException, InterruptedException {
//            super.close(context);
//            if (pipeline != null) {
//                pipeline.sync();
//                pipeline.close();
//            }
//            if (jedis != null) {
//                jedis.close(); // Return Jedis instance to pool
//            }
//
//            pool.close();
        }
    }
}
