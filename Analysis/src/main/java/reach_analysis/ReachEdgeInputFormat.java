package reach_analysis;

import org.apache.giraph.io.EdgeReader;
import org.apache.giraph.io.formats.TextEdgeInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import reach_data.ReachEdgeValue;
import reach_data.Triple;

import java.io.IOException;
import java.util.regex.Pattern;

public class ReachEdgeInputFormat extends TextEdgeInputFormat<IntWritable, ReachEdgeValue> {
    private static final Pattern SEPARATOR = Pattern.compile("\t");

    @Override
    public EdgeReader<IntWritable, ReachEdgeValue> createEdgeReader
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
                return new Triple(sourceId, targetId, tokens[tokens.length-1].charAt(0));
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
        protected ReachEdgeValue getValue(Triple endpoints) throws IOException {
            if(!endpoints.isNew()) { // old edge
                return new ReachEdgeValue(false, false);
            } else if(endpoints.isAdded()){ // added edge
                return new ReachEdgeValue(true, true);
            } else { // deleted edge
                return new ReachEdgeValue(true, false);
            }
        }
    }
}