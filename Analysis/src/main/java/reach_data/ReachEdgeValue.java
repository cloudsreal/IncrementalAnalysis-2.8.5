package reach_data;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachEdgeValue implements Writable {
    // flag=true, type=true: A
    // flag=true, type=false: D
    // flag=false, type=false: U
    // flag=false, type=true: I (incoming)

    boolean flag = false; // false for unchanged, true for changed
    boolean type = false; // true for added, false for deleted

    public ReachEdgeValue(){
        flag = false;
        type = false;
    }

    public ReachEdgeValue(boolean flag, boolean type){
        this.flag = flag;
        this.type = type;
    }

    public boolean isOld(){
        return !flag && !type;
    }

    public boolean isFlag(){
        return flag; // added or deleted edge
    }

    public boolean isIn(){ // incoming edge
        return !flag && type;
    }

    public boolean isAdded(){
        // return type;
        return flag && type;
    }

    public boolean isDeleted(){
        // return type;
        return flag && !type;
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
