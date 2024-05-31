package reach_data;

public class ReachInfo {
  private boolean entry_flag = false;
  private boolean pa_flag = false;
  private boolean pc_flag = false;

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
  
}
