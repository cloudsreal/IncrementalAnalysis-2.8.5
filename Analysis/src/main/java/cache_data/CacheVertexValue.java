package cache_data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Scanner;

import incre_data.*;

public class CacheVertexValue extends VertexValue {

  public CacheVertexValue(){
    stmts = null;
    fact = null;
  }

  public CacheVertexValue(String text, boolean flag, boolean entry) {
    Scanner sc = new Scanner(text);
    stmts = new CacheIRs(sc, flag);
    fact = null;
    this.entry = entry;
  }


  public CacheVertexValue(boolean entry){
    stmts = null;
    fact = null;
    this.entry = entry;
  }

  public CacheVertexValue(String text, boolean flag, String fact_text, boolean entry) {
    Scanner sc = new Scanner(text);
    stmts = new CacheIRs(sc, flag);
    // fact = null;
    fact = new CacheState(fact_text);
    /// this.propagate = propagate;
    this.entry = entry;
  }

  public CacheVertexValue(String fact_text, boolean entry) {
    stmts = null;
    // fact = null;
    fact = new CacheState(fact_text);
    /// this.propagate = propagate;
    this.entry = entry;
  }

  public CacheIRs getCacheIRs(){
    return (CacheIRs)stmts;
  }

  public CacheState getCacheState(){
    return (CacheState)fact;
  }


  @Override
  public void setNewFact() {
    fact = new CacheState(); 
  }

  @Override
  public void setStmts(String text, boolean flag){
    Scanner sc = new Scanner(text);
    stmts = new CacheIRs(sc, flag);
  }

  public void write(DataOutput out) throws IOException {
    if(stmts != null) {
      out.writeByte(1);
      stmts.write(out);
    } else {
      out.writeByte(0);
    }
//    stmts.write(out);
    if (fact != null) {
      out.writeByte(1);
      fact.write(out);
    }
    else {
      out.writeByte(0);
    }
    out.writeBoolean(propagate);
    out.writeBoolean(entry);
  }

  public void readFields(DataInput in) throws IOException {
    if (in.readByte() == 1) {
      if(stmts == null){
        stmts = new CacheIRs();
      }
      stmts.readFields(in);
    }
//    stmts.readFields(in);
    if (in.readByte() == 1) {
      if (fact == null) {
        fact = new CacheState();
      }
      fact.readFields(in);
    }
    propagate = in.readBoolean();
    entry = in.readBoolean();
  }
}