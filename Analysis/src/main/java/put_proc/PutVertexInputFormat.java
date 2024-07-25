package put_proc;

import com.google.common.collect.ImmutableList;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.python.antlr.op.In;
// import pre_data.PreState;
// import pre_data.PreVertexValue;
// import reach_analysis.ReachVertexInputFormat;
// import reach_data.ReachVertexValue;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PutVertexInputFormat extends TextVertexInputFormat<IntWritable, NullWritable, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile("\t");
//		public static JedisPool pool = new JedisPool("localhost", 6379);
        public JedisPoolConfig config = new JedisPoolConfig();
        private static final int BATCH_SIZE = 500;
        private int batchCount = 0;
        private Pipeline pipeline = null;
        private Jedis jedis = null;
        JedisPool pool = null;

    @Override
    public TextVertexInputFormat<IntWritable, NullWritable, NullWritable>.TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
        config.setMaxIdle(800);
        config.setMaxTotal(1000);
//        config.setTestOnBorrow(false);
//        config.setTestOnReturn(false);
        config.setTestOnBorrow(true); // 在借用连接时测试连接有效性
        config.setTestOnReturn(true); // 在归还连接时测试连接有效性
        config.setTestWhileIdle(true); // 在空闲时测试连接有效性
        String host = "r-bp1bf7htsdwzpgil6l.redis.rds.aliyuncs.com";
        int port = 6379;
        pool = new JedisPool(config, host, port);
        return new PreVertexReader();
    }

        public class PreVertexReader extends TextVertexReaderFromEachLineProcessed<String[]>
        {
            @Override
            protected String[] preprocessLine(Text line) {
                String str = line.toString();
                String[] tokens = SEPARATOR.split(str);
                int sIndex = str.indexOf("S");
                int gsIndex = str.indexOf("GS");

                Matcher m = SEPARATOR.matcher(str);
                if(m.find()){
                    int index = m.start();
                    String stmtPart;
                    String factPart;
                    if(gsIndex == -1){ // cache
                        stmtPart = str.substring(index+1, sIndex).trim();
                        factPart = str.substring(sIndex).trim();
                    } else { // alias
                        stmtPart = str.substring(index+1, gsIndex).trim();
                        factPart = str.substring(gsIndex).trim();
                    }
                    if (pipeline == null) {
                        jedis = pool.getResource();
                        pipeline = jedis.pipelined();
                    }
                    pipeline.mset(tokens[0] + "s", stmtPart, tokens[0] + "f", factPart);
                    batchCount++;
                    if (batchCount >= BATCH_SIZE) {
                        if (pipeline != null) {
                            try {
                                pipeline.sync();
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
                    }
//                    Jedis jedis = null;
//                    try {
//                        jedis = pool.getResource();
//                        jedis.mset(tokens[0] + "s", stmtPart, tokens[0] + "f", factPart);
//                    } catch (Exception e) {
//                        /// LOGGER.error("jedis set error:", e);
//                    } finally {
//                        if (null != jedis) {
//                            jedis.close(); // release resource to the pool
//                        }
//                    }
                }
                return tokens;
            }

            @Override
            protected IntWritable getId(String[] tokens) {
                int id = Integer.parseInt(tokens[0]);
                return new IntWritable(id);
            }

            @Override
            protected NullWritable getValue(String[] tokens) {
								return NullWritable.get();
            }

            @Override
            protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException
            {
                return ImmutableList.of();
            }

						@Override
						public void close() throws IOException {
                            super.close();
                            if (pipeline != null) {
                                pipeline.sync();
                                pipeline.close();
                            }
                            if (jedis != null) {
                                jedis.close(); // Return Jedis instance to pool
                            }
                            pool.close();
//							super.close();
//							pool.close();
						}
        }
}
