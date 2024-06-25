cd target/classes
rm *.jar
jar cfm HelloWorld.jar  ../../Manifest.txt Main.class

cp HelloWorld.jar ../..
jar uvf HelloWorld.jar redis/* ; \
jar uvf HelloWorld.jar org/*

# java -jar HelloWorld.jar 