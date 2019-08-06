#!/usr/bin/env bash

TCK_DIST=/home/build/bv/hibernate-validator/tck/beanvalidation-tck-dist-2.0.5

# API
mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file \
-Dfile=jakarta.validation-api-2.0.2.jar -DgroupId=jakarta.validation \
-DartifactId=jakarta.validation-api -Dversion=2.0.2 -Dpackaging=jar

# Parent pom
mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file \
-Dfile=${TCK_DIST}/src/pom.xml -DgroupId=org.hibernate.beanvalidation.tck \
-DartifactId=beanvalidation-tck-parent -Dversion=2.0.5 -Dpackaging=pom

mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file \
-Dfile=${TCK_DIST}/artifacts/beanvalidation-tck-tests-2.0.5.jar -DgroupId=org.hibernate.beanvalidation.tck \
-DartifactId=beanvalidation-tck-tests -Dversion=2.0.5 -Dpackaging=jar

mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file \
-Dfile=${TCK_DIST}/artifacts/beanvalidation-standalone-container-adapter-2.0.5.jar -DgroupId=org.hibernate.beanvalidation.tck \
-DartifactId=beanvalidation-standalone-container-adapter -Dversion=2.0.5 -Dpackaging=jar

mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file \
-Dfile=${TCK_DIST}/artifacts/tck-tests.xml -DgroupId=org.hibernate.beanvalidation.tck \
-DartifactId=beanvalidation-tck-tests -Dversion=2.0.5 -Dpackaging=xml -Dclassifier=suite
