from typing import List

from fastapi import FastAPI, Query
import uvicorn

from apriori_recommendation.apriori import Apriori
from apriori_recommendation.elastic import get_recommendation, get_transaction_items
from apriori_recommendation.kafka import Kafka
from apriori_recommendation.recommendation import RecommenderSystem

app = FastAPI(
    title='Apriori Recommendation',
    description='API for priory Recommendation',
    version='0.0.1',
    docs_url="/",
    redoc_url=None
)

apriori = Apriori(get_transaction_items())
recommender_system = RecommenderSystem()

kafka = Kafka(['invoices', 'segments'], apriori, recommender_system)


@app.get('/recommend/{customer_id}')
def recommend_for_customer(customer_id: str):
    """
    Finds recommended items for customer with id
    """
    return recommender_system.recommend_for_customer(customer_id)


@app.get('/elastic/recommend/{customer_id}')
def recommend_for_customer_elastic(customer_id: str):
    """
   Finds recommended items for customer with id based on vector similarity from elasticsearch
   """
    return get_recommendation(customer_id)


@app.get('/apriori')
def calculate_apriori(min_support: float = 0.1, min_confidence: float = 0.0, min_lift: float = 0.0, max_length: int = None):
    """
    Runs apriori algorithm and returns items/baskets based on additional parameters
    """
    return apriori.apriori({'min_support': min_support, 'min_confidence': min_confidence, 'min_lift': min_lift, 'max_length': max_length})


@app.get('/support')
def calculate_support(items: List = Query()):
    """
    Calculates support for given items
    """
    return apriori.support(items)


@app.on_event("startup")
async def startup_event():
    kafka.start()


@app.on_event('shutdown')
async def shutdown_event():
    kafka.close()
    kafka.join()


if __name__ == '__main__':
    uvicorn.run('apriori_recommendation.main:app')

