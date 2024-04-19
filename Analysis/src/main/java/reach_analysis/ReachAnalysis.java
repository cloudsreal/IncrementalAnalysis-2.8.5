package reach_analysis;

import analysis.Analysis;
import data.Fact;
import data.SetWritable;
import data.VertexValue;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.python.antlr.op.In;
import reach_data.*;

public class ReachAnalysis extends Analysis<ReachVertexValue, IntWritable, ReachMsg> {

    @Override
    public void setAnalysisConf(){
        tool = new ReachTool();
        fact = new ReachState();
        msg = new ReachMsg();
    }

    @Override
    public void compute(Vertex<IntWritable, ReachVertexValue, IntWritable> vertex, Iterable<ReachMsg> messages) {
        setAnalysisConf();
        if (getSuperstep() == 0) {
            ReachState reachState = new ReachState();
            int vertexId = vertex.getId().get();
            char vertexType = vertex.getValue().getVertexType();
            if (vertexType != 'u') {
                reachState.setFlag(true);
            }
            vertex.getValue().setFact(reachState);
            for (Edge<IntWritable, IntWritable> edge : vertex.getEdges()) {
                char edgeType = (char)edge.getValue().get();
                if (vertexType == 'u' && edgeType == 'u') continue;
                msg.setVertexID(vertex.getId());
                msg.setPredID(vertexId);
                if (vertexType == 'a') {
                    msg.setMsgType(false);
                } else if (vertexType == 'd' || vertexType == 'c') {
                    msg.setMsgType(true);
                } else {
                    if (edgeType == 'a') {
                        msg.setMsgType(false);
                    } else if (edgeType == 'd') {
                        msg.setMsgType(true);
                    }
                }
                sendMessage(edge.getTargetVertexId(), msg);
            }
            vertex.voteToHalt();
        }
        else{
            if (beActive(messages, vertex.getValue())) {
                int vertexId = vertex.getId().get();
                Fact oldFact = vertex.getValue().getFact();
                Fact newFact = tool.combine(messages, vertex.getValue());
                boolean canPropagate = tool.propagate(oldFact, newFact);
                vertex.getValue().setFact(newFact);
                if (canPropagate) {
                    msg.setVertexID(vertex.getId());
                    msg.setPredID(vertexId);
                    if (((ReachState) newFact).isPCEmpty()) {
                        msg.setMsgType(false);
                    } else {
                        msg.setMsgType(true);
                    }
                    for (Edge<IntWritable, IntWritable> edge : vertex.getEdges()) {
                        sendMessage(edge.getTargetVertexId(), msg);
                    }
                }
            }
            vertex.voteToHalt();
        }
    }
}