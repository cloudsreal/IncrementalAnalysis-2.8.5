package pre_analysis;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;


import pre_data.PreVertexValue;

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
            if(!preVertexValue.isSub()) return null;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(vertex.getId()).append("\t");
            String stmt = vertex.getValue().getStmtLine();
            if(stmt != null && !stmt.isEmpty()){
                stringBuilder.append(stmt).append("\t");
            }
//            stringBuilder.append(vertex.getValue().getVertexType()).append('\t');
            if(!preVertexValue.isPC()){
                stringBuilder.append("UA");
            } else {
                stringBuilder.append("PC");
            }
            return new Text(stringBuilder.toString());
        }
    }
}
