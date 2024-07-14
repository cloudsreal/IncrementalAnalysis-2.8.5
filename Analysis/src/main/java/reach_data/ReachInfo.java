package reach_data;

/*
ReachInfo uses messages to predict vertex type : PU/UN(PA/PC)
*/
public class ReachInfo {
  private boolean entry_flag = false; // entry_flag = true: entry
  private boolean pa_flag = false; // pa_flag = true: UN(PA/PC)
  private boolean pc_flag = false; // pc_flag = true: PC;

  public ReachInfo(){

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
