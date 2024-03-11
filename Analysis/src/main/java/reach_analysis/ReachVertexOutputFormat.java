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
            IntWritable vertexType = vertex.getValue().getVertexType();
//            if delete
            if (vertexType.get() == 2) {
                stringBuilder.append("0").append("\t");
            } else {
                stringBuilder.append("1").append("\t");
            }
//            if update
            if(!fact.isFlag()){
                stringBuilder.append("0").append("\t");
            } else {
                stringBuilder.append("1").append("\t");
            }
//            PC
            if (!fact.isPCEmpty()){
                for(IntWritable pc : fact.getPC()){
                    stringBuilder.append(pc).append("\t");
                }
            }
            return new Text(stringBuilder.toString());
        }
    }

}
