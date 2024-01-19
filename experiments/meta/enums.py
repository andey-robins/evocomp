from enum import Enum


class CrossoverType(Enum):
    ONE_POINT = 1
    TWO_POINT = 2
    UNIFORM = 3


class GrammarTokenStatus(Enum):
    TERMINAL = 1
    NONTERMINAL = 2
