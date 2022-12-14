from elasticsearch import Elasticsearch

from apriori_recommendation.configuration import get_config


def get_elasticsearch():
    es = Elasticsearch(
        get_config('ELASTIC_HOSTS'),
        timeout=60
    )
    try:
        yield es
    except Exception as e:
        print('Couldn\'t connect to opensearch', e)


def get_transaction_items():
    query = {
        "match_all": {}
    }
    es = next(get_elasticsearch())
    response = es.search(index='invoices', query=query, size=100)
    transactions = []
    for invoice in response['hits']['hits']:
        transactions.append(list(map(lambda x: x['stockCode'], invoice['_source']['items'])))
    return transactions


def get_recommendation(customer_id):
    items = _get_customer_items(customer_id)
    query = {
        "terms": {
            'items.description.keyword': items
        }
    }
    aggs = {
        'recommendations': {
            'significant_terms': {
                'field': 'items.description.keyword',
                'size': 10000,
                'min_doc_count': 100,
                'exclude': items
            }
        }
    }
    buckets = next(get_elasticsearch()).search(index='invoices', query=query, aggs=aggs, size=0)['aggregations']['recommendations']['buckets']
    return [recommend['key'] for recommend in buckets][:5]


def _get_customer_items(customer_id):
    query = {
        'match': {
            'customerID': customer_id
        }
    }
    aggs = {
        'uniq_items': {
            'terms': {
                'field': 'items.description.keyword',
                'size': 10000
            }
        }
    }
    buckets = next(get_elasticsearch()).search(index='invoices', query=query, aggs=aggs, size=0)['aggregations']['uniq_items']['buckets']
    return [bucket['key'] for bucket in buckets]
