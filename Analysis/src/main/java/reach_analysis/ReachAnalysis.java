package reach_analysis;

/// import data.CommonWrite;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.edge.EdgeFactory;
import org.apache.giraph.edge.EdgeStoreFactory;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.python.antlr.op.In;
import reach_data.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// public class ReachAnalysis extends BasicComputation<IntWritable, ReachVertexValue, ReachEdgeValue, ReachMsg> {
//     public ReachTool tool = null;
//     public ReachMsg msg = null;

//     public void setAnalysisConf() {
//         tool = new ReachTool();
//         msg = new ReachMsg();
//     }

//     public void compute(Vertex<IntWritable, ReachVertexValue, ReachEdgeValue> vertex, Iterable<ReachMsg> messages) {
//         setAnalysisConf();
//         if (getSuperstep() == 0){
//             ReachVertexValue vertexValue = vertex.getValue();
//             for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
//                 ReachEdgeValue edgeType = edge.getValue();

//                 // unchanged node and unchanged edge
//                 if(!vertexValue.isPA() && !vertexValue.isPC() && !edgeType.isFlag()) continue;

//                 if(vertexValue.isPA() && !vertexValue.isPC()){
//                     // dataflow fact from added node
//                     msg.setMsgType(false);
//                 } else if (vertexValue.isPC()) {
//                     // dataflow fact from deleted edge or changed node
//                     msg.setMsgType(true);
//                 } else {
//                     msg.setMsgType(!edgeType.isAdded());
//                 }

//                 sendMessage(edge.getTargetVertexId(), msg);

//                 if(edgeType.isFlag() && !edgeType.isAdded()){
//                     // Remove the deleted edges
//                     vertex.removeEdges(edge.getTargetVertexId());
//                 }
//             }
//             vertex.voteToHalt();
//         } else {
//             ReachVertexValue vertexValue = vertex.getValue();
//             boolean pa_flag = true; // UN
//             boolean pc_flag = tool.hasPCMsg(messages);
//             boolean canPropagate = tool.propagate(vertexValue, pa_flag, pc_flag);
//             if (canPropagate) {
//                 vertex.getValue().setPA(pa_flag);
//                 vertex.getValue().setPC(pc_flag);
//                 msg.setMsgType(pc_flag);
//                 for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
//                     sendMessage(edge.getTargetVertexId(), msg);
//                 }
//             }
//             vertex.voteToHalt();
//         }
//     }
// }


public class ReachAnalysis extends BasicComputation<IntWritable, ReachVertexValue, ReachEdgeValue, ReachMsg> {
    public ReachTool tool = null;
    public ReachMsg msg = null;
    /// public ReachMsg msg = null;

    public void setAnalysisConf() {
        tool = new ReachTool();
        msg = new ReachMsg();
    }

    public void compute(Vertex<IntWritable, ReachVertexValue, ReachEdgeValue> vertex, Iterable<ReachMsg> messages) throws IOException {
        setAnalysisConf();
        if(getSuperstep() == 0) {
            IntWritable vertexId = vertex.getId();
            for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
                if(!edge.getValue().isDeleted()) {
                    int targetVertexId = edge.getTargetVertexId().get();
                    addEdgeRequest(new IntWritable(targetVertexId), EdgeFactory.create(vertexId, new ReachEdgeValue(false, true)));
                }
            }
        } else if (getSuperstep() == 1) {
            ReachVertexValue vertexValue = vertex.getValue();
            boolean entry_flag = false;
            if(vertexValue.isPA() || vertexValue.isPC()) entry_flag = true; // every PA/PC can be entry
            for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
                ReachEdgeValue edgeType = edge.getValue();
                if (edgeType.isIn()){
                    if(vertexValue.isPA() || vertexValue.isPC()){
                        entry_flag = false; // if PA/PC has incoming edge, it cannot be entry
                        msg.setPredMsg(true);
                        sendMessage(edge.getTargetVertexId(), msg); // PA/PC will send PredMsg to its pred
                    }
                } else {
                    // unchanged node and unchanged edge
                    if(!vertexValue.isPA() && !vertexValue.isPC() && !edgeType.isFlag()) continue;
                    msg.setPredMsg(false);
                    if(vertexValue.isPA() && !vertexValue.isPC()){
                        // dataflow fact from added node
                        msg.setMsgType(false);
                    } else if (vertexValue.isPC()) {
                        // dataflow fact from deleted edge or changed node
                        msg.setMsgType(true);
                    } else {
                        msg.setMsgType(!edgeType.isAdded());
                    }
                    sendMessage(edge.getTargetVertexId(), msg);

                    if(edgeType.isDeleted()){
                        // Remove the deleted edges
                        vertex.removeEdges(edge.getTargetVertexId());
                    }
                }
            }
            vertex.getValue().setEntry(entry_flag);
            vertex.voteToHalt();
        } else {
            /// ReachVertexValue vertexValue = vertex.getValue();
            /// boolean pa_flag = true; // UN
            ReachInfo reach_info = new ReachInfo();
            boolean pc_flag = tool.hasPCMsg(messages, reach_info);
            boolean pa_flag = reach_info.getPA();
            boolean canPropagate = tool.propagate(vertex.getValue(), pa_flag, pc_flag);
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

                vertex.getValue().setPA(pa_flag);
                vertex.getValue().setPC(pc_flag);
                vertex.getValue().setEntry(entry_flag);
                msg.setPredMsg(false);
                msg.setMsgType(pc_flag);

                for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
                    /// sendMessage(edge.getTargetVertexId(), msg);
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