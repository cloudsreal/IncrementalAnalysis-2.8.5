package pre_analysis;

import com.google.common.collect.ImmutableList;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.python.antlr.op.In;
import pre_data.PreState;
import pre_data.PreVertexValue;
import reach_analysis.ReachVertexInputFormat;
import reach_data.ReachVertexValue;

import java.io.IOException;
import java.util.HashSet;
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
                    if ("a".equalsIgnoreCase(tokens[i + 1]) || "d".equalsIgnoreCase(tokens[i + 1]) || "c".equalsIgnoreCase(tokens[i + 1]) || "u".equalsIgnoreCase(tokens[i + 1])) {
                        break;
                    } else if(i > 0) {
                        stmt.append("\t");
                    }
                }
//                Use stmt, vertexType, updatedType to initialize
                PreVertexValue preVertexValue = new PreVertexValue(stmt.toString(), tokens[++i]);
                if (Integer.parseInt(tokens[++i]) == 1) {
                    preVertexValue.setUpdated(true);
                    i++;
                }
//                Initialize PC
                for(; i < tokens.length; i++) {
                    preVertexValue.addPC(Integer.parseInt(tokens[i]));
                }
                return preVertexValue;
            }

            @Override
            protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException
            {
                return ImmutableList.of();
            }
        }
}
