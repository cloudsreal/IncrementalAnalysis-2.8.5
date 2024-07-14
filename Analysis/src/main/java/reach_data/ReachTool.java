package reach_data;

public class ReachTool {

    public ReachTool(){}

    public boolean hasPCMsg(Iterable<ReachMsg> messages, ReachInfo reach_info) {
        for (ReachMsg item : messages) {
            if(!item.isPredMsg()){
                boolean messageType = item.getMsgType();
                reach_info.setPA(true); // must be PA/PC
                if(messageType) {
                    reach_info.setPC(true);  // must be PC
                    return true;
                }
            } else{
                reach_info.setEntry(true); // might be PU
            }
        }
        return false;
    }

    public boolean propagate(ReachVertexValue vertexValue, boolean pa_flag, boolean pc_flag){
        // unchanged nodes -> UN or PA/PU -> PC
        return (!vertexValue.isPA() && !vertexValue.isPC() && pa_flag) || (!vertexValue.isPC() && pc_flag);
    }
}
