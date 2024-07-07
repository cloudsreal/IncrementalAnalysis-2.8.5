package incre_analysis;

import org.apache.giraph.worker.WorkerContext;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MyWorkerContext extends WorkerContext {

    JedisPoolConfig config = new JedisPoolConfig();
    public static JedisPool pool = null;

    @Override
    public void preApplication() {
        config.setMaxTotal(300);
        config.setMaxIdle(200); //最大空闲连接数
        config.setMaxWaitMillis(50 * 1000); //获取Jedis连接的最大等待时间（50秒）
        config.setTestOnBorrow(true); //在获取Jedis连接时，自动检验连接是否可用
        config.setTestOnReturn(true);  //在将连接放回池中前，自动检验连接是否有效
        config.setTestWhileIdle(true);  //自动测试池中的空闲连接是否都是可用连接
        pool = new JedisPool(config, "localhost", 6379);
        /// @szw, configuration according to Ali EMR
//        String host = "r-bp1kcg19hh4p0xq9dm.redis.rds.aliyuncs.com";
//        int port = 6379;
//        pool = new JedisPool(config, host, port);
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
