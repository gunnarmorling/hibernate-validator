rm -rf target/test-classes;

mkdir target/test-classes;

cp -r src/test/resources/* target/test-classes;

javac \
    -parameters \
    -cp /Users/gunnar/.m2/repository/javax/validation/validation-api/1.1.0.Final/validation-api-1.1.0.Final.jar:/Users/gunnar/.m2/repository/org/hibernate/hibernate-validator/5.3.0-SNAPSHOT/hibernate-validator-5.3.0-SNAPSHOT.jar:../../module-test/lib-test/testng.jar:../../module-test/lib-test/festassert.jar:../../module-test/lib/jodatime.jar:../../module-test/lib/hibernatejpa.jar:../../module-test/lib-test/easymock.jar:../../module-test/lib-test/log4j.jar:../../module-test/lib/classmate.jar:../../module-test/lib/paranamer.jar:../../module-test/lib/jbosslogging.jar:../test-utils/target/hibernate-validator-test-utils-5.3.0-SNAPSHOT.jar \
    -d target/test-classes \
    -g \
    $(find src/test/java -name "*.java")