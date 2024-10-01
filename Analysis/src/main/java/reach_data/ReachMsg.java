package reach_data;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachMsg implements Writable {

    // predMsg=true: PA/PC message sent to its preds
    // predMsg=false, msgType=false: PA message sent to succ
    // predMsg=false, msgType=true: PC message sent to succ

    public boolean predMsg;
    public boolean msgType;

    public ReachMsg() {
        predMsg = false;
        msgType = false;
    }

    public ReachMsg(boolean predMsg, boolean msgType) {
        this.predMsg = predMsg;
        this.msgType = msgType;
    }

    public void setMsgType(boolean msgType) {
        this.msgType = msgType;
    }

    public void setPredMsg(boolean predMsg) {
        this.predMsg = predMsg;
    }

    public boolean isPredMsg(){
        return predMsg;
    }


    public Boolean getMsgType() {
        return msgType;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(predMsg);
        out.writeBoolean(msgType);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        predMsg = in.readBoolean();
        msgType = in.readBoolean();
    }
}

