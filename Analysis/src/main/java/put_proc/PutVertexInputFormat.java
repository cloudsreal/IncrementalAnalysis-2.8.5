package put_proc;

import com.google.common.collect.ImmutableList;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class PutVertexInputFormat extends TextVertexInputFormat<IntWritable, PutVertexValue, NullWritable> {

        private static final Pattern SEPARATOR = Pattern.compile("\t");
        //  public static JedisPool pool = new JedisPool("localhost", 6379);
        public JedisPoolConfig config = new JedisPoolConfig();
        /// private static final int BATCH_SIZE = 300;
        private static final int BATCH_SIZE = 1000;
        private int batchCount = 0;
        private Pipeline pipeline = null;
        private Jedis jedis = null;
        JedisPool pool = null;
        private Set<Integer> un_nodes = new TreeSet<>();
        // 120
//        private String un_hpath = "hdfs://master-1-1.c-5624ec5b03a3b36b.cn-hangzhou.emr.aliyuncs.com:9000/client/un_reach";
        // 116
         private String un_hpath = "hdfs://master-1-1.c-9056493f92112533.cn-hangzhou.emr.aliyuncs.com:9000/client/un_reach";
    
        @Override
        public TextVertexInputFormat<IntWritable, PutVertexValue, NullWritable>.TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
        {
            try {
                    BufferedReader start = new BufferedReader(readHDFS(un_hpath));
                    String unsPath = start.readLine();
                    start.close();

                    BufferedReader br = new BufferedReader(readHDFS(unsPath));
                    String s;
                    while((s = br.readLine())!=null)
                    {
                        if(!s.isEmpty()){
                            un_nodes.add(Integer.parseInt(s));
                        }
                    }
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new PreVertexReader();
        }

        public InputStreamReader readHDFS(String path) throws IOException
        {
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(URI.create(path), conf);
            FSDataInputStream hdfsInStream = fs.open(new Path(path));
            return new InputStreamReader(hdfsInStream, StandardCharsets.UTF_8);
        }

        public class PreVertexReader extends TextVertexReaderFromEachLineProcessed<String[]>
        {
            @Override
            protected String[] preprocessLine(Text line) {
                String str = line.toString();
                String[] tokens = SEPARATOR.split(str);
                
                if(!un_nodes.contains(Integer.parseInt(tokens[0])))
                    return null;
                
                return tokens;
            }

            @Override
            protected IntWritable getId(String[] tokens) {
                if(tokens == null) {
//                    return null;
                    return new IntWritable(-1);
                }
                
                int id = Integer.parseInt(tokens[0]);
                return new IntWritable(id);
            }

            @Override
            protected PutVertexValue getValue(String[] tokens) {
                if(tokens == null){
                    return null;
//                    return NullWritable.get();
                }

                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 1 ; i < tokens.length; i++)
                    stringBuilder.append(tokens[i]).append("\t");

                return new PutVertexValue(stringBuilder.toString());
            }

            @Override
            protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException
            {
                return ImmutableList.of();
            }

        }
}
