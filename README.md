# amazon-sambhav

### To clean & rebuild the project in case of dependency collision (TODO: CLEAN CACHE COMPLETELY)

```bash
mvn clean
mvn dependency:purge-local-repository
mvn install
