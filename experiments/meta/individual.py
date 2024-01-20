from typing import List
from gene import Gene
from grammar import AST, is_nonterminal, productions


class Individual:
    meta_gene = None
    data_gene = None

    def __init__(self) -> None:
        # random values  for testing
        self.meta_gene = Gene([2, 2, 99, 4, 15, 6, 7, 88, 9])
        self.data_gene = Gene([1, 12, 57, 3, 34, 18, 78])

    def decode_crossover_strategy(self) -> List:
        strategy = [AST.Reproduction]
        curr_idx = 0

        while not self.is_fully_decoded(strategy):
            curr_token = strategy[curr_idx]
            if is_nonterminal(curr_token):
                # draw a list (corresponding to a production) from
                # the grammar based on the next codon
                expansion_option_count = len(productions[curr_token])
                # defaults to 0 since we only have one available production to index
                drawn_gene = 0
                # we only draw when we actually need to make a decision
                if expansion_option_count != 1:
                    drawn_gene = self.meta_gene.draw_next()
                    if drawn_gene == -1:
                        return []

                # when we didn't need to draw, this is equivalent to grammar_idx = 0
                grammar_idx = drawn_gene % expansion_option_count
                chosen_expansion = productions[curr_token][grammar_idx]

                # replace the non-terminal with the chosen expansion
                strategy = strategy[:curr_idx] + \
                    chosen_expansion + strategy[curr_idx + 1:]
            else:
                curr_idx += 1

        return strategy

    def is_fully_decoded(self, strat: List) -> bool:
        for e in strat:
            if is_nonterminal(e):
                return False
        return True

    def display_info(self) -> None:
        print(f"Gene name: {self.name}")
