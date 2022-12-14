import pandas as pd

from apriori_recommendation.singleton import Singleton
from apriori_recommendation.utils import cosine_distance


class RecommenderSystem(metaclass=Singleton):
    def __init__(self):
        self.matrix = pd.DataFrame()
        self.updated_matrix = pd.DataFrame()
        self.count = 0

    def update(self, segment):
        if segment['itemId'] not in self.updated_matrix:
            self.updated_matrix[segment['itemId']] = 0
        if segment['customerId'] not in self.updated_matrix.index:
            self.updated_matrix.loc[segment['customerId']] = [0] * len(self.updated_matrix.columns)
        self.updated_matrix.loc[segment['customerId'], segment['itemId']] += 1
        self.updated_matrix = self.updated_matrix.copy()
        self.matrix = self.updated_matrix.copy()

    def recommend_for_customer(self, customer_id):
        if customer_id not in self.matrix.index:
            return 'Customer does\'t exist yet'
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
