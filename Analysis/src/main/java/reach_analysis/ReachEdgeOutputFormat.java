package reach_analysis;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextEdgeOutputFormat;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import reach_data.ReachEdgeValue;
import reach_data.ReachState;
import reach_data.ReachVertexValue;

public class ReachEdgeOutputFormat extends TextEdgeOutputFormat<IntWritable, ReachVertexValue, ReachEdgeValue> {
    @Override
    public TextEdgeWriter createEdgeWriter(TaskAttemptContext context) {
        return new LabelPropagationTextEdgeLineWriter();
    }

    private class LabelPropagationTextEdgeLineWriter extends TextEdgeWriterToEachLine<IntWritable, ReachVertexValue, ReachEdgeValue> {

        @Override
        protected Text convertEdgeToLine(IntWritable sourceId, ReachVertexValue sourceValue, Edge<IntWritable, ReachEdgeValue> edge){
            ReachState fact = (ReachState) sourceValue.getFact();
            StringBuilder stringBuilder = new StringBuilder();
            if((fact.isPU() && edge.getValue().isFlag()) || (fact.isFlag() && sourceValue.getVertexType() != 'd')) {
                stringBuilder.append(sourceId.get()).append("\t").append(edge.getTargetVertexId().get()).append("\t");
            }
            return new Text(stringBuilder.toString());
        }
    }
}
