from apriori_recommendation.singleton import Singleton
from apyori import apriori, TransactionManager


class Apriori(metaclass=Singleton):
    def __init__(self):
        self.freq = TransactionManager([])

    def update(self, invoice):
        self.freq.add_transaction(list(map(lambda x: x['stockCode'], invoice['items'])))

    def support(self, items):
        return self.freq.calc_support(items)

    def apriori(self):
        return apriori(self.freq)
