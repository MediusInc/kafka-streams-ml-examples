# Example of kafka streams with ML
This repository shows how to create a simple flow of data through kafka. We load initial data from csv using module [produce-data](produce-data) (this is used to demonstrate producing of data to kafka).
Data is loaded to first kafka topic `invoices`. From there logstash picks it up and sends data to elasticsearch. 
[invoice-transformer](invoice-transformer) (this is used to show kafka streaming from one topic to another while changing or enriching data with additional information).
Finally [apriori-recommendation](apriori-recommendation) module which implements apriori algorithm and recommendation system. Information is updated each time a new invoice comes to `invoice` topic
(this modul shows how to consume data from kafka).

## Requirements for running this example
* java 17
* maven
* python 3.10
* docker (docker-compose)


### Build
From root run `mvn clean package`. This will build all java modules (every program except [apriori-recommendation](apriori-recommendation), check [README.md](apriori-recommendation%2FREADME.md) there).

### Run
First you need to start all the dockers by running command `docker-compose up`. This will create a running instance of kafka (redpanda flavor), console for kafka (to check topics), 
elasticsearch, logstash and kibana (for creating graphs and analyzing data)

Each other module has its own README.md with instructions on how to run the program.

After you send some initial data to first kafka topic, logstash will pick it up and send it to elasticsearch. You can then go to kibana and under navigation choose:
`Stack Management` -> [Saved Objects](http://localhost:5601/app/management/kibana/objects) and then click on `Import` (top right corner). Import [export.ndjson](kibana_dashboard%2Fexport.ndjson) file into it which will create a basic dashboard. 
You can then navigate to [Dashboard](http://localhost:5601/app/dashboards) and check the imported graphs.
