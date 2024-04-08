package incre_alias_analysis;

import alias_data.AliasTool;
import alias_data.AliasVertexValue;
import alias_data.Pegraph;
import data.Fact;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;


public class IncreAliasVertexOutputFormat extends TextVertexOutputFormat<IntWritable, AliasVertexValue, NullWritable> {
    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, AliasVertexValue, NullWritable> vertex)
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("id: ").append(vertex.getId()).append(" edge sum: ");

            // Fact fact = vertex.getValue().getFact();
            // stringBuilder.append("id: ").append(vertex.getId()).append(" edge sum: ");
            // int sum = 0;
            // if (fact != null) {
            //     sum = ((Pegraph)fact).getNumEdges();
            //     stringBuilder.append(sum);
            // }
            // else{
            //     stringBuilder.append("0");
            // }


            Fact fact = vertex.getValue().getFact();
            int sum = 0;
            if (fact != null) {
                /// sum = ((Pegraph)fact).getNumEdges();
                /// stringBuilder.append(sum);

                if(vertex.getValue().getTool() != null){
                    AliasTool tool = (AliasTool)(vertex.getValue().getTool());
                    Fact out_fact = tool.transfer(vertex.getValue().getStmtList(), fact);
                    sum = ((Pegraph)out_fact).getNumEdges();
                    stringBuilder.append(sum);
                 }
                else{
                    sum = ((Pegraph)fact).getNumEdges();
                    stringBuilder.append(sum);
                }
            }
            else{
                stringBuilder.append("0");
            }



            



            return new Text(stringBuilder.toString());
        }
    }
}
