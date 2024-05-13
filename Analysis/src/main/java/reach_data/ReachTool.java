package reach_data;

import data.*;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.util.Set;

public class ReachTool {

  public ReachTool() {
  }

  public Fact combine(Iterable<ReachMsg> message, Vertex<IntWritable, ReachVertexValue, ReachEdgeValue> vertex) {
    ReachVertexValue vertexValue = vertex.getValue();
    ReachState old_state = (ReachState) vertexValue.getFact();
    ReachState new_state;
    if (old_state == null) {
      new_state = new ReachState();
    } else {
      new_state = (ReachState) old_state.getNew();
    }
    // each vertex is in UN if it receives msg.
    for (ReachMsg item : message) {
      if(!item.isPredMsg()){ // msg from pred
        new_state.setFlag(true);
        if(item.getMsgType()){ // msg from PC
          new_state.setPC(true);
          break;
        }
      } else { // msg from succ
        vertex.setEdgeValue(new IntWritable(item.getVertexID()), new ReachEdgeValue(true, true));
        new_state.setPU(true);
      }
    }
    if(new_state.isFlag()){
      new_state.setPU(false);
    }
    return new_state;
  }

  public Fact combine(Set<Fact> predFacts) {
    return null;
  }

  public Fact transfer(StmtList stmts, Fact incomingFact) {
    return null;
  }

  public boolean propagate(Fact oldFact, Fact newFact) {
    ReachState oldState = (ReachState) oldFact;
    ReachState newState = (ReachState) newFact;
    if (oldFact == null) {
      return true;
    } else {
        return (!oldState.isFlag() && newState.isFlag()) || (!oldState.isPC() && newState.isPC());
    }
  }
}