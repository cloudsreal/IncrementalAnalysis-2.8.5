package data;

import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class VertexValue implements Writable {
    protected StmtList stmts;
    protected Fact fact;
    protected Tool tool;

    public VertexValue() {
        stmts = null;
        fact = null;
        tool = null;
    }
    
    public Fact getFact() {
        return fact;
    }

    public Tool getTool() {
        return tool;
    }

    public void setFact(Fact fact) {
        this.fact = fact;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public StmtList getStmtList() {
        return stmts;
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
