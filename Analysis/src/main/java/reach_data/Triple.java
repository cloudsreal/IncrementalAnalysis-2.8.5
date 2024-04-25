package reach_data;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.python.antlr.op.In;

public class Triple{
    private final int sourceId;
    private final int targetId;
    private final char edgeType;

    public Triple(int sourceId, int targetId, String edgeType) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        if ("a".equalsIgnoreCase(edgeType)) {           // added edge
            this.edgeType = 'a';
        } else if ("d".equalsIgnoreCase(edgeType)) {    // deleted edge
            this.edgeType = 'd';
        } else {
            this.edgeType = 'u';
        }
    }

    public Triple(int sourceId, int targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.edgeType = 'u';
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getTargetId() {
        return targetId;
    }

    public char getEdgeType() {
        return edgeType;
    }
}
