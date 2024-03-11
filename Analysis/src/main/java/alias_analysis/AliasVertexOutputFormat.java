package alias_analysis;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import data.Fact;
import data.Tool;
import alias_data.AliasVertexValue;
import alias_data.Pegraph;
import alias_data.AliasTool;

public class AliasVertexOutputFormat extends TextVertexOutputFormat<IntWritable, AliasVertexValue, NullWritable> {
    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, AliasVertexValue, NullWritable> vertex)
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(vertex.getId()).append("\t");

            AliasVertexValue value = (AliasVertexValue)vertex.getValue();
            stringBuilder.append(value.stmtstoString());
            stringBuilder.append(value.gstoretoString());
            stringBuilder.append(value.pegtoString());

            // Fact fact = vertex.getValue().getFact();
            // int sum = 0;
            // if (fact != null) {
            //     /// sum = ((Pegraph)fact).getNumEdges();
            //     /// stringBuilder.append(sum);

            //     /// if(vertex.getValue().getTool() != null){
            //     ///     AliasTool tool = (AliasTool)(vertex.getValue().getTool());
            //     ///     Fact out_fact = tool.transfer(vertex.getValue().getStmtList(), fact);
            //     ///     sum = ((Pegraph)out_fact).getNumEdges();
            //     ///     stringBuilder.append(sum);
            //     /// }
            //     /// else{
            //     ///     sum = ((Pegraph)fact).getNumEdges();
            //     ///     stringBuilder.append(sum);
            //     /// }
            //     stringBuilder.append("1\t");
            //     sum = ((Pegraph)fact).getNumEdges();
            //     stringBuilder.append(sum);
            // }
            // else{
            //     stringBuilder.append("0");
            // }


            return new Text(stringBuilder.toString());
        }
    }
}
