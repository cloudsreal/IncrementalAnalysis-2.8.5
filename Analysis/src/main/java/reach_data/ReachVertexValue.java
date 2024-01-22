package reach_data;

import data.VertexValue;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachVertexValue extends VertexValue {
    private Text vertexType;

    public ReachVertexValue() {
        stmts = null;
        fact = new ReachState();
        vertexType = new Text();
    }

    public ReachVertexValue(String type){
        stmts = null;
        fact = new ReachState();
        vertexType = new Text(type);
    }

    public void setVertexType(Text vertexType) {
        this.vertexType = vertexType;
    }

    public Text getVertexType(){
        return vertexType;
    }
}
