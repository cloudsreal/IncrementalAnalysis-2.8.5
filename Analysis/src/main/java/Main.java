// public class Main {
//     public static void main(String[] args) {
//         System.out.println("Hello, world!");
//     }
// }

// import redis.clients.jedis.Jedis;
// import redis.clients.jedis.JedisPool;
// import redis.clients.jedis.JedisPoolConfig;

// public class Main {
//     public static void main(String[] args) {
//         JedisPoolConfig config = new JedisPoolConfig();
//         // 最大空闲连接数，需自行评估，不超过Redis实例的最大连接数。
//         config.setMaxIdle(200);
//         // 最大连接数，需自行评估，不超过Redis实例的最大连接数。
//         config.setMaxTotal(300);
//         config.setTestOnBorrow(false);
//         config.setTestOnReturn(false);
//         // 分别将host和password的值替换为实例的连接地址、密码。
//         // String host = "r-bp1s1bt2tlq3p1****pd.redis.rds.aliyuncs.com";
//         // String password = "r-bp1s1bt2tlq3p1****:Database123";
//         // JedisPool pool = new JedisPool(config, host, 6379, 3000, password);
//         JedisPool pool = new JedisPool("localhost", 6379);
//         Jedis jedis = null;
//         try {
//             jedis = pool.getResource();
//             // 执行相关操作，示例如下。
//             jedis.set("foo10", "bar");
//             System.out.println(jedis.get("foo10"));
//             // jedis.zadd("sose", 0, "car");
//             // jedis.zadd("sose", 0, "bike");
//             // System.out.println(jedis.zrange("sose", 0, -1));
//             jedis.set("hello", "world");
//             System.out.println(jedis.get("hello"));
//             jedis.set("code", "program");
//             System.out.println(jedis.get("code"));
//         } catch (Exception e) {
//             // 超时或其他异常处理。
//             e.printStackTrace();
//         } finally {
//             if (jedis != null) {
//                 jedis.close();
//             }
//         }
//         pool.destroy(); // 当应用退出，需销毁资源时，调用此方法。此方法会断开连接、释放资源。
//     }
// }


// import redis.clients.jedis.Jedis;
// import redis.clients.jedis.JedisPool;
// import redis.clients.jedis.JedisPoolConfig;

// public class Main {
//     public static void main(String[] args) {
//         JedisPoolConfig config = new JedisPoolConfig();
//         // 最大空闲连接数，需自行评估，不超过Redis实例的最大连接数。
//         config.setMaxIdle(200);
//         // 最大连接数，需自行评估，不超过Redis实例的最大连接数。
//         config.setMaxTotal(300);
//         config.setTestOnBorrow(false);
//         config.setTestOnReturn(false);
//         // 分别将host和password的值替换为实例的连接地址、密码。
//         /// String host = "r-bp1s1bt2tlq3p1****pd.redis.rds.aliyuncs.com";
//         /// String password = "r-bp1s1bt2tlq3p1****:Database123";
//         String host = "r-bp1rlmgkj0cljfup6k.redis.rds.aliyuncs.com";
//         String password = "r-bp1rlmgkj0cljfup6k:Szw@98seG";
//         JedisPool pool = new JedisPool(config, host, 6379, 3000, password);
//         Jedis jedis = null;
//         try {
//             jedis = pool.getResource();
//             // 执行相关操作，示例如下。
//             jedis.set("foo10", "bar");
//             System.out.println(jedis.get("foo10"));
//             // jedis.zadd("sose", 0, "car");
//             // jedis.zadd("sose", 0, "bike");
//             // System.out.println(jedis.zrange("sose", 0, -1));
//             jedis.set("Hello", "World");
//             System.out.println(jedis.get("Hello"));
//             jedis.set("Coding", "Programming");
//             System.out.println(jedis.get("Coding"));
//         }
//         catch (Exception e) {
//             // 超时或其他异常处理。
//             e.printStackTrace();
//         }
//         finally {
//             if (jedis != null) {
//                 jedis.close();
//             }
//         }
//         pool.destroy();    // 当应用退出，需销毁资源时，调用此方法。此方法会断开连接、释放资源。
//     }
// }


// import redis.clients.jedis.*;
// import java.util.HashSet;
// import java.util.Set;

// public class Main {
//     private static final int DEFAULT_TIMEOUT = 2000;
//     private static final int DEFAULT_REDIRECTIONS = 5;
//     private static final ConnectionPoolConfig config = new ConnectionPoolConfig();

