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

import java.io.IOException;
import java.util.regex.Pattern;


public class ReachAliasVertexInputFormat extends TextVertexInputFormat<IntWritable, ReachVertexValue, ReachEdgeValue> {
    private static final Pattern SEPARATOR = Pattern.compile("\t");
    public static JedisPool pool = new JedisPool("localhost", 6379);

    @Override
    public TextVertexInputFormat<IntWritable, ReachVertexValue, ReachEdgeValue>.TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
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
            /// StringBuilder stmt = new StringBuilder();
            /// String stmt = null;
            StringBuilder stmt = null;
            if(index >= 1) stmt = new StringBuilder();
            for(int i = 1; i < index; i++)
            {
                stmt.append(tokens[i]);
                stmt.append('\t');
                // stmt.concat(tokens[i]);
                // stmt.concat("\t");
            }
            if(index >= 1){
                /// stmt.concat(tokens[index]);
                stmt.append(tokens[index]);
            }

            /// if(stmt != null) vertexValue.setStmtLine(stmt.toString());
            if(stmt != null){
                Jedis jedis = null;
                try {
                    jedis = pool.getResource();
                    if(vertexValue.isDel()){ // deleted node
                        jedis.del(tokens[0]);
                    }
                    else{ 
                        // added/changed node: 
                        //  added node, old stmt/old gs/old IN_k are all empty, 
                        //  changed node, old stmt/old gs/old IN_k cannot be reused
                        /// stmt.append("GS:0\tF:0");
                        jedis.set(tokens[0], stmt.toString());
                    }
                } catch (Exception e) {
                    /// LOGGER.error("jedis set error:", e);
                    System.out.println("jedis set error: STEP preprocessing output");
                } finally {
                    if (null != jedis)
                        jedis.close(); // release resouce to the pool
                }
            }
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
            pool.close();
        }
    }
}
