package reach_data;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.python.antlr.op.In;

public class Triple{
    private final int sourceId;
    private final int targetId;
    private boolean isNew;
    private boolean isAdded;

    public Triple(int sourceId, int targetId, char edgeType) {
        this.sourceId = sourceId;
        this.targetId = targetId;
//        if ("a".equalsIgnoreCase(edgeType)) {           // added edge
//            this.isNew = true;
//            this.isAdded = true;
//        } else if ("d".equalsIgnoreCase(edgeType)) {    // deleted edge
//            this.isNew = true;
//            this.isAdded = false;
//        } else {
//            this.isNew = false;
//            this.isAdded = false;
//        }
        if(edgeType == 'A'){
            this.isNew = true;
            this.isAdded = true;
        } else if(edgeType == 'D'){
            this.isNew = true;
            this.isAdded = false;
        } else {
            this.isNew = false;
            this.isAdded = false;
        }
    }

    public Triple(int sourceId, int targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.isNew = false;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getTargetId() {
        return targetId;
    }

//    public Boolean getEdgeType() {
//        return edgeType;
//    }
    public boolean isNew() {
        return isNew;
    }

    public boolean isAdded() {
        return isAdded;
    }
}
