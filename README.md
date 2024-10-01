# BigDataflow

BigDataflow is a distributed interprocedural dataflow analysis framework for analyzing the program of millions lines of code in minutes.

## Getting started

As the distributed analysis framework is implemented atop the general distributed graph processing platform (i.e., Apache Giraph), ensure that you have already installed Java and Hadoop before employing BigDataflow on your machines.

Versions for use with BigDataflow:

-  jdk version >= 1.8.0.
-  Apache Hadoop >= 2.8.5 & Redis >= 5.0.0 , or EMR version >= 3.45.0

Apache Hadoop has three different installation modes: `Standalone`, `Pseudo-distributed`, and `Fully Distributed`.

If you plan to run BigDataflow on the cloud (i.e. in `Fully Distributed` mode), you can just skip the ***Installing Hadoop in Local Mode*** part and directly employ BigDataflow with no need for installing `hadoop`. Otherwise, you have to prepare a Hadoop environment on your local machine/cluster as follows.

### Installing Hadoop in Local Mode*

1, Download the Hadoop file.

```bash
$ wget https://archive.apache.org/dist/hadoop/common/hadoop-2.8.5/
$ tar -xzf hadoop-2.8.5.tar.gz
$ cd hadoop-2.8.5
```

2, vi `/etc/hosts`

```bash
127.0.0.1       localhost
```

3, vi `etc/hadoop/core-site.xml`

**Remember to change the Hadoop path according to your hadoop-2.8.5 installation directory**

```xml
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:8000</value>
  </property>
</configuration>
```

4, vi `etc/hadoop/hdfs-site.xml`

```xml

<configuration>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/path/to/your/hadoop-2.8.5/tmp/dfs/namenode</value>
    </property>

    <property>
        <name>dfs.datanode.incre_data.dir</name>
        <value>/path/to/your/hadoop-2.8.5/tmp/dfs/datanode</value>
    </property>

    <property>
        <name>dfs.replication</name>
        <value>3</value>
    </property>
</configuration>
```

5, vi `etc/hadoop/yarn-site.xml`

```xml
<configuration>
	<!-- Site specific YARN configuration properties -->
    <property>
            <name>yarn.acl.enable</name>
            <value>0</value>
    </property>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>localhost</value>
    </property>
    <property>
        <name>yarn.nodemanager.vmem-check-enabled</name>
        <value>false</value>
    </property>
    <property>
        <name>yarn.nodemanager.resource.memory-mb</name>
        <value>28672</value>
    </property>
    <property>
        <name>yarn.scheduler.maximum-allocation-mb</name>
        <value>28672</value>
    </property>
    <property>
        <name>yarn.scheduler.maximum-allocation-vcores</name>
        <value>32</value>
    </property>
</configuration>
```

6, vi `etc/hadoop/mapred-site.xml`

```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
        <name>mapreduce.application.classpath</name>
        <value>/path/to/your/hadoop-2.8.5/share/hadoop/giraph/*:/path/to/your/hadoop-2.8.5/share/hadoop/mapreduce/*:/path/to/your/hadoop-2.8.5/share/hadoop/mapreduce/lib/*</value>
    </property>
    <property>
        <name>mapreduce.job.counters.limit</name>
        <value>20000</value>
    </property>
    <property>
        <name>mapred.tasktracker.map.tasks.maximum</name>
        <value>4</value>
    </property>
    <property>
        <name>mapred.map.tasks</name>
        <value>4</value>
    </property>
</configuration>
```

7, Format NameNode

```bash
$ cd hadoop-2.8.5/bin && hdfs namenode -format
```

8, Start HDFS and YARN

```bash
$ cd ../sbin/start-all.sh
```

### Installing Redis in Local Mode

1, Download the Redis file.

```bash
$ wget https://download.redis.io/releases/redis-5.0.9.tar.gz
$ tar xzf redis-5.0.9.tar.gz
$ cd redis-5.0.9
$ make -j4
```

2, Test Redis.

```Bash
$ cd redis-5.0.9/src
$ ./redis-cli 
```

In the redis-cli, you can type PING to check if the connection is successful.

### Implementing the APIs of BigDataflow according to the specific analysis

1, Download the entire BigDataFlow code.

```bash
$ git clone git@github.com:BigDataflow-system/BigDataflow.git
```

2, Interfaces under the `BigDataflow/analysis/src/main/java/incre_data`

`Fact`: Fact of dataflow analysis. 

`Msg`:   Message of dataflow analysis, consisting of target vertex's id and the outgoing fact of the current vertex.

