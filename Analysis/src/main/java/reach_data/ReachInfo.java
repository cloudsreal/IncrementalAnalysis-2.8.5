package reach_data;

/*
ReachInfo uses messages to predict vertex type : PU/UN(PA/PC)
*/
public class ReachInfo {

  private boolean entry_flag = false; // If node is entry, entry_flag = true; unless false
  private boolean pa_flag = false;    // If pa_flag is true, must be UN(updated nodes, i.e., PA/PC)
  private boolean pc_flag = false;    // If pc_flag is true, PC; else PA

  public ReachInfo(){

  }

  public ReachInfo(ReachVertexValue vertexvalue){
    this.entry_flag = vertexvalue.getEntry();
    this.pa_flag = vertexvalue.getPA();
    this.pc_flag = vertexvalue.getPC();
  }


  public void setEntry(boolean entry_flag) {
    this.entry_flag = entry_flag;
  }

  public boolean getEntry() {
    return entry_flag;
  }

  public void setPA(boolean pa_flag) {
    this.pa_flag = pa_flag;
  }

  public boolean getPA() {
    return pa_flag;
  }

  public void setPC(boolean pc_flag) {
      this.pc_flag = pc_flag;
  }

  public boolean getPC() {
    return pc_flag;
  }
  
}
