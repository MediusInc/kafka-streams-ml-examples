import pandas as pd

from apriory_recommendation.singleton import Singleton
from apriory_recommendation.utils import cosine_distance


class RecommenderSystem(metaclass=Singleton):
    def __init__(self):
        self.matrix = pd.DataFrame()

    def update(self, segment):
        if segment['itemId'] not in self.matrix:
            self.matrix[segment['itemId']] = 0
        if segment['customerId'] not in self.matrix.index:
            self.matrix.loc[segment['customerId']] = [0] * len(self.matrix.columns)
        self.matrix.loc[segment['customerId'], segment['itemId']] += 1
        self.matrix = self.matrix.copy()

    def recommend_for_customer(self, customer_id):
        customer_items = self.matrix.loc[customer_id]
        distances = []
        for index, row in self.matrix.drop(customer_id).iterrows():
            distances.append((index, cosine_distance(customer_items, row)))
        distances.sort()
        top_3 = list(map(lambda x: x[0], distances[:3]))
        items = set()
        columns = self.matrix.columns
        for customer in top_3:
            other_items = columns[self.matrix.loc[customer] != 0]
            items.update(set(other_items).difference(set(columns[customer_items != 0])))
        return list(items)[:5]
