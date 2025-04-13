package reach_analysis;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import reach_data.ReachEdgeValue;
import reach_data.ReachVertexValue;

public class ReachVertexOutputFormat extends TextVertexOutputFormat<IntWritable, ReachVertexValue, ReachEdgeValue> {

    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, ReachVertexValue, ReachEdgeValue> vertex) {
            ReachVertexValue value = vertex.getValue();
            if (!value.getPA() && value.getPC())
                return null; // deleted node
            if (!value.getEntry() && value.isPU())
                return null; // not in sub-CFG
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(vertex.getId()).append("\t");
            if (!value.getPC()) { // UA
                stringBuilder.append("1\t");
            } else {
                stringBuilder.append("0\t");
            }
            if (value.getEntry()) { // Entry
                stringBuilder.append("1\t");
            } else {
                stringBuilder.append("0\t");
            }

            // @zewen, added in 2025.03.19, for :
            // 1) counting the number of unchanged preds, whose dataflow facts are reused in
            // Algorithm 5
            // 2) counting the number of added_only but old nodes, whose dataflow facts are
            // further reused in
            // Algorithm 7
            if (!value.getPC()) { // UA, pc_flag is false
                if (value.getPA()) { // pa_flag = true, pc_flag = false: PA.
                    stringBuilder.append("1\t");

                    if (value.getOld())
                        stringBuilder.append("1\t");
                    else
                        stringBuilder.append("0\t");
                } else { // pa_flag = false, pc_flag = false: PU.
                    stringBuilder.append("0\t");

                    stringBuilder.append("0\t"); // skip
                }
            } else {
                stringBuilder.append("0\t"); // skip, only for aligning format to count unchanged preds

                stringBuilder.append("0\t"); // skip
            }

            if (value.getStmt() != null) {
                stringBuilder.append(value.getStmt() + "\t");
            }
            return new Text(stringBuilder.toString());
        }
    }

}
