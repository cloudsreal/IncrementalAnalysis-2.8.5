package reach_analysis;

import data.Fact;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import reach_data.ReachState;
import reach_data.ReachVertexValue;

import java.io.IOException;

public class ReachVertexOutputFormat extends TextVertexOutputFormat<IntWritable, ReachVertexValue, IntWritable> {

    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
//        @Override
//        protected Text convertVertexToLine(Vertex<IntWritable, ReachVertexValue, Text> vertex)
//        {
//            ReachState fact = (ReachState) vertex.getValue().getFact();
//            if(!fact.isFlag()) return null;
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("id: ").append(vertex.getId()).append(" PC : ");
//            if (!fact.isPCEmpty()){
//                stringBuilder.append(fact.getPC());
//            }
//            else{
//                stringBuilder.append("0");
//            }
//            return new Text(stringBuilder.toString());
//        }
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, ReachVertexValue, IntWritable> vertex)
        {
            return new Text("id: " + vertex.getId());
        }

    }

}
