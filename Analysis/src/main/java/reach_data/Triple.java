package reach_data;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.python.antlr.op.In;

public class Triple{
    private final int sourceId;
    private final int targetId;
    private final int edgeType;

    public Triple(int sourceId, int targetId, String edgeType) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        if ("A".equals(edgeType)) {
            this.edgeType = 1;
        } else {
            this.edgeType = 2;
        }
    }

    public Triple(int sourceId, int targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.edgeType = 0;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getTargetId() {
        return targetId;
    }

    public int getEdgeType() {
        return edgeType;
    }
}
