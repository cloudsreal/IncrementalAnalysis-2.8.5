package reach_data;

import data.Msg;
import org.apache.hadoop.io.IntWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachMsg extends Msg {

    public IntWritable predID;
    public IntWritable msgType;
    public ReachMsg(){
        vertexID = new IntWritable();
        fact = null;
        predID = new IntWritable();
        msgType = new IntWritable();
    }

    public void setPredID(int predID) {
        this.predID = new IntWritable(predID);
    }

    public void setMsgType(IntWritable msgType) {
        this.msgType = msgType;
    }

    public IntWritable getPredID(){
        return predID;
    }

    public IntWritable getMsgType() {
        return msgType;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        vertexID.write(dataOutput);
        predID.write(dataOutput);
        if (msgType != null) {
            dataOutput.writeByte(1);
            msgType.write(dataOutput);
        }
        else {
            dataOutput.writeByte(0);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        vertexID.readFields(dataInput);
        predID.readFields(dataInput);
        if (dataInput.readByte() == 1) {
            msgType = new IntWritable();
            msgType.readFields(dataInput);
        }
    }
}
