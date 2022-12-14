config = {
    'ELASTIC_HOSTS': ['http://localhost:9200'],
    'BOOTSTRAP_SERVERS': 'localhost:9092',

}


def get_config(key):
    return config[key]