`Stmt`: Statement at each vertex of CFG.

`StmtList`: Statement list at each vertex of CFG.

`Tool` : Tool of dataflow analysis, consisting of merging, transferring, and propagating operations.

`VertexValue`: Vertex attribute of each vertex in CFG, consisting of its statement list and fact.

3, Interfaces under the `BigDataflow/analysis/src/main/java/incre_analysis`

`IncreAnalysis` : The implementation of the incremental distributed worklist algorithm. Users can instantiate the `Msg`, `Fact`, `Tool` by overriding its functions according to their client analysis.

`MyWorkerContext`: Users can implement methods that executed on each Worker before superstep starts and after superstep ends

4, After implementing the interfaces, compile the code to produce the jar

```bash
# compile the client analysis
$ cd Analysis
$ mvn clean package
$ cd ..

# prepare the jar of giraph under your own jar_directoty
$ cp emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar jar_directoty/
$ cd jar_directoty/
$ mv emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar giraph-examples-1.4.0-SNAPSHOT-shaded.jar

# add your analysis class files into the jar
$ cd target/classes/
$ cp -r analysis/ incre_data/ your_analysis/ your_data/ jar_directoty/
$ cd jar_directoty/
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./incre_analysis/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./incre_data/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./incre_your_analysis/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./your_data/*.class ;\
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./analysis/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./reach_analysis/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./reach_data/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./put_proc/*.class
```

5, Put your analysis jar into your corresponding Hadoop directory

```bash
# jar directory at local
$ cp jar_directoty/giraph-examples-1.4.0-SNAPSHOT-shaded.jar /path/to/your/hadoop-2.8.5/share/hadoop/giraph

# jar directory on the cloud
$ cp jar_directoty/giraph-examples-1.4.0-SNAPSHOT-shaded.jar /opt/apps/extra-jars
```

### Usage Examples on the Cloud

There are some parameters used to fit your own analysis and running environment:

`-vif` : the vertex input format of specific dataflow analysis

`-vip` : the vertex input path on HDFS of specific dataflow analysis

`-vof` : the vertex output format of specific dataflow analysis

`-vsd` : the nodes output path on HDFS of specific dataflow analysis

`-eif` : the edge input format of specific dataflow analysis

`-eip` : the edge input path of specific dataflow analysis

`-esd` : the edge output path on HDFS of specific dataflow analysis

`-op`  : the parent directory output path on HDFS for `-vsd` and `-esd` of specific dataflow analysis

`-wc`  : WorkerContext class, used by the workers to access the global incre_data

`-mc`  : MasterBroadcast Class, used by the master to broadcast the entries of the  CFG

`-w `  : number of workers to use for computation

`-ca ` : custom arguments include maximum milliseconds to wait before giving up waiting for the workers and heap memory limits for the workers. For example, if each physical core of one node can not exceed 10GB, then the heap memory is set to 10×1024 = 10240MB.


**Put old analysis results to Redis**

1, Test the connection to Redis

```bash
$ cd  /mnt/disk1/redis-5.0.9/src # where you install redis-5.0.9
$ ./redis-cli  -h r-xxx.redis.rds.aliyuncs.com -p 6379 # r-xxx.redis.rds.aliyuncs.com refers to a Redis instance hosted on Alibaba Cloud's RDS
```

2, Prepare the Files and Check the old results on HDFS

```bash
$ touch edge_empty
$ hadoop fs -put edge_empty /  # an empty file to save output in this step on HDFS
$ hadoop fs -ls /alias_opt_res # the old analysis results on HDFS
```

3, Put old results to Redis

```bash
i=XX # i is set according to the predicted number of workers
j=YY # j is set according to the number of workers used in optimized analysis

if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
  org.apache.giraph.GiraphRunner put_proc.PutAnalysis \
  -vif reach_analysis.ReachVertexInputFormat \
  -vip /alias_opt_res/CFG_W"$j" \
  -vof put_proc.PutVertexOutputFormat \
  -op /alias_put_res/null/CFG_W"$i" \
  -eip /edge_empty \
  -w "$i" \
  -ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
  > /alias_put_W"$i"_result.txt 2>&1
then
  echo "alias, put------Success"
else
  echo "alias, put------Fail"
fi
```


**Running Reachability Analysis**

We use reachability analysis for alias analysis as an example. For different analysis, you won't need to change the API in reachability analysis, just put the CFG files into related HDFS dir.

1, Prepare the CFG Files for Reachability Analysis

