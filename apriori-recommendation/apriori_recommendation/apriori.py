from apriori_recommendation.singleton import Singleton
from apyori import apriori, TransactionManager


class Apriori(metaclass=Singleton):
    def __init__(self, transactions):
        self.freq = TransactionManager(transactions)

    def update(self, invoice):
        self.freq.add_transaction(list(map(lambda x: x['stockCode'], invoice['items'])))

    def support(self, items):
        return self.freq.calc_support(items)

    def apriori(self, q):
        return [{'items': a.items, 'support': a.support, 'statistics': [{'items_add': s.items_add, 'items_base': s.items_base, 'confidence': s.confidence, 'lift': s.lift} for s in a.ordered_statistics]} for a in apriori(self.freq, **q)]
