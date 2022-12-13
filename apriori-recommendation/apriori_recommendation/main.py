import asyncio
import time
from concurrent.futures import ThreadPoolExecutor
from threading import Thread

from fastapi import FastAPI
import uvicorn

from apriory_recommendation.apriory import Apriory
from apriory_recommendation.kafka import Kafka
from apriory_recommendation.recommendation import RecommenderSystem

app = FastAPI(
    title='Apriori Recommendation',
    description='API for priory Recommendation',
    version='0.0.1',
    docs_url="/",
    redoc_url=None
)

apriory = Apriory()
recommender_system = RecommenderSystem()

kafka = Kafka(['invoices', 'segments'], apriory, recommender_system)


@app.get('/recommend/{customer_id}')
def recommend_for_customer(customer_id: str):
    return recommender_system.recommend_for_customer(customer_id)


@app.get('/apriori')
def recommend_for_customer():
    return apriory.apriori()


@app.on_event("startup")
async def startup_event():
    loop = asyncio.get_running_loop()
    loop.run(None, kafka.run)


@app.on_event('shutdown')
async def shutdown_event():
    kafka.stop()


if __name__ == '__main__':
    uvicorn.run('apriory_recommendation.main:app')

