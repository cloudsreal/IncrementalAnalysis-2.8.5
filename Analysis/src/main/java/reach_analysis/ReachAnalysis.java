package reach_analysis;

import analysis.Analysis;
import data.Fact;
import data.VertexValue;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import reach_data.ReachState;
import reach_data.ReachMsg;
import reach_data.ReachTool;
import reach_data.ReachVertexValue;

public class ReachAnalysis extends Analysis<ReachVertexValue, Text, ReachMsg> {

    @Override
    public void setAnalysisConf(){
        tool = new ReachTool();
        fact = new ReachState();
        msg = new ReachMsg();
    }

    public boolean beActive(Iterable<ReachMsg> messages, ReachVertexValue reachVertexValue){
        boolean beActive = false;
        for(ReachMsg message : messages){
            String messageType = message.getMsgType().toString();
            if(messageType.equals("PA") || messageType.equals("PC")){
                beActive = true;
                break;
            }
        }
        return beActive;
    }

    public void Compute(Vertex<IntWritable, ReachVertexValue, Text> vertex, Iterable<ReachMsg> messages) {
        setAnalysisConf();
        if (getSuperstep() == 0){
            msg.setVertexID(vertex.getId());
            Text vertexType = vertex.getValue().getVertexType();
            for(Edge<IntWritable, Text> edge : vertex.getEdges()) {
                if (vertexType.toString().equals("ADD")) {
                    msg.setMsgType(new Text("PA"));
                } else if (vertexType.toString().equals("DELETE") || vertexType.toString().equals("CHANGE")) {
                    msg.setMsgType(new Text("PC"));
                } else {
                    Text edgeType = edge.getValue();
                    if (edgeType.toString().equals("ADD")) {
                        msg.setMsgType(new Text("PA"));
                    } else if (edgeType.toString().equals("DELETE")) {
                        msg.setMsgType(new Text("PC"));
                    } else {
                        msg.setMsgType(new Text("PU"));
                    }
                }
                sendMessage(edge.getTargetVertexId(), msg);
            }
        }
        else {
            if(beActive(messages, vertex.getValue())){
                Fact oldFact = vertex.getValue().getFact();
                Fact newFact = tool.combine(messages, vertex.getValue());
                boolean canPropagate = tool.propagate(oldFact, newFact);
                if(canPropagate){
                    msg.setVertexID(vertex.getId());
                    if(((ReachState)newFact).isPCEmpty()){
                        msg.setMsgType(new Text("PA"));
                    } else {
                        msg.setMsgType(new Text("PC"));
                    }
                    for(Edge<IntWritable, Text> edge : vertex.getEdges()){
                        if(!edge.getValue().toString().equals("DELETE")){
                            sendMessage(edge.getTargetVertexId(), msg);
                        }
                    }
                }
            }
            vertex.voteToHalt();
        }
    }
}
