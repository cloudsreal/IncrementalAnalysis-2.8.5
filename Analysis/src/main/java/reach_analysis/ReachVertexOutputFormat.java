package reach_analysis;

import data.Fact;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.python.antlr.op.In;
import reach_data.ReachState;
import reach_data.ReachVertexValue;

import java.io.IOException;
import java.util.HashSet;

public class ReachVertexOutputFormat extends TextVertexOutputFormat<IntWritable, ReachVertexValue, IntWritable> {

    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, ReachVertexValue, IntWritable> vertex)
        {
            ReachState fact = (ReachState) vertex.getValue().getFact();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(vertex.getId()).append("\t");
            String stmt = vertex.getValue().getStmtLine();
            if(!stmt.isEmpty()){
                stringBuilder.append(stmt).append("\t");
            }

//            stringBuilder.append(vertex.getValue().getVertexType()).append("\t");
//            if update
//            if(!fact.isFlag()){
//                stringBuilder.append("0").append("\t");
//            } else {
//                stringBuilder.append("1").append("\t");
//            }
            HashSet<Integer> preds = vertex.getValue().getPreds();
            if (!preds.isEmpty()){
                stringBuilder.append("P:\t");
                for(int pred : preds){
                    stringBuilder.append(pred).append("\t");
                }
            }
//            PC
            if (!fact.isPCEmpty()){
                stringBuilder.append("PC:\t");
                HashSet<Integer> pcs = fact.getPC();
                for(int pc : pcs){
                    stringBuilder.append(pc).append("\t");
                }
            }
            return new Text(stringBuilder.toString());
        }
    }

}
