package alias_data;

import alias_stmt.AStmt;
import incre_data.VertexValue;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AliasVertexValue extends VertexValue{
  private MapWritable graphStore = null; // graphStore: MapWritable<IntWritable, NodeTuple>

  public AliasVertexValue(){
    stmts = null;
    fact = null;
    graphStore = null;
  }

  public AliasVertexValue(String text){
    Scanner sc = new Scanner(text);
    stmts = new AliasStmts(sc);
    fact = null;
    graphStore = null;
  }


  public AliasVertexValue(String gsStr, String pegStr, boolean entry){
    stmts = null;
    graphStore = null;
    fact = null;
    /// this.propagate = propagate;
//    CommonWrite.method2("GS:\t" + gsStr);
    setGraphStore(gsStr);
    setPegraph(pegStr);
//    CommonWrite.method2("F:\t" + pegtoString());
    this.entry = entry;
  }

  public AliasVertexValue(String gsStr, boolean entry){
    stmts = null;
    graphStore = null;
    fact = null;
//    CommonWrite.method2("GS:\t" + gsStr);
    /// this.propagate = propagate;
    setGraphStore(gsStr);
    this.entry = entry;
  }

  public void setStmts(String text){
    Scanner sc = new Scanner(text);
    stmts = new AliasStmts(sc);
//    CommonWrite.method2( stmtstoString());
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
//    CommonWrite.method2("<--->setGraphStore");
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
          i = i + 1;
        }
        /// CommonWrite.method2("pre-id:" + String.valueOf(pre_id) + "\tPEG-size:" +  String.valueOf(nodeTuple.getPegraph().getGraph().size()));
        this.graphStore.put(new IntWritable(pre_id), nodeTuple);
        size--;
      } 
    }
  }



  public void setPegraph(String text) {
//    CommonWrite.method2("<--->setPegraph");
    Pattern SEPARATOR = Pattern.compile("\t");
    String[] tokens = SEPARATOR.split(text);

    int i = 0;
    if(tokens[0].equals("1")){
      fact = new Pegraph(tokens, i);
    }
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
  public void setStmts(String text, boolean flag){
    Scanner sc = new Scanner(text);
    stmts = new AliasStmts(sc);
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    if (stmts != null) {
      dataOutput.writeByte(1);
      stmts.write(dataOutput);
    }
    else {
      dataOutput.writeByte(0);
    }

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

    dataOutput.writeBoolean(propagate);
    dataOutput.writeBoolean(entry);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    if(dataInput.readByte() == 1){
      if(stmts == null)
        stmts = new AliasStmts();
      stmts.readFields(dataInput);;
    }

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

    propagate = dataInput.readBoolean();
    entry = dataInput.readBoolean();
  }

}