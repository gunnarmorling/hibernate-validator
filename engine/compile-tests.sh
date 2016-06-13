rm -rf target/test-classes;

mkdir target/test-classes;

rm -rf target/test-classes;

mkdir target/test-classes;

cp -r src/test/resources/* target/test-classes;

javac \
    -Xmodule:org.hibernate.validator.engine \
    -addmods org.hibernate.validator.testutil \
    -modulepath ../../module-test/mods:../../module-test/lib:../../module-test/lib-test \
    -d target/test-classes \
    -cp ../../module-test/lib-test/testng.jar:../../module-test/lib-test/easymock.jar \
    -g \
    $(find src/test/java -name "*.java")