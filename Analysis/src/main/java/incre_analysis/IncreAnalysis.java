package incre_analysis;

import cache_data.CacheState;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.graph.Vertex;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import java.util.*;

import java.lang.Iterable;

import data.*;
import org.apache.hadoop.io.Writable;

public class IncreAnalysis<V extends VertexValue, E extends Writable, M extends Msg> extends BasicComputation<IntWritable, V , E, M> {
  public Tool tool = null;
  public Fact fact = null;
  public M msg  = null;
  public static SetWritable entry = null;

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
      ///entry = getBroadcast("entry");
      /// if(entry.getValues().contains(vertex.getId().get())) {
      CommonWrite.method2("\nstep0, Id:\t"+vertex.getId().toString());
      if(vertex.getValue().isEntry()) {
        CommonWrite.method2("\nId:\t"+vertex.getId().toString() + " is entry");
        Fact in_fact = vertex.getValue().getFact();
        if(in_fact == null){
          /// vertex.getValue().setFact(new CacheState());
          vertex.getValue().setNewFact();
        }
        vertex.getValue().setPropagate(true);
//        if(node \in PU && node not in UA)
        Fact out_fact = tool.transfer(vertex.getValue().getStmtList(), vertex.getValue().getFact());
        CommonWrite.method2("\nId:\t"+vertex.getId().toString()+", State:\t"+out_fact.toString());
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

//        // StringBuilder stringBuilder = new StringBuilder();
//        // stringBuilder.append("\npreds:\t");
//        // for (Msg item : messages){
//        //   stringBuilder.append(item.getVertexID()).append("\t");
//        // }
//        // CommonWrite.method2(stringBuilder.toString());
//        // CommonWrite.method2("Id:\t"+vertex.getId().toString()+", State:\t"+fact.toString());
//
//        /// Fact out_old_fact = tool.transfer(vertex.getValue().getStmtList(), vertex.getValue().getFact());
        Fact out_old_fact = null;
        if(vertex.getValue().isPropagate() && vertex.getValue().getFact() != null){
          out_old_fact = tool.transfer(vertex.getValue().getStmtList(), vertex.getValue().getFact());
        }
        Fact out_new_fact = tool.transfer(vertex.getValue().getStmtList(), fact);

        boolean canPropagate = tool.propagate(out_old_fact, out_new_fact);
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