package pre_analysis;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.giraph.worker.WorkerContext;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;


import data.Fact;
import data.Tool;
import pre_data.PreState;
import pre_data.PreTool;
import pre_data.PreVertexValue;
import pre_data.PreState;

import java.util.HashSet;

public class PreVertexOutputFormat extends TextVertexOutputFormat<IntWritable, PreVertexValue, NullWritable> {
    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, PreVertexValue, NullWritable> vertex)
        {
            PreVertexValue preVertexValue = vertex.getValue();
            if(!preVertexValue.isExist()) return null;
            if(!preVertexValue.isUpdated() && !preVertexValue.isUA()) return null;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(vertex.getId()).append("\t");
            String stmt = vertex.getValue().getStmtLine();
            if(!stmt.isEmpty()){
                stringBuilder.append(stmt).append("\t");
            }
//            stringBuilder.append(vertex.getValue().getVertexType()).append('\t');
            if(preVertexValue.isUA()) {
                stringBuilder.append("UA");
            } else {
                stringBuilder.append("PC");
            }
//            stringBuilder.append("PC:\t");
//            HashSet<IntWritable> pcs = ((PreState)vertex.getValue().getFact()).getPCs();
//            if(!pcs.isEmpty()){
//                stringBuilder.append("1\t");
//                for(IntWritable pc : pcs){
//                    stringBuilder.append(pc.get()).append("\t");
//                }
//            } else {
//                stringBuilder.append("0\t");
//            }
//            stringBuilder.append("UA:\t");
//            HashSet<IntWritable> preds = ((PreState)vertex.getValue().getFact()).getPreds();
//            if(!preds.isEmpty()) {
//                stringBuilder.append("1\t");
//                for (IntWritable pred : preds) {
//                    stringBuilder.append(pred.get()).append("\t");
//                }
//            } else {
//                stringBuilder.append("0\t");
//            }
            return new Text(stringBuilder.toString());
        }
    }
}
