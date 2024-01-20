from enum import Enum


class AST(Enum):
    Reproduction = 1
    Type = 2
    Value = 3
    OptValue = 4
    TypeOne = 10
    TypeTwo = 11
    TypeUniform = 12
    Number = 5
    Epsilon = -1
    Digit = 6
    RandomDigit = 7
    DigitZero = 20
    DigitOne = 21
    DigitTwo = 22
    DigitThree = 23
    DigitFour = 24
    DigitFive = 25
    DigitSix = 26
    DigitSeven = 27
    DigitEight = 28
    DigitNine = 29


nonterminals = [
    AST.Reproduction,
    AST.Type,
    AST.Value,
    AST.OptValue,
    AST.Number,
    AST.Digit,
]

# this structure has a key of a non-terminal and then a list of
# valid productions for that nonterminal listed as the production
# elements
productions = {
    AST.Reproduction: [
        [AST.Type, AST.Value,
            AST.Epsilon, AST.OptValue]
    ],
    AST.Type: [
        [AST.TypeOne],
        [AST.TypeTwo],
        [AST.TypeUniform],
    ],
    AST.Value: [[AST.Number]],
    AST.OptValue: [[AST.Number], [AST.Epsilon]],
    AST.Number: [
        [AST.Digit],
        [AST.Digit, AST.Number],
    ],
    AST.Digit: [
        [AST.DigitZero],
        [AST.DigitOne],
        [AST.DigitTwo],
        [AST.DigitThree],
        [AST.DigitFour],
        [AST.DigitFive],
        [AST.DigitSix],
        [AST.DigitSeven],
        [AST.DigitEight],
        [AST.DigitNine],
        [AST.RandomDigit],
    ],
}


def is_nonterminal(val: AST) -> bool:
    return val in nonterminals


def is_terminal(val: AST) -> bool:
    return val not in nonterminals
