package reach_data;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class Triple {
    private final int sourceId;
    private final int targetId;
    private final String edgeType;

    public Triple(int sourceId, int targetId, String edgeType) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.edgeType = edgeType;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getTargetId() {
        return targetId;
    }

    public String getEdgeType() {
        return edgeType;
    }
}
