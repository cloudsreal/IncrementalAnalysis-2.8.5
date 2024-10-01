package incre_data;

public abstract class Stmt<T>{
  protected T stmt_value;
  
  public T getStmt()
  {
    return stmt_value;
  }
  public void setStmt(T stmt_value)
  {
    this.stmt_value = stmt_value;
  }
}