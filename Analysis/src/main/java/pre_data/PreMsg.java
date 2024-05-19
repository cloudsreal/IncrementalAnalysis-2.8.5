package pre_data;

import data.Msg;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PreMsg implements Writable {

    private boolean flag;

    public PreMsg(){
        flag = true;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeBoolean(flag);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        flag = dataInput.readBoolean();
    }
}
