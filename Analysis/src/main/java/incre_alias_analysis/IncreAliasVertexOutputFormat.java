package incre_alias_analysis;

import alias_data.AliasTool;
import alias_data.AliasVertexValue;
import alias_data.Pegraph;
import data_incre.Fact;
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
//            stringBuilder.append("id: ").append(vertex.getId()).append(" edge sum: ");

            stringBuilder.append(vertex.getId()).append("\t");

//            AliasVertexValue value = (AliasVertexValue)vertex.getValue();

            Fact fact = vertex.getValue().getFact();
            int sum = 0;

            if (fact != null) {
                sum = ((Pegraph)fact).getNumEdges();
                stringBuilder.append("S:\t").append(sum);
            }
            else{
                stringBuilder.append("S:\t0");
            }

//            if (fact != null) {
//                if(vertex.getValue().getTool() != null){
//                    AliasTool tool = (AliasTool)(vertex.getValue().getTool());
//                    Fact out_fact = tool.transfer(vertex.getValue().getStmtList(), fact);
//                    sum = ((Pegraph)out_fact).getNumEdges();
//                    stringBuilder.append(sum).append("\t"); //edge sum
//                }
//                else{
//                    sum = ((Pegraph)fact).getNumEdges();
//                    stringBuilder.append(sum).append("\t");
//                }
//            }
//            else{
//                stringBuilder.append("0\t");
//            }

//            stringBuilder.append(value.gstoretoString()); // GS
//            stringBuilder.append(value.pegtoString()); // S
            return new Text(stringBuilder.toString());
        }
    }
}
