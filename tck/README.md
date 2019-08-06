# Running against API and TCK from Eclipse staging environment

./get-artifact.sh
./artifacts-install.sh
cd ..
mvn clean install -pl modules
mvn clean install -pl tck-runner -Dincontainer
