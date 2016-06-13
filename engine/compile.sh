mkdir -p target/generated-sources/jaxb;

xjc -enableIntrospection -p org.hibernate.validator.internal.xml -extension -target 2.1 -d target/generated-sources/jaxb src/main/xsd/validation-configuration-1.1.xsd src/main/xsd/validation-mapping-1.1.xsd -b src/main/xjb/binding-customization.xjb;

echo "Compiling";

javac -addmods java.xml.bind,java.annotations.common \
    -g \
    -modulepath ../../module-test/mods:../../module-test/lib \
    -processorpath ../../module-test/tools/jbossloggingprocessor.jar:../../module-test/lib/jbossloggingannotations.jar:../../module-test/lib/jbosslogging.jar:../../module-test/tools/jdeparser.jar:../../module-test/tools/jsr250-api.jar \
    -d ../../module-test/mods/org.hibernate.validator.engine \
    $(find src/main/java -name "*.java") $(find target/generated-sources/jaxb -name "*.java");

cp -r src/main/resources/* ../../module-test/mods/org.hibernate.validator.engine;