```bash
# prebuild HDFS dir for Reachability analysis
$ hadoop fs -mkdir -p /alias_bench/diff_CFG

# put diff-CFG(CFG with a batch of updates) Files on HDFS
$ hadoop fs -put /alias_bench/diff_CFG/final /alias_graphs/diff_CFG  && \
hadoop fs -put /alias_bench/diff_CFG/id_stmt_info.txt /alias_graphs/diff_CFG

$ hadoop fs -mv /alias_graphs/diff_CFG/id_stmt_info.txt /alias_graphs/diff_CFG/id_stmt_info
```

2, Run Reachability Analysis

```bash
i=XX # i is set according to the predicted number of workers
startdate=$(date "+%Y-%m-%d %H:%M:%S")
echo "$startdate"

if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
  org.apache.giraph.GiraphRunner reach_analysis.ReachAnalysis \
  -vif reach_analysis.ReachVertexInputFormat \
  -vip /alias_graphs/diff_CFG/id_stmt_info \
  -vof reach_analysis.ReachVertexOutputFormat \
  -vsd /alias_reach_res/CFG_W"$i"/nodes \
  -eif reach_analysis.ReachEdgeInputFormat \
  -eip /alias_graphs/diff_CFG/final \
  -eof reach_analysis.ReachEdgeOutputFormat \
  -esd /alias_reach_res/CFG_W"$i"/edges \
  -op /alias_reach_res/CFG_W"$i" \
  -w "$i" \
  -ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
  > /alias_reach_res_CFG_W"$i"_result.txt 2>&1
then
  enddate=$(date "+%Y-%m-%d %H:%M:%S")
  echo "$enddate"
  echo "$startdate" >> /alias_reach_res_CFG_W"$i"_result.txt 2>&1
  echo "$enddate" >> /alias_reach_res_CFG_W"$i"_result.txt 2>&1
  echo "alias, reach------Success"
else
  enddate=$(date "+%Y-%m-%d %H:%M:%S")
  echo "$enddate"
  echo "$startdate" >> /alias_reach_res_CFG_W"$i"_result.txt 2>&1
  echo "$enddate" >> /alias_reach_res_CFG_W"$i"_result.txt 2>&1
  echo "alias, reach------Fail"
fi
```

3, Check the results

You can get and check the sub-CFG after executing the following command.

```bash
# supposed i=100
$ hadoop fs -getmerge /alias_reach_res/CFG_W100/nodes  alias_reach_res_nodes.res # nodes in sub-CFG is saved to alias_reach_res_nodes.res
$ hadoop fs -getmerge /alias_reach_res/CFG_W100/edges  alias_reach_res_edges.res # edges in sub-CFG is saves to alias_reach_res_edges.res
```

**Running Incre Alias Analysis**

1, vi `alias_analysis_conf` for each CFG of a program

If the BigDataflow is run locally, the content of `alias_analysis_conf.CFG` for each CFG is as follows:

```bash
hdfs://localhost:8000/alias_graphs/CFG/singleton
hdfs://localhost:8000/grammar
```

