package pre_data;

import data.VertexValue;
import org.python.antlr.op.In;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;

public class PreVertexValue extends VertexValue {

    private boolean exist;
    private boolean flag;
    private HashSet<Integer> PC;

    public PreVertexValue() {
        stmts = null;
        fact = new PreState();
        exist = false;
        flag = false;
        PC = new HashSet<>();
    }

    public PreVertexValue(boolean exist, boolean flag, HashSet<Integer> newPC) {
        stmts = null;
        fact = new PreState();
        this.exist = exist;
        this.flag = flag;
        this.PC = newPC;
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setExist(boolean exist){
        this.exist = exist;
    }

    public void setPC(HashSet<Integer> newPC) {
        if (newPC == null || newPC.isEmpty()) {
            this.PC = new HashSet<>();
        } else {
            this.PC = newPC;
        }
    }

    public void addPC(int newPC) {
        this.PC.add(newPC);
    }

    public HashSet<Integer> getPC(){
        return this.PC;
    }

    public boolean hasPC(int predID){
        return this.PC.contains(predID);
    }

    public boolean isFlag(){
        return this.flag;
    }

    public boolean isExist(){
        return this.exist;
    }
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(exist);
        out.writeBoolean(flag);
        out.writeInt(PC.size());
        for (Integer pc : PC) {
            out.writeInt(pc);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        exist = in.readBoolean();
        flag = in.readBoolean();
        int size = in.readInt();
        PC = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            PC.add(in.readInt());
        }
    }

}
