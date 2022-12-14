# Invoice transformer

Implements kafka streaming. It reads invoices from `invoices` topic, splits them into segments and pushes them to `segments` topic.

## Build
Run `mvn clean package` (needs [entity](..%2Fentity) module to be built first)

## Run
After build run `java -jar target/quarkus-app/quarkus-run.jar`
