from typing import List
from grammar import GrammarElement, is_nonterminal


class Individual:
    meta_gene = []
    data_gene = []

    def __init__(self):
        pass

    def decode_crossover_strategy(self):
        strategy = [GrammarElement.Reproduction]
        curr = 0  # pointer to currently processing element

        gene_draw_idx = 0

        while not self.is_fully_decoded(strategy):
            if is_nonterminal(strategy[curr]):
                # do an insert here rather than just a replacement
                # should probably start getting some tests set up to verify this properly
                # draws from the gene and expands our productions

                gene_draw_idx += 1
                gene_draw_idx %= len(self.meta_gene)
            else:
                curr += 1

    def is_fully_decoded(self, strat: List) -> bool:
        for e in strat:
            if is_nonterminal(e):
                return False
        return True

    def display_info(self):
        print(f"Gene name: {self.name}")
