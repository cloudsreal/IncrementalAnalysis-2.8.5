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
    private IntWritable vertexType;

    public ReachVertexValue() {
        stmts = null;
        fact = new ReachState();
        vertexType = new IntWritable(0);
    }

    public ReachVertexValue(String type){
        stmts = null;
        fact = new ReachState();
        if ("a".equalsIgnoreCase(type)) {
            vertexType = new IntWritable(1);
        } else if ("d".equalsIgnoreCase(type)) {
            vertexType = new IntWritable(2);
        } else if ("c".equalsIgnoreCase(type)) {
            vertexType = new IntWritable(3);
        } else {
            vertexType = new IntWritable(0);
        }
    }

    public void setVertexType(IntWritable vertexType) {
        this.vertexType = vertexType;
    }

    public IntWritable getVertexType(){
        return vertexType;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(vertexType.get());
        if (fact != null) {
            out.writeByte(1);
            fact.write(out);
        } else {
            out.writeByte(0);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        vertexType = new IntWritable(in.readInt());
        if (in.readByte() == 1) {
            if (fact == null) {
                fact = new ReachState();
            }
            fact.readFields(in);
        }
    }

}
