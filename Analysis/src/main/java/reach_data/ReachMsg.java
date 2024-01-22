package reach_data;

import data.Msg;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class ReachMsg extends Msg {

    public Text msgType;

    public ReachMsg(){
        vertexID = new IntWritable(0);
        fact = null;
        msgType = new Text("");
    }

    @Override
    public void setVertexID(IntWritable id) {
        super.setVertexID(id);
    }

    public void setMsgType(Text msgType) {
        this.msgType = msgType;
    }

    public Text getMsgType() {
        return msgType;
    }

}
