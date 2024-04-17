package reach_data;

import data.Msg;
import org.apache.hadoop.io.IntWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachMsg extends Msg {

    public Integer predID;
    public boolean msgType;
    public ReachMsg(){
        vertexID = new IntWritable();
        fact = null;
        predID = null;
        msgType = false;
    }

    public void setPredID(int predID) {
        this.predID = predID;
    }

    public void setMsgType(boolean msgType) {
        this.msgType = msgType;
    }

    public int getPredID(){
        return predID;
    }

    public boolean getMsgType() {
        return msgType;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        vertexID.write(dataOutput);
        dataOutput.writeInt(predID);
        dataOutput.writeBoolean(msgType);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        vertexID.readFields(dataInput);
        predID = dataInput.readInt();
        msgType = dataInput.readBoolean();
    }
}
