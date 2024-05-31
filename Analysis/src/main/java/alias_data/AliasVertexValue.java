package alias_data;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.MapWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import data.*;
import alias_stmt.AStmt;

public class AliasVertexValue extends VertexValue{
  private MapWritable graphStore; // graphStore: MapWritable<IntWritable, NodeTuple>

  public AliasVertexValue(){
    stmts = new AliasStmts();
    fact = null;
    graphStore = null;
  }

  public AliasVertexValue(String text){
    Scanner sc = new Scanner(text);
    stmts = new AliasStmts(sc);
    fact = null;
    graphStore = null;
  }

  public AStmt getStmt() {
    return (AStmt)stmts.getStmts()[0];
  }

  public MapWritable getGraphStore() {
        return graphStore;
  }

  public void setGraphStore(MapWritable graphStore) {
        this.graphStore = graphStore;
  }

  public String stmtstoString() {
    /// return String.valueOf(this.size());

    StringBuilder strBuilder = new StringBuilder();
    strBuilder.append(((AliasStmts)stmts).toString());
    return strBuilder.toString();
  }

  public String gstoretoString() {
    StringBuilder strBuilder = new StringBuilder();
    if(graphStore != null){
      strBuilder.append("GS:1\t").append(graphStore.size()).append("\t");
      for (Map.Entry<Writable, Writable> item: graphStore.entrySet()){
        // strBuilder.append("k").append(item.getKey()).append("-");
        strBuilder.append(item.getKey()).append("\t");
        strBuilder.append(((NodeTuple)item.getValue()).nodetupleToString());
      }
    }
    else{
      strBuilder.append("GS:0\t");
    }
    /// Wait for implementation
    return strBuilder.toString();
  }


  public String pegtoString() {
    StringBuilder strBuilder = new StringBuilder();
    /// Wait for implementation
    if(fact != null){
      strBuilder.append("F:1\t");
      strBuilder.append(((Pegraph)fact).graphtoString()).append("\t");
    }
    else{
      strBuilder.append("F:0");
    }
    return strBuilder.toString();
  }


  @Override
  public void write(DataOutput dataOutput) throws IOException {
    stmts.write(dataOutput);
    if(graphStore != null){
        dataOutput.writeByte(1);
        graphStore.write(dataOutput);
    }
    else{
        dataOutput.writeByte(0);
    }

    if (fact != null) {
      dataOutput.writeByte(1);
      fact.write(dataOutput);
    }
    else {
      dataOutput.writeByte(0);
    }
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    stmts.readFields(dataInput);
    if(dataInput.readByte() == 1){
      if(graphStore == null)
        graphStore = new MapWritable();
      graphStore.readFields(dataInput);
    }

    if (dataInput.readByte() == 1) {
      if (fact == null) {
        fact = new Pegraph();
      }
      fact.readFields(dataInput);
    }
  }

}