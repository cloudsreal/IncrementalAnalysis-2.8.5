package incre_cache_analysis;

import alias_data.Grammar;
import alias_data.Singletons;
import org.apache.giraph.worker.WorkerContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class IncreMyWorkerContext extends WorkerContext {

    JedisPoolConfig config = new JedisPoolConfig();
    public static JedisPool pool = null;

    @Override
    public void preApplication() {
        config.setMaxTotal(1000);
        config.setMaxIdle(10); //最大空闲连接数
        config.setMaxWaitMillis(50 * 1000); //获取Jedis连接的最大等待时间（50秒）
        config.setTestOnBorrow(true); //在获取Jedis连接时，自动检验连接是否可用
        config.setTestOnReturn(true);  //在将连接放回池中前，自动检验连接是否有效
        config.setTestWhileIdle(true);  //自动测试池中的空闲连接是否都是可用连接
        pool = new JedisPool(config, "localhost", 6379);
    }

    @Override
    public void postApplication() {
        pool.close();
    }

    @Override
    public void preSuperstep() {

    }

    @Override
    public void postSuperstep() {

    }
}
