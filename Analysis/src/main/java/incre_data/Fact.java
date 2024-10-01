package incre_data;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class Fact implements Writable{
  public abstract void merge(Fact fact);
  public abstract Fact getNew();
  public abstract boolean consistent(Fact fact);

  @Override
  public void write(DataOutput out) throws IOException{

  }

  @Override
  public void readFields(DataInput in) throws IOException{

  }

}