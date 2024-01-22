package reach_data;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReachEdgeValue implements Writable {
    private Text type;

    public ReachEdgeValue() {
        type = new Text();
    }

    public ReachEdgeValue(String type) {
        this.type = new Text(type);
    }

    public Text getType() {
        return type;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        type.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        type.readFields(dataInput);
    }
}
