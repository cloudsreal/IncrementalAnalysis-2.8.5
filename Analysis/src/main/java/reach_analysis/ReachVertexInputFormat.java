package reach_analysis;

import com.google.common.collect.ImmutableList;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.python.antlr.op.In;
import reach_data.ReachVertexValue;

import javax.validation.constraints.Null;
import java.io.IOException;
import java.util.regex.Pattern;

public class ReachVertexInputFormat extends TextVertexInputFormat<IntWritable, ReachVertexValue, IntWritable> {
    private static final Pattern SEPARATOR = Pattern.compile("\t");

    @Override
    public TextVertexInputFormat<IntWritable, ReachVertexValue, IntWritable>.TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException
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
                ReachVertexValue vertexValue = new ReachVertexValue(tokens[tokens.length - 1]);
                int index = tokens.length - 1;
                if (vertexValue.getVertexType() != 'u'){
                    index -= 1;
                }
                StringBuilder stmt = new StringBuilder();
                for(int i = 1; i < index; i++)
                {
                    stmt.append(tokens[i]);
                    stmt.append('\t');
                }
                if(index >= 1){
                    stmt.append(tokens[index]);
                }
                vertexValue.setStmtLine(stmt.toString());
                return vertexValue;
            }

            @Override
            protected Iterable<Edge<IntWritable, IntWritable>> getEdges(String[] tokens) throws IOException
            {
                return ImmutableList.of();
            }
        }
}
