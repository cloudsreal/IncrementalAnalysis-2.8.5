cd ./Analysis/target/classes

mkdir ./new_jar

chmod +x ./new_jar

cp -r incre_analysis data incre_cache_analysis cache_data ./new_jar
cp ../../../giraph-examples-1.4.0-SNAPSHOT-shaded.jar ./new_jar

cd new_jar

jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../incre_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../data/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../incre_cache_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../cache_data/*.class

cp giraph-examples-1.4.0-SNAPSHOT-shaded.jar /Users/zhangyujin/Desktop/jar