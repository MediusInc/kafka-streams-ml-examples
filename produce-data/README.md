# Data producer

On start it reads data from csv and pushes it to `invoices` kafka topic

## Build
Run `mvn clean package` (needs [entity](..%2Fentity) module to be built first)

## Run
After build run `java -jar target/quarkus-app/quarkus-run.jar`
