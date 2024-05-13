package pre_data;

import data.Fact;
import org.apache.hadoop.io.IntWritable;
import org.python.antlr.op.In;
// import reach_data.ReachState;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;

public class PreState extends Fact {
    public HashSet<IntWritable> preds;

    public HashSet<IntWritable> pcs;

    public PreState() {
        this.preds = new HashSet<>();
        this.pcs = new HashSet<>();
    }

    public HashSet<IntWritable> getPreds() {
        return this.preds;
    }

    public HashSet<IntWritable> getPCs() {
        return this.pcs;
    }

    public void addPred(int vertexID) {
        this.preds.add(new IntWritable(vertexID));
    }

    public void addPC(int vertexID) {
        this.pcs.add(new IntWritable(vertexID));
    }

    public void merge(Fact fact) {
    }

    public Fact getNew() {
        PreState state = new PreState();
        state.preds = new HashSet<>(this.preds);
        return state;
    }

    public boolean consistent(Fact oldfact) {
        PreState oldState = (PreState) oldfact;
        for (IntWritable vertex : this.preds) {
            if (!oldState.preds.contains(vertex)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(preds.size());
        out.writeInt(pcs.size());
        for (IntWritable vertex : preds) {
            out.writeInt(vertex.get());
        }
        for (IntWritable vertex : pcs) {
            out.writeInt(vertex.get());
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        int predsSize = in.readInt();
        int pcsSize = in.readInt();
        preds.clear();
        for (int i = 0; i < predsSize; i++) {
            preds.add(new IntWritable(in.readInt()));
        }
        for (int i = 0; i < pcsSize; i++) {
            pcs.add(new IntWritable(in.readInt()));
        }
    }
}
