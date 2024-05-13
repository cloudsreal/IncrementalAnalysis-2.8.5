package reach_data;

import data.Fact;
import org.apache.hadoop.io.IntWritable;
import org.python.antlr.op.In;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class ReachState extends Fact {
    public boolean flag; // UN nodes have true flag
    public boolean pc_flag; // UN nodes have true flag
    public boolean pu_flag;
    // public HashSet<Integer> PC; // set of PC nodes

    public ReachState() {
        this.flag = false;
        /// this.PC = new HashSet<>();
        this.pc_flag = false;
        this.pu_flag = false;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public boolean isPC(){
        return pc_flag;
    }

    public boolean isPU(){
        return pu_flag;
    }

    public void setPU(boolean pu_flag) {
        this.pu_flag = pu_flag;
    }

    public void setPC(boolean pc_flag) {
        this.pc_flag = pc_flag;
    }

    // public HashSet<Integer> getPC() {
    // return this.PC;
    // }

    // public void merge(Fact fact) {
    // }

    public void merge(Fact fact) {
    }

    // public boolean isPCEmpty() {
    // return PC.isEmpty();
    // }

    // public void addPC(Integer vertex) {
    // this.PC.add(vertex);
    // }

    public Fact getNew() {
        ReachState state = new ReachState();
        state.setFlag(this.flag);
        state.setPU(pu_flag);
        state.setPC(pc_flag);
        return state;
    }

    public boolean consistent(Fact oldfact) {
        return false;
    }

     @Override
     public void write(DataOutput out) throws IOException {
        out.writeBoolean(flag);
        out.writeBoolean(pc_flag);
        out.writeBoolean(pu_flag);
     }

     @Override
     public void readFields(DataInput in) throws IOException {
        flag = in.readBoolean();
        pc_flag = in.readBoolean();
        pu_flag = in.readBoolean();
     }

}