package reach_data;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachMsg implements Writable {

    public Integer vertexID;
    public boolean predMsg;
    public boolean msgType;

    public ReachMsg() {
        vertexID = null;
        predMsg = false;
        msgType = false;
    }

    public void setVertexID(int vertexID) {
        this.vertexID = vertexID;
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

    public int getVertexID() {
        return vertexID;
    }

    public Boolean getMsgType() {
        return msgType;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        /// vertexID.write(dataOutput);
        out.writeInt(vertexID);
        out.writeBoolean(predMsg);
        out.writeBoolean(msgType);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        /// vertexID.readFields(dataInput);
        vertexID = in.readInt();
        predMsg = in.readBoolean();
        msgType = in.readBoolean();
    }
}
