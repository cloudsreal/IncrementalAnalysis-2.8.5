cd ./Analysis/target/classes

mkdir ./new_jar

chmod +x ./new_jar

cp -r put_proc ./new_jar
cp ../../../giraph-examples-1.4.0-SNAPSHOT-shaded.jar ./new_jar

cd new_jar

jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../put_proc/*.class ; \

cp giraph-examples-1.4.0-SNAPSHOT-shaded.jar /Users/zhangyujin/Desktop/jar