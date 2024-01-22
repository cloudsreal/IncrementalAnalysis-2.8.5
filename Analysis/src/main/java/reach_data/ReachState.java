package reach_data;

import data.Fact;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.util.*;

public class ReachState extends Fact {
    public boolean flag;
    public Set<IntWritable> PC;

    public ReachState() {
        flag = false;
        PC = new HashSet<>();
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

}