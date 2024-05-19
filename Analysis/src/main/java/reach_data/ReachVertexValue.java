package reach_data;

import data.VertexValue;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.python.antlr.op.In;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ReachVertexValue implements Writable {
    private boolean entry_flag = false;
    private boolean pa_flag = false;
    private boolean pc_flag = false;
    private String stmtLine = null;

    public ReachVertexValue() {

    }

    public ReachVertexValue(char type){
        // stmtLine = null;
        // pa_flag = false;
        // pc_flag = false;
        // if ("a".equalsIgnoreCase(type)) {           // added node
        //     pa_flag = true;
        // } else if ("d".equalsIgnoreCase(type)) {    // deleted node
        //     pc_flag = true;
        // } else if ("c".equalsIgnoreCase(type)) {    // changed node
        //     pa_flag = true;
        //     pc_flag = true;
        // }
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

    public void setStmtLine(String stmtLine) {
        this.stmtLine = stmtLine;
    }

    public String getStmtLine() {
        return stmtLine;
    }

    public boolean isPA(){
        return pa_flag;
    }

    public boolean isPC(){
        return pc_flag;
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
        if(stmtLine != null){
            out.writeBoolean(true);
            Text.writeString(out, stmtLine);
        }
        else{
            out.writeBoolean(false);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        pa_flag = in.readBoolean();
        pc_flag = in.readBoolean();
        entry_flag = in.readBoolean();
        if(in.readBoolean()){
            stmtLine = Text.readString(in);
        }
        else{
            stmtLine = null;
        }
    }

}
