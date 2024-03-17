package incre_analysis;

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
  public static SetWritable entry1 = null;
  public static SetWritable entry2 = null;

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
      entry1 = getBroadcast("entry1");
      entry2 = getBroadcast("entry2");
      if(entry1.getValues().contains(vertex.getId().get())) {
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
    else if (getSuperstep() == 1) {
      if(beActive(messages, vertex.getValue())) {
        fact = tool.combine(messages, vertex.getValue());
        vertex.getValue().setFact(fact);
//        final SetWritable entry2 = getBroadcast("entry2");
        if (entry2.getValues().contains(vertex.getId().get())) {
          Fact out_fact = tool.transfer(vertex.getValue().getStmtList(), fact);
          for (Edge<IntWritable, E> edge : vertex.getEdges()) {
            msg.setVertexID(vertex.getId());
            msg.setExtra(vertex.getValue());
            msg.setFact(out_fact.getNew());
            sendMessage(edge.getTargetVertexId(), msg);
          }
        }
      }
      vertex.voteToHalt();
    }
    else {
      if(beActive(messages, vertex.getValue())){
        fact = tool.combine(messages, vertex.getValue());
        Fact out_old_fact = tool.transfer(vertex.getValue().getStmtList(), vertex.getValue().getFact());
        Fact out_new_fact = tool.transfer(vertex.getValue().getStmtList(), fact);
        boolean canPropagate = tool.propagate(out_old_fact, out_new_fact);
        if (canPropagate) {
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