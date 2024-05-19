package pre_data;

import org.apache.hadoop.io.Text;
/// import org.python.antlr.op.In;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import org.apache.hadoop.io.Writable;

public class PreVertexValue implements Writable {

    private boolean updated = false;
    private boolean pc_flag = false;
    private boolean sub_flag = false; // sub_flag represent if a node is in sub_flag-CFG
    private String stmtLine = null;;

    public PreVertexValue() {
        // stmtLine = null;
        // updated = false;
        // sub_flag = false;
        // pc_flag = false;
    }

    public PreVertexValue(String stmt){
        stmtLine = stmt;
        // updated = false;
        // pc_flag = false;
        // sub_flag = false;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public void setSub_flag(boolean sub_flag) {
        this.sub_flag = sub_flag;
    }

    public void setPc_flag(boolean pc_flag){
        this.pc_flag = pc_flag;
    }

    public String getStmtLine() {
        return stmtLine;
    }

    public boolean isSub(){
        return sub_flag;
    }

    public boolean isUpdated(){
        return this.updated;
    }

    public boolean isPC() {
        return pc_flag;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(updated);
        out.writeBoolean(pc_flag);
        out.writeBoolean(sub_flag);
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
        updated = in.readBoolean();
        pc_flag = in.readBoolean();
        sub_flag = in.readBoolean();
        if(in.readBoolean()){
            stmtLine = Text.readString(in);
        }
        else{
            stmtLine = null;
        }
       
    }

}
