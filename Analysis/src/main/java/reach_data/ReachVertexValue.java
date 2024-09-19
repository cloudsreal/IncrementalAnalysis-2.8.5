package reach_data;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachVertexValue implements Writable {
    /*
     *  @ zyj and szw :
     *   each node can know if it is entry by checking whether its incoming edges exist
     */
    private boolean entry_flag = false;
    /* @ zyj and szw :
     *   We reprensent the influenced node type of CFG nodes in a compact form as follows:
     *   1) pa_flag = false, pc_flag = false: PU.
     *       Default Value
     *   2) pa_flag = true, pc_flag = false: PA.
     *       Influenced by only added cases, known by reachability analysis
     *   3) pa_flag = true, pc_flag = true : PC.
     *       Influenced by at least deleted or changed cases, known by reachability analysis
     *   4) pa_flag = false, pc_flag = true: deleted nodes.
     *       Known at the beginning
     *
     *   SUMMARY: PA's and PU's old fact can be reused.
     */
    private boolean pa_flag = false;
    private boolean pc_flag = false;
    private String stmt_str = null;

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

    public void setStmt(String stmt_string) {
        this.stmt_str = stmt_string;
    }

    public String getStmt(){
        return stmt_str;
    }


    public void setValues(boolean entry_flag, boolean pa_flag, boolean pc_flag) {
        this.entry_flag = entry_flag;
        this.pa_flag = pa_flag;
        this.pc_flag = pc_flag;
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

    public boolean getPA(){
        return pa_flag;
    }

    public boolean getPC(){
        return pc_flag;
    }

    public boolean isDel(){
        return !pa_flag && pc_flag;
    }

    public boolean isPU(){
        return !pa_flag && !pc_flag;
    }

    public boolean getEntry(){
        return entry_flag;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(pa_flag);
        out.writeBoolean(pc_flag);
        out.writeBoolean(entry_flag);
        if(stmt_str != null){
            out.writeBoolean(true);
            // out.writeInt(stmt_str.length()); 
            // out.writeChars(stmt_str);
            out.writeBytes(stmt_str+"\n");
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
            // int len = in.readInt();
            // StringBuilder stmt_build = new StringBuilder();
            // while(len > 0){
            //     stmt_build.append(in.readChar());
            //     len--;
            // }
            // stmt_str = stmt_build.toString();
            stmt_str = in.readLine();
        }
    }

}
