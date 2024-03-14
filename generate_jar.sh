cd Analysis/target/classes;

mkdir ./jar_dir

chmod +x ./jar_dir
cp -r incre_analysis data incre_cache_analysis cache_data ./jar_dir

cp ../../../emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar ./jar_dir

cd ./jar_dir
mv emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar giraph-examples-1.4.0-SNAPSHOT-shaded.jar
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./data/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./incre_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./incre_cache_analysis/*.class; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./cache_data/*.class

cp ./giraph-examples-1.4.0-SNAPSHOT-shaded.jar /Users/zhangyujin/hadoop-2.7.2/share/hadoop/giraph
