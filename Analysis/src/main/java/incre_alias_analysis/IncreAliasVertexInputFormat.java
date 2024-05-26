package incre_alias_analysis;

import alias_data.AliasVertexValue;
import cache_data.CacheVertexValue;

import com.google.common.collect.ImmutableList;
import data.CommonWrite;
import org.apache.giraph.edge.Edge;
import org.apache.giraph.io.formats.TextVertexInputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class IncreAliasVertexInputFormat extends TextVertexInputFormat<IntWritable, AliasVertexValue, NullWritable> {

  private static final Pattern SEPARATOR = Pattern.compile("\t");
  JedisPoolConfig config = new JedisPoolConfig();
  public static JedisPool pool; 

  @Override
  public TextVertexInputFormat<IntWritable, AliasVertexValue, NullWritable>.TextVertexReader createVertexReader(
      InputSplit split, TaskAttemptContext context) throws IOException {
    config.setMaxTotal(100);
    config.setMaxIdle(10); 
    config.setMaxWaitMillis(50 * 1000);
    config.setTestOnBorrow(true);
    config.setTestOnReturn(true);
    config.setTestWhileIdle(true);
    pool = new JedisPool(config, "localhost", 6379);
    return new AliasVertexReader();
  }

  public class AliasVertexReader extends TextVertexReaderFromEachLineProcessed<String[]> {
    @Override
    protected String[] preprocessLine(Text line) {
      String[] tokens = SEPARATOR.split(line.toString());
      return tokens;
    }

    @Override
    protected IntWritable getId(String[] tokens) {
      int id = Integer.parseInt(tokens[0]);
      CommonWrite.method2("\nId:" + tokens[0]);
      return new IntWritable(id);
    }

    @Override
    protected AliasVertexValue getValue(String[] tokens) {
        // StringBuilder stmt = new StringBuilder();
        // for (int i = 1; i < tokens.length - 1; i++) {
        //   stmt.append(tokens[i]);
        //   stmt.append('\t');
        // }
        // stmt.append(tokens[tokens.length - 1]);
        // return new AliasVertexValue(stmt.toString());
        boolean nFlag = false;
        boolean eFlag = false;
        if(tokens[1].charAt(0) == '1') nFlag = true;
        if(tokens[2].charAt(0) == '1') eFlag = true;

        String value_str = null;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            value_str = jedis.get(tokens[0]);
        } catch (Exception e) {
            /// LOGGER.error("jedis set error:", e);
            System.out.println("jedis set error: STEP preprocessing output");
        } finally {
            if (null != jedis)
                jedis.close(); // release resouce to the pool
            else{
                CommonWrite.method2("\nId:" + tokens[0] + ", jedis is null");
            }
        }

        if(value_str == null){
            CommonWrite.method2("\nId:" + tokens[0] + " value is null, entry : " + String.valueOf(eFlag));
            return null;
        }
        else{
            /// CommonWrite.method2("\nId:" + tokens[0] + ", nFlag: " + String.valueOf(nFlag)
            ///                                         + ", eFlag: " + String.valueOf(eFlag));
            CommonWrite.method2("\nId:" + tokens[0] + " value: " + value_str);
        }

    
        int gs_index = value_str.indexOf('G');
        

        // get stmt
        String stmt_str = null;
        String gs_str = null;
        if(gs_index == -1){ // new added node/changed node, only stmt in redis
            stmt_str = value_str;
            gs_str = "0";
        }
        else{
            stmt_str = value_str.substring(0, gs_index - 1);
        } 

        // get GS + Fact for UA1
        if(nFlag){ // UA1 : can use GS and Fact
            // get GS
            if(gs_index == -1){  // new added node, only stmt in redis
                gs_str = "0"; 
                return new AliasVertexValue(stmt_str, gs_str /*gs is "0"*/,eFlag);
            }
            else{ // id --> stmt+gs+fact 
                int f_index = value_str.indexOf('F');
                gs_str = value_str.substring(gs_index + 3, f_index - 1);
                String fact_str = value_str.substring(f_index + 2);
                return new AliasVertexValue(stmt_str, gs_str, fact_str, eFlag);
            }
        }
        else{ // PC0
            gs_str = "0";
            return new AliasVertexValue(stmt_str, gs_str, eFlag);
        }

    //   StringBuilder stmt = new StringBuilder();
    //   int i = 1;
    //   for(; i < tokens.length; i++){
    //     /// CommonWrite.method2("token-idx: "+ String.valueOf(i) + ": " + tokens[i]);
    //     if(tokens[i].equals("GS:")){
    //         break;
    //     }
    //     else{
    //       stmt.append(tokens[i]);
    //       stmt.append('\t');
    //     }
    //   }
    //   CommonWrite.method2("stmts:\t" + stmt.toString());

    //   StringBuilder gsStr = new StringBuilder();
    //   i++;
    //   for(; i < tokens.length; i++){
    //     if(tokens[i].equals("F:")){
    //         break;
    //     }
    //     else{
    //       gsStr.append(tokens[i]);
    //       gsStr.append('\t');
    //     }
    //   }
    //   CommonWrite.method2("GS:\t" + gsStr.toString());

    //   StringBuilder pegStr = new StringBuilder();
    //   i++;
    //   for(; i < tokens.length; i++){
    //     pegStr.append(tokens[i]);
    //     pegStr.append('\t');
    //   }
    //   CommonWrite.method2("F:\t" + pegStr.toString());
      
    //   /// return new AliasVertexValue(stmt.toString());
    //   return new AliasVertexValue(stmt.toString(), gsStr.toString(), pegStr.toString(), false);
    }

    @Override
    protected Iterable<Edge<IntWritable, NullWritable>> getEdges(String[] tokens) throws IOException {
      return ImmutableList.of();
    }

    @Override
    public void close() throws IOException {
      super.close();
      pool.close();
    }
  }
}