package alias_stmt;

import incre_data.Stmt;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class AStmt extends Stmt<TYPE> implements Writable{

  public String toString()
  {
      StringBuilder out = new StringBuilder();
      out.append("(");
      toString_sub(out);
      out.append(")");
      return out.toString();
  }

  public String to_string()
  {
      StringBuilder out = new StringBuilder();
      out.append("(");
      toString_sub(out);
      out.append(")");
      return out.toString();
  }


  public int getSize(){
    return 0;
  }

  public void readString(String[] token, int idx) {
    // Wait for instance implementation
  }

  public void toString_sub(StringBuilder str){}

  public AStmt decopy(){
    return new AStmt(); 
  }

  public void write(DataOutput out) throws IOException {
    // Wait for instance implementation
  }
  public void readFields(DataInput in) throws IOException {
    // Wait for instance implementation
  }
}
