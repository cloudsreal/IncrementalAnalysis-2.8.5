cd ./Analysis/target/classes

mkdir ./new_jar

chmod +x ./new_jar

cp -r alias_analysis alias_data alias_stmt analysis cache_analysis cache_data data put_proc ./new_jar
cp ../../../giraph-examples-1.4.0-SNAPSHOT-shaded-redis.jar ./new_jar

cd new_jar

mv giraph-examples-1.4.0-SNAPSHOT-shaded-redis.jar giraph-examples-1.4.0-SNAPSHOT-shaded.jar

jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../data/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../cache_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../cache_data/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../alias_data/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../alias_stmt/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../alias_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../put_proc/*.class ; \

mv giraph-examples-1.4.0-SNAPSHOT-shaded.jar giraph-examples-1.4.0-SNAPSHOT-shaded-opt-put-116.jar

cp giraph-examples-1.4.0-SNAPSHOT-shaded-opt-put-116.jar /Users/zhangyujin/Desktop/Experiment/jar