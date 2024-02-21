package reach_data;

import cache_data.CacheState;
import data.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.util.Set;

public class ReachTool implements Tool<ReachMsg> {

    public ReachTool(){}

    public Fact combine(Iterable<ReachMsg> message, VertexValue vertexValue){
        ReachState old_state = (ReachState) vertexValue.getFact();
        ReachState new_state;
        if(old_state == null) {
            new_state = new ReachState();
        } else {
            new_state = (ReachState) old_state.getNew();
        }
        for (ReachMsg item : message) {
            IntWritable messageType = item.getMsgType();
            if(messageType.get() == 2) {
                new_state.setFlag(true);
                new_state.addPC(item.getPredID());
            } else if(messageType.get() == 1) {
                new_state.setFlag(true);
            }
        }
        return new_state;
    }

    public Fact combine(Set<Fact> predFacts){
        return null;
    }
    public Fact transfer(StmtList stmts, Fact incomingFact){
        return null;
    }
    public boolean propagate(Fact oldFact, Fact newFact){
        ReachState oldState = (ReachState) oldFact;
        ReachState newState = (ReachState) newFact;
        if(oldFact == null) {
            return true;
        } else {
            return !newState.consistent(oldState);
        }
    }
}
