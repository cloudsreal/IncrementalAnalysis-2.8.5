package reach_data;

import data.Fact;
import org.apache.hadoop.io.IntWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class ReachState extends Fact {
    public boolean flag;
    public Set<IntWritable> PC;

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

    public Set<IntWritable> getPC(){
        return PC;
    }
    public void merge(Fact fact){
    }

    public boolean isPCEmpty(){
        return PC.isEmpty();
    }

    public void addPC(IntWritable vertex){
        this.flag = true;
        if (!this.PC.contains(vertex)) {
            this.PC.add(vertex);
        }
    }

    public Fact getNew(){
        ReachState state = new ReachState();
        state.setFlag(this.flag);
        state.PC.addAll(this.PC);
        return state;
    }

    public boolean consistent(Fact oldfact){
        ReachState oldState = (ReachState)oldfact;
        if(!oldState.flag && this.flag){
            return false;
        }
        for(IntWritable vertex : this.PC){
            if(!oldState.PC.contains(vertex)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(flag);
        if (flag) {
            out.writeInt(PC.size());
            for (IntWritable vertex : PC) {
                out.writeInt(vertex.get());
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
                PC.add(new IntWritable(in.readInt()));
            }
        }
    }

}