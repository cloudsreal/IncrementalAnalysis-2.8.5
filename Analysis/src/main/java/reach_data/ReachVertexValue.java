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

public class ReachVertexValue extends VertexValue {
    private char vertexType;

    public ReachVertexValue() {
        stmts = null;
        fact = new ReachState();
        vertexType = ' ';
    }

    public ReachVertexValue(String type){
        stmts = null;
        fact = new ReachState();
        if ("a".equalsIgnoreCase(type)) {
            vertexType = 'a';
        } else if ("d".equalsIgnoreCase(type)) {
            vertexType = 'd';
        } else if ("c".equalsIgnoreCase(type)) {
            vertexType = 'c';
        } else {
            vertexType = 'u';
        }
    }

    public void setVertexType(char vertexType) {
        this.vertexType = vertexType;
    }

    public char getVertexType(){
        return vertexType;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeChar(vertexType);
        if (fact != null) {
            out.writeByte(1);
            fact.write(out);
        } else {
            out.writeByte(0);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        vertexType = in.readChar();
        if (in.readByte() == 1) {
            if (fact == null) {
                fact = new ReachState();
            }
            fact.readFields(in);
        }
    }

}
