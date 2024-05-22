package alias_stmt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class RetAStmt extends AStmt
{
	public RetAStmt()
	{
		this.stmt_value = TYPE.Ret;
	}
	@Override
	public void toString_sub(StringBuilder str)
	{
		str.append("ret");
	}

	@Override
	public AStmt decopy() {
		return new RetAStmt();
	}

	@Override
	public String toString() {
		return "";
	}
	
	@Override
	public String to_string(){
		return "";
	}

	@Override
	public void write(DataOutput dataOutput) throws IOException {

	}

	@Override
	public void readFields(DataInput dataInput) throws IOException {

	}
}
