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
    public HashSet<Integer> PC; // set of PC nodes

    public ReachState() {
        this.flag = false;
        this.PC = new HashSet<>();
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public HashSet<Integer> getPC(){
        return this.PC;
    }
    public void merge(Fact fact){
    }

    public boolean isPCEmpty(){
        return PC.isEmpty();
    }

    public void addPC(Integer vertex){
        this.PC.add(vertex);
    }

    public Fact getNew(){
        ReachState state = new ReachState();
        state.setFlag(this.flag);
        state.PC = new HashSet<>(this.PC);
        return state;
    }

    public boolean consistent(Fact oldfact){
        return false;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(flag);
        if (flag) {
            out.writeInt(PC.size());
            for (Integer vertex : PC) {
                out.writeInt(vertex);
            }
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        flag = in.readBoolean();
        if (flag) {
            int pcSize = in.readInt();
            PC.clear();
            for (int i = 0; i < pcSize; i++) {
                PC.add(in.readInt());
            }
        }
    }

}