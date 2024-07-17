package reach_data;

public class ReachTool {

    public ReachTool(){}

    public void combine(Iterable<ReachMsg> messages, ReachInfo reach_info) {
        for (ReachMsg item : messages) {
            if(!item.isPredMsg()){
                boolean messageType = item.getMsgType();
                reach_info.setPA(true); // must be PA/PC
                if(messageType) {
                    reach_info.setPC(true);  // must be PC
                    return;
                }
            } else{
                reach_info.setEntry(true); // might be PU
            }
        }
    }

    public boolean propagate(ReachVertexValue vertexValue, ReachInfo reachInfo){
        // cases :
        //  1)  PU -> PA/PC
        //  2)  PA -> PC
        return  !(vertexValue.getPA() == reachInfo.getPA() && vertexValue.getPC() == reachInfo.getPC());
    }
}
