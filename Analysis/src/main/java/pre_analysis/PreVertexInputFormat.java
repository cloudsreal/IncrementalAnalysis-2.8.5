package pre_analysis;

import com.google.common.collect.ImmutableList;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import pre_data.PreVertexValue;

import java.io.IOException;
import java.util.regex.Pattern;

public class PreVertexInputFormat extends TextVertexInputFormat<IntWritable, PreVertexValue, NullWritable> {

    private static final Pattern SEPARATOR = Pattern.compile("\t");

    @Override
    public TextVertexInputFormat<IntWritable, PreVertexValue, NullWritable>.TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
    {
        return new PreVertexReader();
    }

        public class PreVertexReader extends TextVertexReaderFromEachLineProcessed<String[]>
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
            protected PreVertexValue getValue(String[] tokens) {
                int i = 0;
                StringBuilder stmt = new StringBuilder();
                for(; i < tokens.length; i++){
                    if (i > 0) stmt.append(tokens[i]);
                    if ("1".equalsIgnoreCase(tokens[i + 1]) || "0".equalsIgnoreCase(tokens[i + 1])) {
                        break;
                    } else if(i > 0) {
                        stmt.append("\t");
                    }
                }
                PreVertexValue preVertexValue = new PreVertexValue(stmt.toString());
                preVertexValue.setUpdated("1".equalsIgnoreCase(tokens[++i]));
                preVertexValue.setPc_flag("1".equalsIgnoreCase(tokens[++i]));
                return preVertexValue;
            }

            @Override
            protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException
            {
                return ImmutableList.of();
            }
        }
}
