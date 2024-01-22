package reach_analysis;

import org.apache.giraph.io.EdgeReader;
import org.apache.giraph.io.formats.TextEdgeInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import reach_data.Triple;

import java.io.IOException;
import java.util.regex.Pattern;

public class ReachEdgeInputFormat extends TextEdgeInputFormat<IntWritable, Text> {
    private static final Pattern SEPARATOR = Pattern.compile("\t");

    @Override
    public EdgeReader<IntWritable, Text> createEdgeReader
            (InputSplit split, TaskAttemptContext context) {
        return new IntReachEdgeValueTextEdgeReader();
    }

    public class IntReachEdgeValueTextEdgeReader extends TextEdgeReaderFromEachLineProcessed<Triple> {
        @Override
        protected Triple preprocessLine(Text line) {
            String[] tokens = SEPARATOR.split(line.toString());
            int sourceId = Integer.parseInt(tokens[0]);
            int targetId = Integer.parseInt(tokens[1]);
            String edgeType = tokens[2];
            return new Triple(sourceId, targetId, edgeType);
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
        protected Text getValue(Triple endpoints) throws IOException {
            return new Text(endpoints.getEdgeType());
        }
    }
}
