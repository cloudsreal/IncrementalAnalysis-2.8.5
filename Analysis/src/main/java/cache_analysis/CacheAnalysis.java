package cache_analysis;

import analysis.Analysis;
import cache_data.*;
import org.apache.hadoop.io.NullWritable;

public class CacheAnalysis extends Analysis<CacheVertexValue, NullWritable, CacheMsg> {

    @Override
    public void setAnalysisConf(){
      tool = new CacheTool(); 
      fact = new CacheState(); 
      msg = new CacheMsg();
    }
}