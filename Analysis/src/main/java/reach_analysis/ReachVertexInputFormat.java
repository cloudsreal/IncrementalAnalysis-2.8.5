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

import java.io.IOException;
import java.util.regex.Pattern;

public class ReachVertexInputFormat extends TextVertexInputFormat<IntWritable, ReachVertexValue, ReachEdgeValue> {
    private static final Pattern SEPARATOR = Pattern.compile("\t");

    @Override
    public TextVertexInputFormat<IntWritable, ReachVertexValue, ReachEdgeValue>.TextVertexReader createVertexReader(InputSplit split, TaskAttemptContext context) throws IOException {
        return new ReachVertexReader();
    }

    public class ReachVertexReader extends TextVertexReaderFromEachLineProcessed<String[]> {

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
            if (vertexValue.getPA() || vertexValue.getPC()) {
                index -= 1;
            }
            

            StringBuilder stmt = null;
            /// StringBuilder stmt = new StringBuilder();
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

            return vertexValue;
        }

        @Override
        protected Iterable<Edge<IntWritable, ReachEdgeValue>> getEdges(String[] tokens) throws IOException {
            return ImmutableList.of();
        }

    }
}
