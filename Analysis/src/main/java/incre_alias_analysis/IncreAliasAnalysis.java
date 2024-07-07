package incre_alias_analysis;

import alias_data.*;
import alias_stmt.AStmt;
import alias_stmt.ReturnAStmt;
import alias_stmt.TYPE;
import data_incre.VertexValue;
import incre_analysis.IncreAnalysis;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;

public class IncreAliasAnalysis extends IncreAnalysis<AliasVertexValue, NullWritable, AliasMsg> {

    @Override
    public void setAnalysisConf(){
      IncreAliasWorkerContext context = getWorkerContext();
      tool = new AliasTool(context); 
      fact = new Pegraph(); 
      msg = new AliasMsg();
    }

    @Override
    public boolean beActive(Iterable<AliasMsg> messages, VertexValue vertexValue){
      boolean beActive = false;
      AStmt curStmt = (AStmt)(vertexValue.getStmtList().getStmts()[0]);
      AliasVertexValue aliasVertexValue = (AliasVertexValue)vertexValue;

      if(curStmt.getStmt() == TYPE.Return){
        if(aliasVertexValue.getGraphStore() == null){
          aliasVertexValue.setGraphStore(new MapWritable());
        }
      }

      for (AliasMsg message : messages) {
          AStmt preStmt = message.getStmt();
          /// AStmt curStmt = (AStmt)vertexValue.getStmtList().getStmts()[0];
          if (!(curStmt.getStmt() == TYPE.Return && ((ReturnAStmt)curStmt).getLength() == 0 &&
                  (preStmt.getStmt() == TYPE.Callfptr || preStmt.getStmt() == TYPE.Call))) {
              beActive = true;
              // break;
          }
          //update special graphstore
          if(curStmt.getStmt() == TYPE.Return){
              MapWritable oldGraphStore = aliasVertexValue.getGraphStore();
              NodeTuple nodeTuple = new NodeTuple();
              nodeTuple.setPegraph((Pegraph)message.getFact());
              nodeTuple.setStmtList(message.getStmtList());
              oldGraphStore.put(new IntWritable(message.getVertexID().get()), nodeTuple.getNew());
              // oldGraphStore.put(new IntWritable(message.getVertexID().get()), message.getNodeTuple().getNew());
          }
      }
      return beActive;
    }
}