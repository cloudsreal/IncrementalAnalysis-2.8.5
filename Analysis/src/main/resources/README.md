# Configuration Guide

This directory contains configuration files for the IncrementalAnalysis project.

## Files

- **analysis.properties** - The active configuration file used by the application
- **analysis.properties.cloud-example** - Example configuration for cloud environments (EMR, Redis RDS, etc.)

## How to Configure

### For Local Development

The default `analysis.properties` file is already configured for local development:

```properties
hdfs.namenode.uri=hdfs://localhost:8000
hdfs.client.analysis.conf.path=/client/analysis_conf
redis.host=localhost
redis.port=6379
```

### For Cloud Deployment (e.g., Alibaba Cloud EMR)

1. Copy the cloud example:
   ```bash
   cp analysis.properties.cloud-example analysis.properties
   ```

2. Update the values in `analysis.properties`:
   - Replace `ClusterID` in `hdfs.namenode.uri` with your actual EMR cluster ID
   - Update `redis.host` with your Redis RDS address
   - Adjust `redis.port` if using a non-standard port

Example for Alibaba Cloud EMR:
```properties
hdfs.namenode.uri=hdfs://emr-header-1.cluster-289320:9000
hdfs.client.analysis.conf.path=/client/analysis_conf
redis.host=r-bp1zmxl3k5ypxoho2d.redis.rds.aliyuncs.com
redis.port=6379
```

## Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `hdfs.namenode.uri` | HDFS NameNode URI (scheme, host, and port) | `hdfs://localhost:8000` |
| `hdfs.client.analysis.conf.path` | Path to analysis configuration in HDFS | `/client/analysis_conf` |
| `redis.host` | Redis server hostname or IP address | `localhost` |
| `redis.port` | Redis server port | `6379` |

## Notes

- The configuration file is loaded from the classpath at runtime
- If the configuration file is not found, the application will use default values for local development
- You can switch between environments by simply updating this one configuration file
- After changing the configuration, rebuild the application: `mvn clean package`
