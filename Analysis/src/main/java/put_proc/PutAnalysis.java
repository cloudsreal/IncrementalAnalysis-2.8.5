package put_proc;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;

public class PutAnalysis extends BasicComputation<IntWritable, PutVertexValue, NullWritable, NullWritable> {

    @Override
    public void compute(Vertex<IntWritable, PutVertexValue, NullWritable> vertex, Iterable<NullWritable> messages) {
        if (getSuperstep() == 0) {
            vertex.voteToHalt();
        }
    }
}