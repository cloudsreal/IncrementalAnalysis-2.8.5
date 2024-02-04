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
                return new ReachVertexValue(tokens[tokens.length - 1]);
            }

            @Override
            protected Iterable<Edge<IntWritable, IntWritable>> getEdges(String[] tokens) throws IOException
            {
                return ImmutableList.of();
            }
        }
}
