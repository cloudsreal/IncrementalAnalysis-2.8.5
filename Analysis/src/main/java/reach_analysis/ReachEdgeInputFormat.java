package reach_analysis;

import org.apache.giraph.io.EdgeReader;
import org.apache.giraph.io.formats.TextEdgeInputFormat;
import org.apache.giraph.utils.IntPair;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.python.antlr.op.In;
import reach_data.Triple;

import java.io.IOException;
import java.util.regex.Pattern;

public class ReachEdgeInputFormat extends TextEdgeInputFormat<IntWritable, IntWritable> {
    private static final Pattern SEPARATOR = Pattern.compile("\t");

    @Override
    public EdgeReader<IntWritable, IntWritable> createEdgeReader
            (InputSplit split, TaskAttemptContext context) {
        return new IntReachEdgeValueTextEdgeReader();
    }

    public class IntReachEdgeValueTextEdgeReader extends TextEdgeReaderFromEachLineProcessed<Triple> {
        @Override
        protected Triple preprocessLine(Text line) {
            String[] tokens = SEPARATOR.split(line.toString());
            int sourceId = Integer.parseInt(tokens[0]);
            int targetId = Integer.parseInt(tokens[1]);
            if (tokens.length > 2) {
                return new Triple(sourceId, targetId, tokens[tokens.length-1]);
            }
            return new Triple(sourceId, targetId);
        }

        @Override
        protected IntWritable getSourceVertexId(Triple endpoints) {
            return new IntWritable(endpoints.getSourceId());
        }

        @Override
        protected IntWritable getTargetVertexId(Triple endpoints) {
            return new IntWritable(endpoints.getTargetId());
        }

        @Override
        protected IntWritable getValue(Triple endpoints) throws IOException {
            return new IntWritable((int)endpoints.getEdgeType());
        }
    }
}