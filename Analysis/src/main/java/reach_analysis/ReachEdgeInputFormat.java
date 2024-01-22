package reach_analysis;

import org.apache.giraph.io.formats.TextEdgeInputFormat;
import org.apache.hadoop.io.IntWritable;
import reach_data.ReachEdgeValue;

public class ReachEdgeInputForm extends TextEdgeInputFormat<IntWritable, ReachEdgeValue> {
    private static final Pattern SEPARATOR = Pattern.compile("\t");

    @Override
    public EdgeReader<IntWritable, ReachEdge> createEdgeReader(InputSplit split, TaskAttemptContext context) {
        return new IntCacheEdgeValueTextEdgeReader();
    }

    public class IntCacheEdgeValueTextEdgeReader extends TextEdgeReaderFromEachLineProcessed<IntTriple> {
        @Override
        protected IntTriple preprocessLine(Text line) {
            String[] tokens = SEPARATOR.split(line.toString());
            int id = Integer.parseInt(tokens[0]);
            String type = tokens[1];
            return new IntTriple(id, type);
        }

        @Override
        protected IntWritable getSourceVertexId(IntTriple endpoints) {
            return new IntWritable(endpoints.getFirst());
        }

        @Override
        protected IntWritable getTargetVertexId(IntTriple endpoints) {
            // You can modify this based on your graph structure
            return new IntWritable(endpoints.getFirst());
        }

        @Override
        protected CacheEdgeValue getValue(IntTriple endpoints) throws IOException {
            return new CacheEdgeValue(endpoints.getSecond());
        }
    }
}
