package incre_analysis;

import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;

import java.lang.Iterable;

import incre_data.*;
import org.apache.hadoop.io.Writable;

public class IncreAnalysis<V extends VertexValue, E extends Writable, M extends Msg> extends BasicComputation<IntWritable, V , E, M> {
  public Tool tool = null;
  public Fact fact = null;
  public M msg  = null;

  public void setAnalysisConf(){
    // TODO for initialize tool. fact/msg type according to specific dataflow analysis
    // e.g.
    // tool = new CacheTool();
    // fact = new CacheState();
    // msg = new CacheMsg();
  }


  public boolean beActive(Iterable<M> messages, VertexValue vertexValue){
    // TODO
    return true;
  }

  @Override
  public void compute(Vertex<IntWritable, V, E> vertex, Iterable<M> messages) {

    setAnalysisConf();

    if (getSuperstep() == 0) {
      if(vertex.getValue().isEntry()) {

        Fact in_fact = vertex.getValue().getFact();
        if(in_fact == null){
          vertex.getValue().setNewFact();
        }
        vertex.getValue().setPropagate(true);
        Fact out_fact = tool.transfer(vertex.getValue().getStmtList(), vertex.getValue().getFact());
        for(Edge<IntWritable, E> edge : vertex.getEdges()) {
          msg.setVertexID(vertex.getId());
          msg.setExtra(vertex.getValue());
          msg.setFact(out_fact.getNew());
          sendMessage(edge.getTargetVertexId(), msg);
        }
      }
      vertex.voteToHalt();
    }
    else {

      if(beActive(messages, vertex.getValue())){
        fact = tool.combine(messages, vertex.getValue());

        Fact out_old_fact = null;
        if(vertex.getValue().isPropagate() && vertex.getValue().getFact() != null){
          out_old_fact = tool.transfer(vertex.getValue().getStmtList(), vertex.getValue().getFact());
        }
        Fact out_new_fact = tool.transfer(vertex.getValue().getStmtList(), fact);

        boolean canPropagate = tool.propagate(out_old_fact, out_new_fact);

//        CommonWrite.method2("\nstep" + getSuperstep() + ", Id:\t"+vertex.getId().toString());
//        CommonWrite.method2("\nId:\t"+vertex.getId().toString()+", State:\t"+ out_new_fact.toString());

        if (canPropagate) {
          vertex.getValue().setPropagate(true);
          vertex.getValue().setFact(fact);
          msg.setVertexID(vertex.getId());
          msg.setExtra(vertex.getValue());
          msg.setFact(out_new_fact.getNew());
          for(Edge<IntWritable,E> edge : vertex.getEdges()){
            sendMessage(edge.getTargetVertexId(), msg);
          }
        }
      }
      vertex.voteToHalt();
    }
  }
}