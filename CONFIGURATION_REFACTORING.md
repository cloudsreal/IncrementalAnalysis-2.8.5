# Configuration Refactoring Summary

## Overview

This refactoring makes all hard-coded file paths and addresses configurable, allowing the project to run in different environments (local, cloud) by modifying a single configuration file.

## Changes Made

### 1. New Configuration System

**Created files:**
- `Analysis/src/main/resources/analysis.properties` - Default configuration for local development
- `Analysis/src/main/resources/analysis.properties.cloud-example` - Example configuration for cloud deployment
- `Analysis/src/main/resources/README.md` - Configuration documentation
- `Analysis/src/main/java/incre_analysis/ConfigurationManager.java` - Singleton class to manage configuration

### 2. Updated Java Classes

All hard-coded paths have been replaced with calls to `ConfigurationManager`:

**Modified files:**
- `Analysis/src/main/java/incre_analysis/MasterBroadcast.java`
  - Removed: `hdfs://localhost:8000/client/analysis_conf`
  - Now uses: `ConfigurationManager.getInstance().getFullHdfsAnalysisConfPath()`

- `Analysis/src/main/java/incre_analysis/MyWorkerContext.java`
  - Removed: `new JedisPool(config, "localhost", 6379)`
  - Now uses: `new JedisPool(config, configManager.getRedisHost(), configManager.getRedisPort())`

- `Analysis/src/main/java/incre_alias_analysis/IncreAliasWorkerContext.java`
  - Removed: `hdfs://localhost:8000/client/analysis_conf`
  - Now uses: `configManager.getFullHdfsAnalysisConfPath()`

- `Analysis/src/main/java/incre_alias_analysis/IncreAliasVertexInputFormat.java`
  - Removed: `new JedisPool(config, "localhost", 6379)`
  - Now uses: `new JedisPool(config, configManager.getRedisHost(), configManager.getRedisPort())`

- `Analysis/src/main/java/incre_cache_analysis/IncreCacheVertexInputFormat.java`
  - Removed: `new JedisPool("localhost", 6379)`
  - Now uses: `new JedisPool(config, configManager.getRedisHost(), configManager.getRedisPort())`

### 3. Other Changes

- Updated `.gitignore` to exclude Maven `target/` directory

## Configuration Properties

The system now supports these configurable properties:

| Property | Description | Default Value |
|----------|-------------|---------------|
| `hdfs.namenode.uri` | HDFS NameNode URI | `hdfs://localhost:8000` |
| `hdfs.client.analysis.conf.path` | Path to analysis config in HDFS | `/client/analysis_conf` |
| `redis.host` | Redis server host | `localhost` |
| `redis.port` | Redis server port | `6379` |

## How to Use

### For Local Development
The default configuration works out of the box. No changes needed.

### For Cloud Deployment
1. Copy the example:
   ```bash
   cp Analysis/src/main/resources/analysis.properties.cloud-example \
      Analysis/src/main/resources/analysis.properties
   ```

2. Edit `Analysis/src/main/resources/analysis.properties`:
   - Update HDFS namenode URI for your cluster
   - Update Redis host/port for your Redis instance

3. Rebuild the project:
   ```bash
   cd Analysis
   mvn clean package
   ```

### Example Cloud Configuration

For Alibaba Cloud EMR:
```properties
hdfs.namenode.uri=hdfs://emr-header-1.cluster-289320:9000
hdfs.client.analysis.conf.path=/client/analysis_conf
redis.host=r-bp1zmxl3k5ypxoho2d.redis.rds.aliyuncs.com
redis.port=6379
```

## Benefits

1. **Single Source of Truth**: All environment-specific settings in one file
2. **Easy Environment Switching**: No code changes needed to switch between local/cloud
3. **Maintainability**: No more searching through code for hard-coded addresses
4. **Documentation**: Clear, documented configuration options
5. **Fallback Defaults**: System works even if config file is missing

## Testing

The changes have been validated:
- ✅ Build compiles successfully: `mvn clean compile`
- ✅ Package builds successfully: `mvn clean package`
- ✅ No compilation errors or warnings related to configuration changes
