package pre_data;

import data.VertexValue;
import org.apache.hadoop.io.Text;
import org.python.antlr.op.In;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;

public class PreVertexValue extends VertexValue {

    private boolean exist;
    private boolean updated;
    private boolean UA;
    private char vertexType;
    private String stmtLine;
    private HashSet<Integer> PC;

    public PreVertexValue() {
        stmts = null;
        fact = new PreState();
        exist = false;
        updated = false;
        UA = false;
        vertexType = ' ';
        PC = new HashSet<>();
    }

    public PreVertexValue(String type){
        stmts = null;
        fact = new PreState();
        exist = true;
        updated = false;
        UA = false;
        PC = new HashSet<>();
        if ("a".equalsIgnoreCase(type)) {           // added node
            vertexType = 'a';
        } else if ("d".equalsIgnoreCase(type)) {    // deleted node
            vertexType = 'd';
            exist = false;
        } else if ("c".equalsIgnoreCase(type)) {    // changed node
            vertexType = 'c';
        } else {
            vertexType = 'u';
        }
    }

    public PreVertexValue(String stmt, String type){
        stmts = null;
        stmtLine = stmt;
        fact = new PreState();
        exist = true;
        updated = false;
        UA = false;
        PC = new HashSet<>();
        if ("a".equalsIgnoreCase(type)) {           // added node
            vertexType = 'a';
        } else if ("d".equalsIgnoreCase(type)) {    // deleted node
            vertexType = 'd';
            exist = false;
        } else if ("c".equalsIgnoreCase(type)) {    // changed node
            vertexType = 'c';
        } else {
            vertexType = 'u';
        }
    }

    public PreVertexValue(boolean exist, boolean updated, HashSet<Integer> newPC) {
        stmts = null;
        fact = new PreState();
        this.exist = exist;
        this.updated = updated;
        this.PC = newPC;
    }

    public void setVertexType(char vertexType) {
        this.vertexType = vertexType;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public void setUA(boolean UA) {
        this.UA = UA;
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

    public String getStmtLine() {
        return stmtLine;
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

    public boolean isPCEmpty(){
        return this.PC.isEmpty();
    }

    public boolean isUpdated(){
        return this.updated;
    }

    public boolean isExist(){
        return this.exist;
    }

    public char getVertexType(){
        return this.vertexType;
    }

    public boolean isUA(){
        return this.UA;
    }
    @Override
    public void write(DataOutput out) throws IOException {
        Text.writeString(out, stmtLine);
        out.writeBoolean(exist);
        out.writeBoolean(updated);
        out.writeBoolean(UA);
        out.writeChar(vertexType);
        out.writeInt(PC.size());
        for (Integer pc : PC) {
            out.writeInt(pc);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        stmtLine = Text.readString(in);
        exist = in.readBoolean();
        updated = in.readBoolean();
        UA = in.readBoolean();
        vertexType = in.readChar();
        int size = in.readInt();
        PC = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            PC.add(in.readInt());
        }
    }

}
