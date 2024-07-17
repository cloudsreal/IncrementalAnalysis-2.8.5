package reach_analysis;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextEdgeOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import reach_data.ReachEdgeValue;
import reach_data.ReachVertexValue;

public class ReachEdgeOutputFormat extends TextEdgeOutputFormat<IntWritable, ReachVertexValue, ReachEdgeValue> {
    @Override
    public TextEdgeWriter createEdgeWriter(TaskAttemptContext context) {
        return new LabelPropagationTextEdgeLineWriter();
    }

    private class LabelPropagationTextEdgeLineWriter extends TextEdgeWriterToEachLine<IntWritable, ReachVertexValue, ReachEdgeValue> {

        @Override
        protected Text convertEdgeToLine(IntWritable sourceId, ReachVertexValue sourceValue, Edge<IntWritable, ReachEdgeValue> edge){
            // if(!sourceValue.isSub()) return null;
            // StringBuilder stringBuilder = new StringBuilder();
            // stringBuilder.append(edge.getTargetVertexId().get()).append("\t");
            // stringBuilder.append(sourceId.get());
            // return new Text(stringBuilder.toString());

            /// if(!sourceValue.isSub()) return null;
            if((sourceValue.getPC() || sourceValue.getPA()) && edge.getValue().isIn()){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(edge.getTargetVertexId().get()).append("\t");
                stringBuilder.append(sourceId.get());
                return new Text(stringBuilder.toString());
            }
            return null;
        }
    }
}
