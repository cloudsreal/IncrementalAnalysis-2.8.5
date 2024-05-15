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

import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PutVertexInputFormat extends TextVertexInputFormat<IntWritable, NullWritable, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile("\t");
		public static JedisPool pool = new JedisPool("localhost", 6379);

    @Override
    public TextVertexInputFormat<IntWritable, NullWritable, NullWritable>.TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
        return new PreVertexReader();
    }

        public class PreVertexReader extends TextVertexReaderFromEachLineProcessed<String[]>
        {
            @Override
            protected String[] preprocessLine(Text line) {
								String str = line.toString();
                String[] tokens = SEPARATOR.split(str);
								Matcher m = SEPARATOR.matcher(str);
								if(m.find()){
									int index = m.start();

									try (Jedis jedis = pool.getResource()) {
										jedis.set(tokens[0], str.substring(index+1));
									} catch (Exception e) {
									/// LOGGER.error("jedis set error:", e);
									}
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
                // PreVertexValue preVertexValue = new PreVertexValue();
                // if (Integer.parseInt(tokens[1]) == 1) {
                //     preVertexValue.setExist(true);
                // }
                // if (Integer.parseInt(tokens[2]) == 1) {
                //     preVertexValue.setFlag(true);
                // }
                // for(int i = 3; i < tokens.length; i++)
                // {
                //     preVertexValue.addPC(Integer.parseInt(tokens[i]));
                // }
                // return preVertexValue;
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
							pool.close();
						}
        }
}
