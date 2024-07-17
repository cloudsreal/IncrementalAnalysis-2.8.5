package reach_analysis;

/// import data.CommonWrite;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.edge.EdgeFactory;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import reach_data.*;

import java.io.IOException;


public class ReachAnalysis extends BasicComputation<IntWritable, ReachVertexValue, ReachEdgeValue, ReachMsg> {
    public ReachTool tool = null;
    public ReachMsg msg = null;

    public void setAnalysisConf() {
        tool = new ReachTool();
        msg = new ReachMsg();
    }

    public void compute(Vertex<IntWritable, ReachVertexValue, ReachEdgeValue> vertex, Iterable<ReachMsg> messages) throws IOException {
        setAnalysisConf();
        /*
         *   Step 0: Adding incoming edge
         */
        if(getSuperstep() == 0) {
            IntWritable vertexId = vertex.getId();
            for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
                if(!edge.getValue().isDeleted()) {
                    int targetVertexId = edge.getTargetVertexId().get();
                    addEdgeRequest(new IntWritable(targetVertexId), EdgeFactory.create(vertexId, new ReachEdgeValue(false, true)));
                }
            }
        } else if (getSuperstep() == 1) {
            /*
             *   Step 1: Initialization of Reachability Analysis :
             *       changed type of dataflow facts start from the changed nodes and edges
             */

            ReachVertexValue vertexValue = vertex.getValue();
            boolean entry_flag = false;
            if(vertexValue.getPA() || vertexValue.getPC())
                entry_flag = true; // every PA/PC can be entry

            for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {

                ReachEdgeValue edgeType = edge.getValue();

                if (edgeType.isIn()){
                    if(vertexValue.getPA() || vertexValue.getPC()){
                        entry_flag = false; // if PA/PC has incoming edges, it cannot be entry
                        msg.setPredMsg(true);
                        sendMessage(edge.getTargetVertexId(), msg); // send PredMsg to its pred
                    }
                } else {
                    if(vertexValue.isPU() && !edgeType.isFlag())
                        continue; // unchanged node and unchanged edge

                    msg.setPredMsg(false);
                    if(vertexValue.getPA() && !vertexValue.getPC()){
                        msg.setMsgType(false); // changed type of dataflow fact from added node to its succs
                    } else if (vertexValue.getPC()) {
                        msg.setMsgType(true); // changed type of dataflow fact from deleted/changed node to its succs
                    } else {
                        msg.setMsgType(!edgeType.isAdded()); // changed type of dataflow fact according to edge type
                    }
                    sendMessage(edge.getTargetVertexId(), msg);

                    if(edgeType.isDeleted())
                        vertex.removeEdges(edge.getTargetVertexId()); // Remove all deleted edges
                }
            }
            vertex.getValue().setEntry(entry_flag);
            vertex.voteToHalt();
        } else {
            /*
             *   Step 2-n: Reachability Analysis
             */

            if(vertex.getValue().getPC()){
                // skip changed and deleted nodes, as they have already set entry_flag and no need to propagate vertex type again
                vertex.voteToHalt();
                return;
            }

            ReachVertexValue vertexValue = vertex.getValue();
            ReachInfo reach_info = new ReachInfo(vertexValue);
            tool.combine(messages, reach_info);
            boolean canPropagate = tool.propagate(vertexValue, reach_info);

            if (canPropagate) {

                boolean entry_flag = false;

                // notify its PU for just once
                if(vertex.getValue().isPU()){
                    entry_flag = true;
                    msg.setPredMsg(true);
                    for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
                        ReachEdgeValue edgeType = edge.getValue();
                        if(edgeType.isIn()){
                            sendMessage(edge.getTargetVertexId(), msg);
                            entry_flag = false;
                        }
                    }
                }

                // notify its succs for updating the changed type of dataflow fact
                vertex.getValue().setValues(entry_flag, reach_info.getPA(), reach_info.getPC());
                msg.setPredMsg(false);
                msg.setMsgType(reach_info.getPC());
                for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
                    ReachEdgeValue edgeType = edge.getValue();
                    if(!edgeType.isIn()) {
//                        CommonWrite.method2(getSuperstep() + ": " + vertex.getId().get() + " " + edge.getTargetVertexId().toString() + " C");
                        sendMessage(edge.getTargetVertexId(), msg);
                    }
                }
            }
            else {
                if(vertex.getValue().isPU() && reach_info.getEntry())
                    vertex.getValue().setEntry(true);
            }
            vertex.voteToHalt();
        }

    }
}