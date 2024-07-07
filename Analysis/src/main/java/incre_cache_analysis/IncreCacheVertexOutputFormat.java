package incre_cache_analysis;

import cache_data.CacheTool;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.io.formats.TextVertexOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;


import data_incre.Fact;
import data_incre.Tool;
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
                Tool tool = new CacheTool();
                Fact out_fact = tool.transfer(vertex.getValue().getStmtList(), fact);
                stringBuilder.append((CacheState)out_fact).append("\t"); // item
                stringBuilder.append("S:\t").append(((CacheState)fact).statetoString()); // in_fact
            }
            else{
                stringBuilder.append("0\tS:\t0");
            }
            return new Text(stringBuilder.toString());
        }
    }
}
