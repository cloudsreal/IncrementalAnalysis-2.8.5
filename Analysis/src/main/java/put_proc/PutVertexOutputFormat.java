package put_proc;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.giraph.worker.WorkerContext;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

// import data.Fact;
// import data.Tool;
// import pre_data.PreState;
// import pre_data.PreTool;
// import pre_data.PreVertexValue;
// import pre_data.PreState;

import java.util.HashSet;

public class PutVertexOutputFormat extends TextVertexOutputFormat<IntWritable, NullWritable, NullWritable> {
    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }

    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, NullWritable, NullWritable> vertex)
        {
          return null;
        }
    }
}
