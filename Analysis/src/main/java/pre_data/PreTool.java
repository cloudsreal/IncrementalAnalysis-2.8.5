package pre_data;

import data.Fact;
import data.StmtList;
import data.VertexValue;
import data.Tool;

import java.util.Set;

public class PreTool implements Tool<PreMsg> {

    public PreTool( ){
    }

    public Fact combine(Iterable<PreMsg> message, VertexValue vertexValue){
        PreVertexValue preVertexValue = (PreVertexValue) vertexValue;
        PreState old_state = (PreState) vertexValue.getFact();
        PreState new_state;
        if(old_state == null) {
            new_state = new PreState();
        } else {
            new_state = (PreState) old_state.getNew();
        }
        for (PreMsg item : message) {
            int predID = item.getPredID().get();
            if(!preVertexValue.hasPC(predID)){
                new_state.addPred(predID);
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
        PreState oldState = (PreState) oldFact;
        PreState newState = (PreState) newFact;
        if(oldFact == null) {
            return true;
        } else {
            return !newState.consistent(oldState);
        }
    }

}
