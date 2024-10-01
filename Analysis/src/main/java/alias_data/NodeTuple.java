package alias_data;

import alias_stmt.AStmt;
import incre_data.StmtList;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NodeTuple implements Writable{
  public AliasStmts stmtlist;
  public Pegraph pegraph;

  public NodeTuple() {
    stmtlist = null;
    pegraph = null;
  }

  public AStmt getStmt() {
      return (AStmt)stmtlist.getStmts()[0];
  }

  public Pegraph getPegraph() {
    return pegraph;
  }

  public void setPegraph(Pegraph pegraph) {
    this.pegraph = pegraph;
  }

  public void setStmtList(StmtList stmt) {
      this.stmtlist = (AliasStmts)stmt;
  }

  public AliasStmts getStmtList() {
    return this.stmtlist;
  }
  public String nodetupleToString() {
      StringBuilder strBuilder = new StringBuilder();
      strBuilder.append(stmtlist.to_string()).append(",");
      if(pegraph != null){
          strBuilder.append("1\t");
          strBuilder.append(pegraph.graphtoString());
      }
      else{
          strBuilder.append("0\t");
      }
      return strBuilder.toString();
  }


  @Override
  public void write(DataOutput dataOutput) throws IOException {

    if (pegraph != null) {
      dataOutput.writeByte(1);
      pegraph.write(dataOutput);
    }
    else {
        dataOutput.writeByte(0);
    }

    if (stmtlist != null) {
        dataOutput.writeByte(1);
        stmtlist.write(dataOutput);
    }
    else {
        dataOutput.writeByte(0);
    }
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    if (dataInput.readByte() == 1) {
        if (pegraph == null) {
            pegraph = new Pegraph();
        }
        pegraph.readFields(dataInput);
    }
    if (dataInput.readByte() == 1) {
        if (stmtlist == null) {
          stmtlist = new AliasStmts();
        }
        stmtlist.readFields(dataInput);
    }
  }

  public NodeTuple getNew() {
      NodeTuple nodeTuple = new NodeTuple();
      nodeTuple.pegraph = new Pegraph();
      nodeTuple.stmtlist = new AliasStmts();
      nodeTuple.pegraph.setDeep(this.pegraph);
      nodeTuple.stmtlist.setDeep(this.stmtlist);
      return nodeTuple;
  }

}