Else, set the content of `alias_analysis_conf.CFG` as follows if BigDataflow is run on the [EMR](https://www.alibabacloud.com/product/emapreduce) cluster:

```bash
# the `ClusterID` is automatically assigned by the EMR service
hdfs://emr-header-1.cluster-ClusterID:9000/alias_graphs/CFG/singleton
hdfs://emr-header-1.cluster-ClusterID:9000/grammar
```

2, Put `alias_analysis_conf.CFG` and `grammar file` on HDFS dir

```bash
$ ./hadoop fs -put alias_analysis_conf.CFG /client
$ ./hadoop fs -put /path/to/AliasAnalysis/grammar /
```

3, Prepare the CFG Files for Incre Alias Analysis

Please check the nodes output and edges output in reachability analysis first.

```bash
# supposed i=100 in reachability analysis
$ hadoop fs -getmerge /alias_reach_res/CFG_W100/nodes  alias_reach_res_nodes.res # nodes in sub-CFG is saved to sub_nodes.res
$ hadoop fs -getmerge /alias_reach_res/CFG_W100/edges  alias_reach_res_edges.res # edges in sub-CFG is saves to sub_edges.res
```

Then execute the following command:

```bash
# put singleton File for alias analysis on HDFS  
$ hadoop fs -put /alias_bench/CFG/var_singleton_info.txt /alias_graphs/CFG
$ hadoop fs -mv /alias_graphs/CFG/var_singleton_info.txt /alias_graphs/CFG/singleton
```

4, Run Incre Alias Analysis

```bash
$ hadoop fs -mv /client/alias_analysis_conf.CFG /client/analysis_conf

i=XX # i is set according to the predicted number of workers
j=YY # j is set according to the number of workers used in reachability analysis
startdate=$(date "+%Y-%m-%d %H:%M:%S")
echo "$startdate"

if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
  org.apache.giraph.GiraphRunner incre_alias_analysis.IncreAliasAnalysis \
  -vif incre_alias_analysis.IncreAliasVertexInputFormat \
  -vip /alias_reach_res/CFG_W"$j"/nodes \
  -vof alias_analysis.AliasVertexOutputFormat \
  -op /alias_incre_res/CFG_W"$i" \
  -eif incre_alias_analysis.IncreAliasEdgeInputFormat \
  -eip /alias_reach_res/CFG_W"$j"/edges \
  -wc incre_alias_analysis.IncreAliasWorkerContext \
  -w "$i" \
  -ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
  > /alias_incre_res_CFG_W"$i"_result.txt 2>&1
then
  enddate=$(date "+%Y-%m-%d %H:%M:%S")
  echo "$enddate"
  echo "$startdate" >> /alias_incre_res_CFG_W"$i"_result.txt 2>&1
  echo "$enddate" >> /alias_incre_res_CFG_W"$i"_result.txt 2>&1
  echo "alias, incre------Success"
else
  enddate=$(date "+%Y-%m-%d %H:%M:%S")
  echo "$enddate"
  echo "$startdate" >> /alias_incre_res_CFG_W"$i"_result.txt 2>&1
  echo "$enddate" >> /alias_incre_res_CFG_W"$i"_result.txt 2>&1
  echo "alias, incre------Fail"
fi

$ hadoop fs -mv /client/analysis_conf /client/alias_analysis_conf.CFG 
```

5, Check the results

You can see the results in the file `alias_incre_CFG.res` after executing the following command.

```bash
# supposed i=100
$ hadoop fs -cat /alias_incre_res/CFG_W100/p* > alias_incre_CFG.res
```

**Running Incre Cache Analysis**

1, Check the nodes output and edges output in reachability analysis

```bash
# supposed i=100 in reachability analysis
$ hadoop fs -getmerge /cache_reach_res/CFG_W100/nodes  cache_reach_res_nodes.res # nodes in sub-CFG is saved to sub_nodes.res
$ hadoop fs -getmerge /cache_reach_res/CFG_W100/edges  cache_reach_res_edges.res # edges in sub-CFG is saves to sub_edges.res
```

2, Run Cache Analysis

```bash

i=XX # i is set according to the predicted number of workers
j=YY # j is set according to the number of workers used in reachability analysis
startdate=$(date "+%Y-%m-%d %H:%M:%S")
echo "$startdate"

$ if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
  org.apache.giraph.GiraphRunner incre_cache_analysis.IncreCacheAnalysis \
  -vif incre_cache_analysis.IncreCacheVertexInputFormat \
  -vip /cache_reach_res/CFG_W"$j"/nodes \
  -vof cache_analysis.CacheVertexOutputFormat \
  -op /cache_incre_res/CFG_W"$i" \
  -eif incre_cache_analysis.IncreCacheEdgeInputFormat \
  -eip /cache_reach_res/CFG_W"$j"/final \
  -wc incre_cache_analysis.IncreCacheWorkerContext \
  -w "$i" \
  -ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
  > /cache_incre_res_CFG_W"$i"_result.txt 2>&1
then
   enddate=$(date "+%Y-%m-%d %H:%M:%S")
   echo "$enddate"
   echo "$startdate" >> /cache_incre_res_CFG_W"$i"_result.txt 2>&1
   echo "$enddate" >> /cache_incre_res_CFG_W"$i"_result.txt 2>&1
   echo "cache, incre------Success"
else
   enddate=$(date "+%Y-%m-%d %H:%M:%S")
   echo "$enddate"
   echo "$startdate" >> /cache_incre_res_CFG_W"$i"_result.txt 2>&1
   echo "$enddate" >> /cache_incre_res_CFG_W"$i"_result.txt 2>&1
   echo "cache, incre------Fail"
fi
```

3, Check the results

You can see the results in the file `cache_incre_CFG.res` after executing the following command.

```bash
# supposed i=100
$ hadoop fs -cat /cache_incre_res/CFG_W100/p* > cache_incre_CFG.res
```
