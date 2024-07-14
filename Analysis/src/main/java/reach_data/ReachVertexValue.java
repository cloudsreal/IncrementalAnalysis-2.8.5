package reach_data;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachVertexValue implements Writable {
    private boolean entry_flag = false;
    /*
    pa_flag = false, pc_flag = false: PU
    pa_flag = true, pc_flag = false: PA
    pa_flag = true, pc_flag = true: PC
    pa_flag = false, pc_flag = true: deleted nodes
    PA's and PU's old fact can be reused.
    */
    private boolean pa_flag = false;
    private boolean pc_flag = false;

    public ReachVertexValue() {

    }

    public ReachVertexValue(char type){
        if (type == 'A') {           // added node
            pa_flag = true;
        } else if (type == 'D') {    // deleted node
            pc_flag = true;
        } else if (type == 'C') {    // changed node
            pa_flag = true;
            pc_flag = true;
        }
    }

    public void setEntry(boolean entry_flag) {
        this.entry_flag = entry_flag;
    }

    public void setPA(boolean pa_flag) {
        this.pa_flag = pa_flag;
    }

    public void setPC(boolean pc_flag) {
        this.pc_flag = pc_flag;
    }

    public boolean isPA(){
        return pa_flag;
    }

    public boolean isPC(){
        return pc_flag;
    }

    public boolean isDel(){
        return !pa_flag && pc_flag;
    }

    public boolean isPU(){
        return !pa_flag && !pc_flag;
    }

    public boolean isEntry(){
        return entry_flag;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(pa_flag);
        out.writeBoolean(pc_flag);
        out.writeBoolean(entry_flag);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        pa_flag = in.readBoolean();
        pc_flag = in.readBoolean();
        entry_flag = in.readBoolean();
    }

}
