mkdir ./target/classes/jar_directory
cp ../add_jar.sh ./target/classes/jar_directory
cp ../emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar ./target/classes/jar_directory
cd ./target/classes/jar_directory
mv emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar giraph-examples-1.4.0-SNAPSHOT-shaded.jar
chmod +x add_jar.sh
./add_jar.sh

