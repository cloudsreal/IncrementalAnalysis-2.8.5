package reach_analysis;

import analysis.Analysis;
import data.*;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.python.antlr.op.In;
import reach_data.*;
import org.apache.giraph.graph.BasicComputation;

import java.util.HashSet;

public class ReachAnalysis extends BasicComputation<IntWritable, ReachVertexValue, ReachEdgeValue, ReachMsg> {
	public ReachTool tool = null;
	public Fact fact = null;
	public ReachMsg msg = null;

	public void setAnalysisConf() {
		tool = new ReachTool();
		fact = new ReachState();
		msg = new ReachMsg();
	}

	@Override
	public void compute(Vertex<IntWritable, ReachVertexValue, ReachEdgeValue> vertex, Iterable<ReachMsg> messages) {
		setAnalysisConf();
		if (getSuperstep() == 0) {
			vertex.getValue().setFact(new ReachState());
			int vertexId = vertex.getId().get();
			char vertexType = vertex.getValue().getVertexType();
			final SetWritable entry = getBroadcast("entry");
			msg.setVertexID(vertexId);
			// @zyj: 1.1 deleted node: send msg to itself
			if (vertexType == 'd') {
				sendMessage(vertex.getId(), msg);
			} else {
				// @szw: edge的类型有三种:
				// 1) 为null表示为u，
				// 2~3) 不为null时, 为true表示'a'; false表示'd';
				for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
					ReachEdgeValue edgeType = edge.getValue();
					// @zyj: 1.2 unchanged edges or added edges
					if (!edgeType.isFlag() || edgeType.isAdded()) {
						sendMessage(edge.getTargetVertexId(), msg);
					}
				}
				// @zyj: 2.1 entry: send msg to itself
				if (entry.getValues().contains(vertexId)) {
					sendMessage(vertex.getId(), msg);
				}
			}
			vertex.voteToHalt();
		} else if (getSuperstep() == 1) {
			// @szw : sub new cfg中的那些既是UN又是entry的node，需要把自己的entry flag置为true
			ReachState reachState = new ReachState();
			char vertexType = vertex.getValue().getVertexType();
			int vertexId = vertex.getId().get();
			// @zyj: flag represents UN
			if (vertexType != 'u') {
				reachState.setFlag(true);
			}
			for (ReachMsg msg : messages) {
				int predID = msg.getVertexID();
				if (predID != vertexId) {
					vertex.getValue().addPred(msg.getVertexID());
				}
			}
			msg.setVertexID(vertexId);
			if(vertexType != 'u') {
				msg.setPredMsg(true);
				HashSet<Integer> preds = vertex.getValue().getPreds();
				for(Integer predID : preds) {
					sendMessage(new IntWritable(predID), msg);
				}
				vertex.getValue().clearPreds();
			}
			msg.setPredMsg(false);
			for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
				ReachEdgeValue edgeType = edge.getValue();
				if (vertexType == 'u' && !edgeType.isFlag())
					continue;
				msg.setVertexID(vertexId);
				if (vertexType == 'a') { // PA: dataflow fact from added node
					msg.setMsgType(false);
				} else if (vertexType == 'd' || vertexType == 'c') { // PC: dataflow fact from deleted edge or changed node
					reachState.setPC(true);
					msg.setMsgType(true);
				} else {
					if (edgeType.isAdded()){// PA: dataflow fact from added edge
						msg.setMsgType(false);
					} else {// PC: dataflow fact from deleted edge
						msg.setMsgType(true);
					}
				}
				sendMessage(edge.getTargetVertexId(), msg);
			}
			vertex.getValue().setFact(reachState);
			vertex.voteToHalt();
		} else {
			/// if (beActive(messages, vertex.getValue()))
			{
				int vertexId = vertex.getId().get();
				Fact oldFact = vertex.getValue().getFact();
				Fact newFact = ((ReachTool)tool).combine(messages, vertex); // collect the changed type of dataflow fact from the// updated predecessors
				boolean canPropagate = tool.propagate(oldFact, newFact);
				vertex.getValue().setFact(newFact);
				if (canPropagate) {
					msg.setVertexID(vertexId);
					if(!((ReachState)oldFact).isFlag() && ((ReachState)newFact).isFlag()){
						HashSet<Integer> preds = vertex.getValue().getPreds();
						for(Integer pred : preds){
							msg.setPredMsg(true);
							sendMessage(new IntWritable(pred), msg);
						}
						vertex.getValue().clearPreds();
					}
					// @szw: msg的类型有三种:
					// 1) 为null表示向前继发的message，不需要predid，
					// 2~3) 不为null时, 为true表示'a'; false表示'c';
					// 此外，节点向pred发送消息时，只需要发送一次即可知道是不是UN, 然后就可以把自己的preds置为null了，后续用不到
					// 因为，若后续pred的PA&PC = false, 说明需要修改为UN，自然不是entry
					msg.setPredMsg(false);
                    msg.setMsgType(((ReachState) newFact).isPC());
					for (Edge<IntWritable, ReachEdgeValue> edge : vertex.getEdges()) {
						sendMessage(edge.getTargetVertexId(), msg);
					}
				}
			}
			vertex.voteToHalt();
		}
	}
}