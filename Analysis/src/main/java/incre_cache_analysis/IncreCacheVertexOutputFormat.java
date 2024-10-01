package incre_cache_analysis;

import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;


import incre_data.Fact;
import cache_data.CacheState;
import cache_data.CacheVertexValue;

public class IncreCacheVertexOutputFormat extends TextVertexOutputFormat<IntWritable, CacheVertexValue, NullWritable> {
    @Override
    public TextVertexWriter createVertexWriter(TaskAttemptContext context) {
        return new LabelPropagationTextVertexLineWriter();
    }
    
    private class LabelPropagationTextVertexLineWriter extends TextVertexWriterToEachLine {
        @Override
        protected Text convertVertexToLine(Vertex<IntWritable, CacheVertexValue, NullWritable> vertex)
        {
            StringBuilder stringBuilder = new StringBuilder();
            Fact fact = vertex.getValue().getFact();
            stringBuilder.append(vertex.getId()).append("\t");
            if (fact != null) {
                /// stringBuilder.append("S:\t").append(((CacheState)fact).statetoString()); // in_fact
                stringBuilder.append(((CacheState)fact).toString());
            }
            else{
                stringBuilder.append("0\t");
                /// stringBuilder.append("S:\t0");
            }
            return new Text(stringBuilder.toString());
        }
    }
}
