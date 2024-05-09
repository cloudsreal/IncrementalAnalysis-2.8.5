package pre_analysis;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextEdgeOutputFormat;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import pre_data.PreVertexValue;

import java.io.IOException;

public class PreEdgeOutputFormat extends TextEdgeOutputFormat<IntWritable, PreVertexValue, NullWritable> {
    @Override
    public TextEdgeWriter createEdgeWriter(TaskAttemptContext context) {
        return new LabelPropagationTextEdgeLineWriter();
    }

    private class LabelPropagationTextEdgeLineWriter extends TextEdgeWriterToEachLine<IntWritable, PreVertexValue, NullWritable> {

        @Override
        protected Text convertEdgeToLine(IntWritable sourceId, PreVertexValue sourceValue, Edge<IntWritable, NullWritable> edge){
            if(!sourceValue.isExist() || !sourceValue.isUpdated()) return null;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(edge.getTargetVertexId().get()).append("\t");
            stringBuilder.append(sourceId.get());
            return new Text(stringBuilder.toString());
        }
    }
}
