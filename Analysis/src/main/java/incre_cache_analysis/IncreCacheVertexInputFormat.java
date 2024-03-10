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
import data.CommonWrite;

public class IncreCacheVertexInputFormat extends TextVertexInputFormat<IntWritable, CacheVertexValue, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile("\t");

    @Override
    public TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
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
            // StringBuilder stmt = new StringBuilder();
            // for(int i = 1; i < tokens.length - 1; i++)
            // {
            //     stmt.append(tokens[i]);
            //     stmt.append('\t');
            // }
            // // stmt.append(tokens[tokens.length - 1]);
            // if(tokens.length > 1){
            //     stmt.append(tokens[tokens.length - 1]);
            // }
            // return new CacheVertexValue(stmt.toString());

            StringBuilder stmt = new StringBuilder();
            int i = 1;
            for(; i < tokens.length; i++){
                /// CommonWrite.method2("token-idx: "+ String.valueOf(i) + ": " + tokens[i]);
                if(tokens[i].equals("S:")){
                    break;
                }
                else{
                    stmt.append(tokens[i]);
                    stmt.append('\t');
                }
            }

            StringBuilder fact = new StringBuilder();
            if(tokens[i+1].equals("0")){
                return new CacheVertexValue(stmt.toString());
            }
            else{
                i= i + 2;
                for(; i < tokens.length; i++){
                    fact.append(tokens[i]);
                    fact.append('\t');
                }
                /// CommonWrite.method2(fact.toString());
                return new CacheVertexValue(stmt.toString(), fact.toString());
            }
        }

        @Override
        protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException
        {
            return ImmutableList.of();
        }
    }
}
