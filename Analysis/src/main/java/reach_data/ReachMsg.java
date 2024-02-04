package reach_data;

import cache_data.CacheState;
import data.Msg;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.python.antlr.op.In;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachMsg extends Msg {

    public IntWritable msgType;

    public ReachMsg(){
        vertexID = new IntWritable(0);
        fact = null;
        msgType = new IntWritable(0);
    }

    @Override
    public void setVertexID(IntWritable id) {
        super.setVertexID(id);
    }

    public void setMsgType(IntWritable msgType) {
        this.msgType = msgType;
    }

    public IntWritable getMsgType() {
        return msgType;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        vertexID.write(dataOutput);
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
        if (dataInput.readByte() == 1) {
            msgType = new IntWritable();
            msgType.readFields(dataInput);
        }
    }
}
