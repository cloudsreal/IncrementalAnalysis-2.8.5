package reach_analysis;

import org.apache.giraph.edge.Edge;
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
        protected Text convertVertexToLine(Vertex<IntWritable, ReachVertexValue, ReachEdgeValue> vertex)
        {
            ReachVertexValue value = vertex.getValue();
            if(!value.isPA() && value.isPC()) return null;      // deleted node
            if(!value.isEntry() && value.isPU()) return null; // not in sub-CFG
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(vertex.getId()).append("\t");
            /// String stmt = vertex.getValue().getStmtLine();
            /// if(stmt != null){
            ///     stringBuilder.append("1\t").append(stmt).append("\t");
            /// }
            /// else{
            ///     stringBuilder.append("0\t");
            /// }

//            if(value.isPA() || value.isPC()){                   // UN
//                stringBuilder.append("1").append("\t");
//            } else {
//                stringBuilder.append("0").append("\t");
//            }
//            if(value.isPC()){                                   // PC
//                stringBuilder.append("1").append("\t");
//            } else {
//                stringBuilder.append("0").append("\t");
//            }
            if(!value.isPC()) { // UA
                stringBuilder.append("1\t");
            } else {
                stringBuilder.append("0\t");
            }
            if(value.isEntry()){ // Entry
                stringBuilder.append("1\t");
            } else {
                stringBuilder.append("0\t");
            }
//            for(Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
//                stringBuilder.append(edge.getTargetVertexId().get());
//                stringBuilder.append("\t");
//            }
            return new Text(stringBuilder.toString());
        }
    }

}
