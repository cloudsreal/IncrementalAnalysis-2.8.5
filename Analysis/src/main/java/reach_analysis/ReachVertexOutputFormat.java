package reach_analysis;

import data.Fact;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.python.antlr.op.In;
import reach_data.ReachEdgeValue;
import reach_data.ReachState;
import reach_data.ReachVertexValue;

import java.io.IOException;
import java.util.HashSet;

public class ReachVertexOutputFormat extends TextVertexOutputFormat<IntWritable, ReachVertexValue, ReachEdgeValue> {

    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, ReachVertexValue, ReachEdgeValue> vertex) {
            ReachState fact = (ReachState) vertex.getValue().getFact();
            if(!fact.isPU() && !fact.isFlag()) return null;
            if(vertex.getValue().getVertexType() == 'd') return null;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(vertex.getId()).append("\t");
            String stmt = vertex.getValue().getStmtLine();
            if (!stmt.isEmpty()) {
                stringBuilder.append(stmt).append("\t");
            }
            if(fact.isPU() || (fact.isFlag() && !fact.isPC())) {
                stringBuilder.append("UA\t");
            }
            else {
                stringBuilder.append("PC\t");
            }
            return new Text(stringBuilder.toString());
        }
    }

}
