package reach_analysis;

import com.google.common.collect.ImmutableList;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import reach_data.ReachEdgeValue;
import reach_data.ReachVertexValue;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.regex.Pattern;


public class ReachCacheVertexInputFormat extends TextVertexInputFormat<IntWritable, ReachVertexValue, ReachEdgeValue> {
    private static final Pattern SEPARATOR = Pattern.compile("\t");
    //    public static JedisPool pool = new JedisPool("localhost", 6379);
    public JedisPoolConfig config = new JedisPoolConfig();
    private static final int BATCH_SIZE = 1000;
    private int batchCount = 0;
    private Pipeline pipeline = null;
    private Jedis jedis = null;
    JedisPool pool = null;

    @Override
    public TextVertexInputFormat<IntWritable, ReachVertexValue, ReachEdgeValue>.TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
        config.setMaxIdle(700);
        config.setMaxTotal(1000);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        String host = "r-bp1r9wn09qjvy0wnyz.redis.rds.aliyuncs.com";
        int port = 6379;
        pool = new JedisPool(config, host, port);
        return new ReachVertexReader();
    }

    public class ReachVertexReader extends TextVertexReaderFromEachLineProcessed<String[]>
    {
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
        protected ReachVertexValue getValue(String[] tokens) {
            ReachVertexValue vertexValue = new ReachVertexValue(tokens[tokens.length - 1].charAt(0));

            int index = tokens.length - 1;
            if (vertexValue.getPA() || vertexValue.getPC()){
                index -= 1;
            }
            else{
                return vertexValue;
            }

            StringBuilder stmt = null;
            if (index >= 1) {
                stmt = new StringBuilder();
                for (int i = 1; i < index; i++) {
                    stmt.append(tokens[i]);
                    stmt.append('\t');
                }
                stmt.append(tokens[index]);
            }

            if(stmt != null){
                vertexValue.setStmt(stmt.toString());
            }

            if (pipeline == null) {
                jedis = pool.getResource();
                pipeline = jedis.pipelined();
            }
            if (vertexValue.isDel()) {
//                pipeline.del(tokens[0] + "s");
                pipeline.del(tokens[0] + "f");
            } else {
//                pipeline.set(tokens[0] + "s", stmt.toString());
//                pipeline.del(tokens[0] + "f");
                pipeline.set(tokens[0] + "f", "S:\t0");
            }
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

            /// StringBuilder stmt = new StringBuilder();
            /// String stmt = null;
//            StringBuilder stmt = null;
//            if(index >= 1) stmt = new StringBuilder();
//            for(int i = 1; i < index; i++)
//            {
//                stmt.append(tokens[i]);
//                stmt.append('\t');
//                // stmt.concat(tokens[i]);
//                // stmt.concat("\t");
//            }
//            if(index >= 1){
//                /// stmt.concat(tokens[index]);
//                stmt.append(tokens[index]);
//            }
//
//            /// if(stmt != null) vertexValue.setStmtLine(stmt.toString());
//            if(stmt != null){
//                Jedis jedis = null;
//                try {
//                    jedis = pool.getResource();
//                    if(vertexValue.isDel()){ // deleted node
//                        jedis.del(tokens[0]);
//                    }
//                    else{
//                        // added/changed node:
//                        //  added node, old stmt/old IN_k are all empty,
//                        //  changed node, old stmt/old IN_k cannot be reused
//                        jedis.set(tokens[0], stmt.toString());
//                    }
//                } catch (Exception e) {
//                    /// LOGGER.error("jedis set error:", e);
//                    System.out.println("jedis set error: STEP preprocessing output");
//                } finally {
//                    if (null != jedis)
//                        jedis.close(); // release resouce to the pool
//                }
//            }

            return vertexValue;
        }

        @Override
        protected Iterable<Edge<IntWritable, ReachEdgeValue>> getEdges(String[] tokens) throws IOException
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

//            super.close();
//            pool.close();
        }
    }
}
