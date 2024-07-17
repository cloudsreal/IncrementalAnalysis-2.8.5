package incre_alias_analysis;

import alias_data.AliasVertexValue;

import com.google.common.collect.ImmutableList;
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
  public static JedisPool pool = new JedisPool("localhost", 6379);
//    public static JedisPool pool;

  @Override
  public TextVertexInputFormat<IntWritable, AliasVertexValue, NullWritable>.TextVertexReader createVertexReader(
      InputSplit split, TaskAttemptContext context) throws IOException {
      config.setMaxTotal(300);
      config.setMaxIdle(200); //最大空闲连接数
      config.setMaxWaitMillis(50 * 1000); //获取Jedis连接的最大等待时间（50秒）
      config.setTestOnBorrow(true); //在获取Jedis连接时，自动检验连接是否可用
      config.setTestOnReturn(true);  //在将连接放回池中前，自动检验连接是否有效
      config.setTestWhileIdle(true);  //自动测试池中的空闲连接是否都是可用连接
//      String host = "r-bp1kcg19hh4p0xq9dm.redis.rds.aliyuncs.com";
//      int port = 6379;
//      pool = new JedisPool(config, host, port);
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
//      CommonWrite.method2("\nId:" + tokens[0]);
      return new IntWritable(id);
    }

    @Override
    protected AliasVertexValue getValue(String[] tokens) {
        boolean nFlag = false;
        boolean eFlag = false;
        if(tokens[1].charAt(0) == '1') nFlag = true;
        if(tokens[2].charAt(0) == '1') eFlag = true;

        AliasVertexValue aliasVertexValue;

        if(nFlag){ // UA1 : can use GS and Fact
            String value_str = null;
            Jedis value_jedis = null;
            try {
                value_jedis = pool.getResource();
                value_str = value_jedis.get(tokens[0]+"f");
            } catch (Exception e) {
                System.out.println("jedis set error: STEP preprocessing output");
            } finally {
                if (null != value_jedis)
                    value_jedis.close(); // release resouce to the pool
            }
            int gs_index = -1;
            if(value_str != null) {
                gs_index = value_str.indexOf('G'); //GS:\tF:\t
            }
            if(gs_index == -1){  // new added node, only stmt in redis
                aliasVertexValue = new AliasVertexValue("0" /*gs is "0"*/,eFlag);
            }
            else{ // id --> stmt+gs+fact
                int f_index = value_str.indexOf('F');
                String gs_str = value_str.substring(gs_index + 3, f_index - 1);
                String fact_str = value_str.substring(f_index + 2);
                aliasVertexValue =  new AliasVertexValue(gs_str, fact_str, eFlag);
            }
        } else { // PC
            aliasVertexValue =  new AliasVertexValue("0" /*gs is "0"*/, eFlag);
        }

        if(eFlag) { // get stmts from redis for entrys
            String stmt_str = null;
            Jedis stmt_jedis = null;
            try {
                stmt_jedis = pool.getResource();
                stmt_str = stmt_jedis.get(tokens[0]+"s");
            } catch (Exception e) {
                System.out.println("jedis set error: STEP preprocessing output");
            } finally {
                if (null != stmt_jedis)
                    stmt_jedis.close(); // release resouce to the pool
            }
            aliasVertexValue.setStmts(stmt_str);
        }

        return aliasVertexValue;
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
