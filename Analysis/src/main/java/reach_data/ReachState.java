package reach_data;

import cache_data.CacheState;
import data.Fact;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.python.antlr.op.In;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class PredList extends Fact {
    public boolean flag;
    public Set<IntWritable> PC;

    public PredList() {
        flag = false;
        PC = new HashSet<>();
    }

    public void merge(Fact fact){
        if (fact == null) return;
        PredList tmp_pred = (PredList) fact;

        for(IntWritable pc : tmp_pred.PC){
        }

    }

    private Text getTypeById(IntWritable id) {
        for (Map.Entry<Text, Set<IntWritable>> entry : typeIdsMap.entrySet()) {
            if (entry.getValue().contains(id)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Fact getNew(){
        PredList newPredList = new PredList();

        for (Map.Entry<Text, Set<IntWritable>> entry : this.typeIdsMap.entrySet()) {
            Text type = entry.getKey();
            Set<IntWritable> ids = entry.getValue();

            Set<IntWritable> newIds = new HashSet<>(ids);
            newPredList.typeIdsMap.put(new Text(type), newIds);
        }

        return newPredList;
    }

    public Text getType() {
        for (Text type : Arrays.asList(new Text("PC"), new Text("PA"), new Text("PU"))) {
            Set<IntWritable> ids = this.typeIdsMap.get(type);
            if (ids != null && !ids.isEmpty()) {
                return type;
            }
        }
        return new Text("PU");
    }
    public boolean consistent(Fact fact){
        return false;
    }



}
