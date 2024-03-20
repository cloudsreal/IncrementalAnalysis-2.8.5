package pre_analysis;

import analysis.Analysis;
import data.Fact;
import data.VertexValue;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import pre_data.*;
import org.apache.hadoop.io.NullWritable;
import reach_data.ReachMsg;
import reach_data.ReachState;
import reach_data.ReachVertexValue;

import java.util.HashSet;

public class PreAnalysis extends Analysis<PreVertexValue, NullWritable, PreMsg> {

    @Override
    public void setAnalysisConf(){
        tool = new PreTool();
        fact = new PreState();
        msg = new PreMsg();
    }

    @Override
    public boolean beActive(Iterable<PreMsg> messages, VertexValue vertexValue){
        PreVertexValue preVertexValue = (PreVertexValue) vertexValue;
        return preVertexValue.isFlag();
    }

    @Override
    public void compute(Vertex<IntWritable, PreVertexValue, NullWritable> vertex, Iterable<PreMsg> messages) {
        setAnalysisConf();
        if (getSuperstep() == 0) {
//            vertex.getValue().setFact(preState);
            if(vertex.getValue().isExist()){
                PreState preState = new PreState();
                for (Edge<IntWritable, NullWritable> edge : vertex.getEdges()) {
                    int predID = edge.getTargetVertexId().get();
                    if(!vertex.getValue().hasPC(predID)){
                        preState.addPred(predID);
                    }
                }
                vertex.getValue().setFact(preState);
            }
            vertex.voteToHalt();
        }
        else {
//            if(beActive(messages, vertex.getValue())){
//                Fact newFact = ((PreTool)tool).combine(messages, vertex.getValue());
//                vertex.getValue().setFact(newFact);
//            }
            vertex.voteToHalt();
        }
    }
}