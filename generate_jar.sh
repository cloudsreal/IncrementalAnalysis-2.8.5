cd ./Analysis/target/classes

mkdir ./new_jar

chmod +x ./new_jar

cp -r reach_analysis data analysis reach_data pre_analysis pre_data ./new_jar
cp ../../../emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar ./new_jar

cd new_jar
mv emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar giraph-examples-1.4.0-SNAPSHOT-shaded.jar

jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../data/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../reach_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../reach_data/*.class
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../pre_analysis/*.class ; \
jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ../pre_data/*.class

cp giraph-examples-1.4.0-SNAPSHOT-shaded.jar /Users/zhangyujin/Desktop/jar