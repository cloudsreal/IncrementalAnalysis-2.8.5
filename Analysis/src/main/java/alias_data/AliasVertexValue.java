package alias_data;

import alias_stmt.AStmt;
import data.CommonWrite;
import data.VertexValue;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

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


  public AliasVertexValue(String text, String gsStr, String pegStr, boolean propagate){
    Scanner sc = new Scanner(text);
    stmts = new AliasStmts(sc);
    graphStore = null;
    fact = null;
    this.propagate = propagate;
    setGraphStore(gsStr);
    setPegraph(pegStr);
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

  public void setGraphStore(String text) {
    /// this.graphStore = graphStore;
    CommonWrite.method2("<--->setGraphStore");
    Pattern SEPARATOR = Pattern.compile("\t");
    String[] tokens = SEPARATOR.split(text);

    int i = 0;
    if(tokens[i].equals("1")){
      graphStore = new MapWritable();
      i++;
      
      int size = Integer.parseInt(tokens[i]);
      i++;
      /// for (Map.Entry<Writable, Writable> item: graphStore.entrySet()){
      ///   strBuilder.append("k").append(item.getKey()).append("-");
      ///   strBuilder.append(((NodeTuple)item.getValue()).nodetupleToString());
      /// }
      while(size > 0){
        int pre_id = Integer.parseInt(tokens[i]);
        i++;

        NodeTuple nodeTuple = new NodeTuple();
        // nodeTuple.setStmtList(message.getStmtList());
        // nodeTuple.setPegraph((Pegraph)message.getFact());
        nodeTuple.setStmtList(new AliasStmts(tokens, i));
        i = i + 1 + nodeTuple.getStmt().getSize();
        if(tokens[i].equals("1")){
          nodeTuple.setPegraph(new Pegraph(tokens, i));
          i = i + 2 + nodeTuple.getPegraph().getSize();
        }
        else{
          i = i+1;
        }
        /// CommonWrite.method2("pre-id:" + String.valueOf(pre_id) + "\tPEG-size:" +  String.valueOf(nodeTuple.getPegraph().getGraph().size()));
        this.graphStore.put(new IntWritable(pre_id), nodeTuple);
        size--;
      } 
    }
  }



  public void setPegraph(String text) {
    CommonWrite.method2("<--->setPegraph");
    Pattern SEPARATOR = Pattern.compile("\t");
    String[] tokens = SEPARATOR.split(text);

    int i = 0;
    if(tokens[0].equals("1")){
      fact = new Pegraph(tokens, i);
    }
  }

  // public String stmtstoString() {
  //   /// return String.valueOf(this.size());

  //   StringBuilder strBuilder = new StringBuilder();
  //   strBuilder.append(((AliasStmts)stmts).toString());
  //   return strBuilder.toString();
  // }

  // public String gstoretoString() {
  //   StringBuilder strBuilder = new StringBuilder();
  //   if(graphStore != null){
  //     /// strBuilder.append("GS1\t").append(graphStore.size()).append("\t");
  //     /// for (Map.Entry<Writable, Writable> item: graphStore.entrySet()){
  //     ///   strBuilder.append("k").append(item.getKey()).append("-");
  //     ///   strBuilder.append(((NodeTuple)item.getValue()).nodetupleToString());
  //     /// }

  //     strBuilder.append("GS:\t1\t").append(graphStore.size()).append("\t");
  //     for (Map.Entry<Writable, Writable> item: graphStore.entrySet()){
  //        strBuilder.append(item.getKey()).append("\t");
  //        strBuilder.append(((NodeTuple)item.getValue()).nodetupleToString());
  //     }
  //   }
  //   else{
  //     /// strBuilder.append("GS0\t");
  //     strBuilder.append("GS:\t0\t");
  //   }
  //   /// Wait for implementation
  //   return strBuilder.toString();
  // }


  // public void stringToPeg(String[] token) {
  //   StringBuilder strBuilder = new StringBuilder();
  //   /// Wait for implementation
  //   if(fact != null){
  //     strBuilder.append("1\t");
  //     strBuilder.append(((Pegraph)fact).toString()).append("\t");
  //   }
  //   else{
  //     strBuilder.append("0");
  //   }
  //   /// return strBuilder.toString();
  // }

  @Override
  public void setNewFact() {
    fact = new Pegraph();
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