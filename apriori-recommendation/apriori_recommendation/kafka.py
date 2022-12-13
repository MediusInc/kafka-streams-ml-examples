import json
import threading

from confluent_kafka import DeserializingConsumer


class Kafka(threading.Thread):
    def __init__(self, topics, apriori, recommender_system):
        super().__init__()
        config = {
            'bootstrap.servers': 'localhost:9092',
            'client.id': 'segment_consumer_app',
            'enable.auto.commit': True,
            'auto.offset.reset': 'earliest',
            'group.id': 'segment_consumer_group',
            'value.deserializer': lambda v, ctx: json.loads(v.decode('utf-8'))
        }
        self.consumer = DeserializingConsumer(config)
        self.consumer.subscribe(topics)
        self.should_run = True
        self.apriori = apriori
        self.recommender_system = recommender_system

    def close(self):
        self.should_run = False

    async def run(self):
        self.should_run = True
        while self.should_run:
            msg = self.consumer.poll(5)
            if msg is not None:
                value = msg.value()
                if msg.topic() == 'segments':
                    self.recommender_system.update(value)
                else:
                    self.apriori.update(value)
        self.consumer.close()
