package put_proc;

import com.google.common.collect.ImmutableList;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PutVertexInputFormat extends TextVertexInputFormat<IntWritable, NullWritable, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile("\t");
		public static JedisPool pool = new JedisPool("localhost", 6379);
        public JedisPoolConfig config = new JedisPoolConfig();
//        public static JedisPool pool = null;

    @Override
    public TextVertexInputFormat<IntWritable, NullWritable, NullWritable>.TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
        config.setMaxIdle(200);
        config.setMaxTotal(300);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
//        String host = "r-bp1rkfthkdyc2z2ghq.redis.rds.aliyuncs.com";
//        int port = 6379;
//        pool = new JedisPool(config, host, port);
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
                    int sIndex = str.indexOf("S");
                    int gsIndex = str.indexOf("GS");
                    String factPart;
                    if(gsIndex == -1){ // cache
                        factPart = str.substring(sIndex).trim();
                    } else { // alias
                        factPart = str.substring(gsIndex).trim();
                    }
                    Jedis jedis = null;
                    try {
                        jedis = pool.getResource();
                        jedis.set(tokens[0] + "f", factPart);
                    } catch (Exception e) {
                        /// LOGGER.error("jedis set error:", e);
                        System.out.println("jedis set error: STEP preprocessing output");
                    } finally {
                        if (null != jedis)
                            jedis.close(); // release resource to the pool
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
