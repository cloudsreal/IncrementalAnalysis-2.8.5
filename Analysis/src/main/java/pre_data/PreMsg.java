package pre_data;

import data.Msg;
import org.apache.hadoop.io.IntWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PreMsg extends Msg {
    public IntWritable predID;

    public PreMsg(){
        vertexID = new IntWritable();
        fact = null;
        predID = new IntWritable();
    }

    public void setPredID(int predID) {
        this.predID = new IntWritable(predID);
    }

    public IntWritable getPredID(){
        return predID;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        vertexID.write(dataOutput);
        predID.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        vertexID.readFields(dataInput);
        predID.readFields(dataInput);
    }
}