//     public static void main(String args[]) {
//         // 最大空闲连接数，由于直连模式为客户端直接连接某个数据库分片，需要保证：业务机器数 * MaxTotal < 单个数据库分片的最大连接数。
//         // 其中社区版单个分片的最大连接数为10,000，企业版单个分片的最大连接数为30,000。
//         config.setMaxTotal(30);
//         // 最大空闲连接数, 根据业务需要设置。
//         config.setMaxIdle(20);
//         config.setMinIdle(15);

//         // 开通直连访问时申请到的直连地址。
//         String host = "r-bp1rlmgkj0cljfup6k.redis.rds.aliyuncs.com";
//         int port = 6379;
//         // 实例的密码。
//         String password = "Szw@98seG";

//         Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
//         jedisClusterNode.add(new HostAndPort(host, port));
//         JedisCluster jc = new JedisCluster(jedisClusterNode, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT, DEFAULT_REDIRECTIONS,
//             password, "clientName", config);

//         jc.set("key", "value");
//         jc.get("key");


//         jc.set("Happy", "Birthday");
//         jc.get("Happy");

//         jc.set("foo10", "bar");
//         jc.get("foo10");

//         jc.close();     // 当应用退出，需销毁资源时，调用此方法。此方法会断开连接、释放资源。
//     }
// }



// import redis.clients.jedis.Jedis;
// import redis.clients.jedis.JedisPool;
// import redis.clients.jedis.JedisPoolConfig;

// public class Main {
//     public static void main(String[] args) {
//         JedisPoolConfig config = new JedisPoolConfig();
//         // 最大空闲连接数，需自行评估，不超过Redis实例的最大连接数。
//         config.setMaxIdle(200);
//         // 最大连接数，需自行评估，不超过Redis实例的最大连接数。
//         config.setMaxTotal(300);
//         config.setTestOnBorrow(false);
//         config.setTestOnReturn(false);
//         // 分别将host和password的值替换为实例的连接地址、密码。
//         String host = "r-bp1rkfthkdyc2z2ghq.redis.rds.aliyuncs.com";
//         String password = "r-bp1rkfthkdyc2z2ghq:Szw@98seG";
//         JedisPool pool = new JedisPool(config, host, 6379, 3000, password);
//         Jedis jedis = null;
//         try {
//             jedis = pool.getResource();
//             // 执行相关操作，示例如下。
//             jedis.set("Foo666", "Bar333");
//             System.out.println(jedis.get("Foo666"));
//             jedis.set("Key111", "Value111");
//             System.out.println(jedis.get("key111"));
//             jedis.set("Happy33", "Birthday77");
//             System.out.println(jedis.get("Happy33"));
//         }
//         catch (Exception e) {
//             // 超时或其他异常处理。
//             e.printStackTrace();
//         }
//         finally {
//             if (jedis != null) {
//                 jedis.close();
//             }
//         }
//         pool.destroy();    // 当应用退出，需销毁资源时，调用此方法。此方法会断开连接、释放资源。
//     }
// }



import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Main {
    public static void main(String[] args) {
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大空闲连接数，需自行评估，不超过Redis实例的最大连接数。
        config.setMaxIdle(200);
        // 最大连接数，需自行评估，不超过Redis实例的最大连接数。
        config.setMaxTotal(300);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        // 分别将host和password的值替换为实例的连接地址、密码。
        // String host = "r-bp1rkfthkdyc2z2ghq.redis.rds.aliyuncs.com";
        // String password = "r-bp1rkfthkdyc2z2ghq:Szw@98seG";
        // JedisPool pool = new JedisPool(config, host, 6379, 3000, password);


        String host = "r-bp1rkfthkdyc2z2ghq.redis.rds.aliyuncs.com";
        int port = 6379;
        JedisPool pool = new JedisPool(config, host, port);
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            // 执行相关操作，示例如下。
            jedis.set("Foo666", "Bar333");
            System.out.println(jedis.get("Foo666"));
            jedis.set("Key111", "Value111");
            System.out.println(jedis.get("Key111"));
            jedis.set("Happy33", "Birthday77");
            System.out.println(jedis.get("Happy33"));
        }
        catch (Exception e) {
            // 超时或其他异常处理。
            e.printStackTrace();
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        pool.destroy();    // 当应用退出，需销毁资源时，调用此方法。此方法会断开连接、释放资源。
    }
}