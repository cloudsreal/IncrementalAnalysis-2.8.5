package put_proc;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PutVertexValue implements Writable {
    protected String fact;

    public PutVertexValue() {
        fact = null;
    }

    public PutVertexValue(String fact) {
        this.fact = fact;
    }
    
    public void setFact(String fact) {
        this.fact = fact;
    }

    public String getFact() {
        return fact;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        if(fact != null){
            dataOutput.writeBoolean(true);
            dataOutput.writeBytes(fact+"\n");
        }
        else{
            dataOutput.writeBoolean(false);
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        if(dataInput.readBoolean()){
            fact = dataInput.readLine();
        }
    }
    
}
