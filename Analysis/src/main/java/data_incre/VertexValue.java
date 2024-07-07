package data_incre;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class VertexValue implements Writable {
    protected StmtList stmts;
    protected Fact fact;
    protected boolean propagate;
    protected boolean entry;
    protected Tool tool; /// @szw : just for testing

    public VertexValue() {
        stmts = null;
        fact = null;
        propagate = false;
        entry = false;
        tool = null;
    }
    
    public void setFact(Fact fact) {
        this.fact = fact;
    }

    public void setNewFact() {
        // wait for implementation
    }

    public Fact getFact() {
        return fact;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public Tool getTool() {
        return tool;
    }

    public boolean isPropagate() {
        return propagate;
    }

    public void setPropagate(boolean propagate){
        this.propagate = propagate;
    }

    public boolean isEntry() {
        return entry;
    }

    public StmtList getStmtList() {
        return stmts;
    }

    public void setStmts(StmtList stmts) {
        this.stmts = stmts;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        // wait for implementation to serialize vertexvalue under specific dataflow analysis
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        // wait for implementation to deserialize vertexvalue under specific dataflow analysis
    }
    
}
