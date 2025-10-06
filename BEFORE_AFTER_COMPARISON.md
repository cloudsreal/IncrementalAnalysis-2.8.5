# Before and After: Configuration Refactoring

## Problem Statement

The repository contained hard-coded file paths throughout the codebase, making it difficult to switch between local development and cloud deployment environments.

## Before: Hard-Coded Paths

### MasterBroadcast.java
```java
// Hard-coded path
public static String conf_path = "hdfs://localhost:8000/client/analysis_conf";
// Commented cloud alternative
// public static String conf_path = "hdfs://master-1-1.c-db548736175e8161.cn-hangzhou.emr.aliyuncs.com:9000/client/analysis_conf";
```

### MyWorkerContext.java
```java
// Hard-coded Redis connection
pool = new JedisPool(config, "localhost", 6379);
// Commented cloud alternative
// String host = "r-bp1zmxl3k5ypxoho2d.redis.rds.aliyuncs.com";
// int port = 6379;
// pool = new JedisPool(config, host, port);
```

### IncreAliasWorkerContext.java
```java
// Hard-coded HDFS path
BufferedReader pa = new BufferedReader(readHDFS("hdfs://localhost:8000/client/analysis_conf"));
// Commented cloud alternative
// BufferedReader pa = new BufferedReader(readHDFS("hdfs://master-1-1.c-db548736175e8161.cn-hangzhou.emr.aliyuncs.com:9000/client/analysis_conf"));
```

### IncreAliasVertexInputFormat.java
```java
// Hard-coded Redis connection
pool = new JedisPool(config, "localhost", 6379);
// Commented cloud alternative
// String host = "r-bp1zmxl3k5ypxoho2d.redis.rds.aliyuncs.com";
// int port = 6379;
// pool = new JedisPool(config, host, port);
```

### IncreCacheVertexInputFormat.java
```java
// Hard-coded Redis connection
public static JedisPool pool = new JedisPool("localhost", 6379);
// Commented cloud alternative
// pool = new JedisPool(config, "localhost", 6379);
// String host = "r-bp1zmxl3k5ypxoho2d.redis.rds.aliyuncs.com";
// pool = new JedisPool(config, host, port);
```

## After: Configurable System

### New: ConfigurationManager.java
```java
public class ConfigurationManager {
    private static ConfigurationManager instance;
    private Properties properties;

    public String getHdfsNamenodeUri() {
        return properties.getProperty("hdfs.namenode.uri", "hdfs://localhost:8000");
    }

    public String getFullHdfsAnalysisConfPath() {
        return getHdfsNamenodeUri() + getHdfsClientAnalysisConfPath();
    }

    public String getRedisHost() {
        return properties.getProperty("redis.host", "localhost");
    }

    public int getRedisPort() {
        return Integer.parseInt(properties.getProperty("redis.port", "6379"));
    }
}
```

### New: analysis.properties
```properties
# HDFS Configuration
hdfs.namenode.uri=hdfs://localhost:8000
hdfs.client.analysis.conf.path=/client/analysis_conf

# Redis Configuration
redis.host=localhost
redis.port=6379
```

### Updated: MasterBroadcast.java
```java
// Using ConfigurationManager
private static final ConfigurationManager config = ConfigurationManager.getInstance();
public static String conf_path = config.getFullHdfsAnalysisConfPath();
```

### Updated: MyWorkerContext.java
```java
// Using ConfigurationManager
private static final ConfigurationManager configManager = ConfigurationManager.getInstance();

pool = new JedisPool(config, configManager.getRedisHost(), configManager.getRedisPort());
```

### Updated: IncreAliasWorkerContext.java
```java
// Using ConfigurationManager
private static final ConfigurationManager configManager = ConfigurationManager.getInstance();

BufferedReader pa = new BufferedReader(readHDFS(configManager.getFullHdfsAnalysisConfPath()));
```

### Updated: IncreAliasVertexInputFormat.java
```java
// Using ConfigurationManager
private static final ConfigurationManager configManager = ConfigurationManager.getInstance();

pool = new JedisPool(config, configManager.getRedisHost(), configManager.getRedisPort());
```

### Updated: IncreCacheVertexInputFormat.java
```java
// Using ConfigurationManager
private static final ConfigurationManager configManager = ConfigurationManager.getInstance();

pool = new JedisPool(config, configManager.getRedisHost(), configManager.getRedisPort());
```

## Switching Between Environments

### Before
To switch from local to cloud, you had to:
1. Find every file with hard-coded paths
2. Comment out local paths
3. Uncomment cloud paths
4. Recompile

### After
To switch from local to cloud, you only need to:
1. Edit `Analysis/src/main/resources/analysis.properties`
2. Update the values for your cloud environment
3. Recompile

**Example for Cloud:**
```properties
hdfs.namenode.uri=hdfs://emr-header-1.cluster-289320:9000
hdfs.client.analysis.conf.path=/client/analysis_conf
redis.host=r-bp1zmxl3k5ypxoho2d.redis.rds.aliyuncs.com
redis.port=6379
```

## Benefits

1. ✅ **Single Configuration File**: All environment settings in one place
2. ✅ **No Code Changes**: Switch environments without touching Java code
3. ✅ **Documented**: Clear examples for both local and cloud
4. ✅ **Maintainable**: Easy to understand and modify
5. ✅ **Safe Defaults**: Works even if config file is missing
6. ✅ **Version Control Friendly**: No need to comment/uncomment code
