from elasticsearch import Elasticsearch

def get_elasticsearch():
    es = es.e(
        ['http://localhost:9200'],
        timeout=60,
        use_ssl=False,
    )
    try:
        yield es
    except Exception as e:
        print('Couldn\'t connect to opensearch', exc_info=e)
