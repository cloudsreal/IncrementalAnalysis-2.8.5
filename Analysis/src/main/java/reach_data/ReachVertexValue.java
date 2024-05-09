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

public class ReachVertexValue extends VertexValue {
    private char vertexType;
    private String stmtLine;
    private HashSet<Integer> preds;

    public ReachVertexValue() {
        stmts = null;
        fact = new ReachState();
        vertexType = ' ';
    }

    public ReachVertexValue(String type){
        stmtLine = null;
        stmts = null;
        fact = new ReachState();
        preds = new HashSet<>();
        if ("a".equalsIgnoreCase(type)) {           // added node
            vertexType = 'a';
        } else if ("d".equalsIgnoreCase(type)) {    // deleted node
            vertexType = 'd';
        } else if ("c".equalsIgnoreCase(type)) {    // changed node
            vertexType = 'c';
        } else {
            vertexType = 'u';
        }
    }

    public void setStmtLine(String stmtLine) {
        this.stmtLine = stmtLine;
    }

    public void addPred(int pred) {
        preds.add(pred);
    }

    public HashSet<Integer> getPreds() {
        return preds;
    }

    public String getStmtLine() {
        return stmtLine;
    }

    public void setVertexType(char vertexType) {
        this.vertexType = vertexType;
    }

    public char getVertexType(){
        return vertexType;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        Text.writeString(out, stmtLine);
        out.writeChar(vertexType);
        if (fact != null) {
            out.writeByte(1);
            fact.write(out);
        } else {
            out.writeByte(0);
        }
        out.writeInt(preds.size());
        for (Integer pred : preds) {
            out.writeInt(pred);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        stmtLine = Text.readString(in);
        vertexType = in.readChar();
        if (in.readByte() == 1) {
            if (fact == null) {
                fact = new ReachState();
            }
            fact.readFields(in);
        }
        int size = in.readInt();
        preds = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            preds.add(in.readInt());
        }
    }

}
