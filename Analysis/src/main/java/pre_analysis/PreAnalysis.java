package pre_analysis;

import analysis.Analysis;
/// import data.CommonWrite;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import pre_data.PreVertexValue;
import org.apache.hadoop.io.NullWritable;

public class PreAnalysis extends BasicComputation<IntWritable, PreVertexValue, NullWritable, NullWritable> {

    // public PreMsg msg = null;

    // public void setAnalysisConf(){
    //     msg = new PreMsg();
    // }

    public void compute(Vertex<IntWritable, PreVertexValue, NullWritable> vertex, Iterable<NullWritable> messages) {
        /// setAnalysisConf();
        if (getSuperstep() == 0) {
            if(vertex.getValue().isUpdated()){
                vertex.getValue().setSub_flag(true);
                for (Edge<IntWritable, NullWritable> edge : vertex.getEdges()) {
                    sendMessage(edge.getTargetVertexId(), NullWritable.get());
                }
            }
            vertex.voteToHalt();
        }
        else {
            /// CommonWrite.method2(String.valueOf(vertex.getId().get()));
            vertex.getValue().setSub_flag(true);
            vertex.voteToHalt();
        }
    }
}