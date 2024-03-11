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
            StringBuilder stringBuilder = new StringBuilder();
            PreVertexValue preVertexValue = vertex.getValue();
            if(!preVertexValue.isFlag() || !preVertexValue.isExist()) return null;
            stringBuilder.append(vertex.getId()).append("\t");
            HashSet<IntWritable> preds = ((PreState)vertex.getValue().getFact()).getPreds();
            if(!preds.isEmpty()) {
                for (IntWritable pred : preds) {
                    stringBuilder.append(pred.get()).append("\t");
                }
            }
            return new Text(stringBuilder.toString());
        }
    }
}
