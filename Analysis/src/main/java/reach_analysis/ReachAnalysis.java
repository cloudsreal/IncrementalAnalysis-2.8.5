package reach_analysis;

import alias_analysis.MyWorkerContext;
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
    public boolean beActive(Iterable<ReachMsg> messages, VertexValue vertexValue){
        ReachVertexValue reachVertexValue = (ReachVertexValue) vertexValue;
        boolean beActive = false;
        for(ReachMsg message : messages){
            IntWritable messageType = message.getMsgType();
            if(messageType.get() != 0){
                beActive = true;
                break;
            }
        }
        return beActive;
    }

    @Override
    public void compute(Vertex<IntWritable, ReachVertexValue, IntWritable> vertex, Iterable<ReachMsg> messages) {
        setAnalysisConf();
        if (getSuperstep() == 0) {
            ReachState reachState = new ReachState();
            vertex.getValue().setFact(reachState);
            int vertexId = vertex.getId().get();
            IntWritable vertexType = vertex.getValue().getVertexType();
            for (Edge<IntWritable, IntWritable> edge : vertex.getEdges()) {
                msg.setVertexID(vertex.getId());
                msg.setPredID(vertexId);
                if (vertexType.get() == 1) {
                    msg.setMsgType(new IntWritable(1));
                } else if (vertexType.get() == 2 || vertexType.get() == 3) {
                    msg.setMsgType(new IntWritable(2));
                } else {
                    IntWritable edgeType = edge.getValue();
                    if (edgeType.get() == 1) {
                        msg.setMsgType(new IntWritable(1));
                    } else if (edgeType.get() == 2) {
                        msg.setMsgType(new IntWritable(2));
                    } else {
                        msg.setMsgType(new IntWritable(0));
                    }
                }
                sendMessage(edge.getTargetVertexId(), msg);
            }
            vertex.voteToHalt();
        }
        else {
            if(beActive(messages, vertex.getValue())){
//                ReachState reachState = new ReachState();
//                for(ReachMsg message : messages){
//                    IntWritable messageType = message.getMsgType();
//                    if(messageType.get() != 0){
//                        reachState.addPC(message.getPredID());
//                    }
//                }
//                vertex.getValue().setFact(reachState);
                Fact oldFact = vertex.getValue().getFact();
                Fact newFact = tool.combine(messages, vertex.getValue());
                boolean canPropagate = tool.propagate(oldFact, newFact);
                int vertexId = vertex.getId().get();
                if(canPropagate){
                    vertex.getValue().setFact(newFact);
                    msg.setVertexID(vertex.getId());
                    msg.setPredID(vertexId);
                    if(((ReachState)newFact).isPCEmpty()){
                        msg.setMsgType(new IntWritable(1));
                    } else {
                        msg.setMsgType(new IntWritable(2));
                    }
                    for(Edge<IntWritable, IntWritable> edge : vertex.getEdges()){
                        sendMessage(edge.getTargetVertexId(), msg);
                    }
                }
            }
            vertex.voteToHalt();
        }
    }
}