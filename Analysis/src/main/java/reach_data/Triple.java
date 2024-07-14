package reach_data;

public class Triple{
    private final int sourceId;
    private final int targetId;
    /*
    isNew = true, isAdded = true: added edges
    isNew = true, isAdded = false: deleted edges
    isNew = false, isAdded = false: old edges
    */
    private boolean isNew;
    private boolean isAdded;

    public Triple(int sourceId, int targetId, char edgeType) {
        this.sourceId = sourceId;
        this.targetId = targetId;
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

    public boolean isNew() {
        return isNew;
    }

    public boolean isAdded() {
        return isAdded;
    }
}
