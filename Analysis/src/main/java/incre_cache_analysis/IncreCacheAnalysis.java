package incre_cache_analysis;

import incre_analysis.IncreAnalysis;
import cache_data.*;
import org.apache.hadoop.io.NullWritable;

public class IncreCacheAnalysis extends IncreAnalysis<CacheVertexValue, NullWritable, CacheMsg> {

    @Override
    public void setAnalysisConf(){
      tool = new CacheTool(); 
      fact = new CacheState(); 
      msg = new CacheMsg();
    }
}