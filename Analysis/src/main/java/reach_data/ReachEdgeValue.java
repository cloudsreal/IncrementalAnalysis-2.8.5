package reach_data;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachEdgeValue implements Writable {

    boolean flag;
    boolean type;

    public ReachEdgeValue(){
        flag = false;
        type = false;
    }

    public ReachEdgeValue(boolean flag, boolean type){
        this.flag = flag;
        this.type = type;
    }

    public boolean isFlag(){
        return flag;
    }

    public boolean isAdded(){
        return type;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(flag);
        out.writeBoolean(type);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        flag = in.readBoolean();
        type = in.readBoolean();
    }
}
