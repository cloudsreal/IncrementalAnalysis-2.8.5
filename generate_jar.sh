cd ./Analysis/target/classes

mkdir ./new_jar

chmod +x ./new_jar

cp -r alias_data alias_stmt analysis_reach cache_data data_incre data_reach incre_alias_analysis incre_analysis incre_cache_analysis put_proc reach_analysis reach_data ./new_jar
cp ../../../giraph-examples-1.4.0-SNAPSHOT-shaded-redis.jar ./new_jar

cd new_jar

mv giraph-examples-1.4.0-SNAPSHOT-shaded-redis.jar giraph-examples-1.4.0-SNAPSHOT-shaded.jar

jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../alias_data/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../alias_stmt/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../analysis_reach/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../cache_data/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../data_incre/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../data_reach/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../incre_cache_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../incre_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../incre_alias_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../put_proc/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../reach_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../reach_data/*.class ; \

cp giraph-examples-1.4.0-SNAPSHOT-shaded.jar /Users/zhangyujin/Desktop/0706-jar