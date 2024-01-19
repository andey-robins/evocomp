from enum import Enum


class GrammarElement(Enum):
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
    GrammarElement.Reproduction,
    GrammarElement.Type,
    GrammarElement.Value,
    GrammarElement.OptValue,
    GrammarElement.Number,
    GrammarElement.Digit,
]

# this structure has a key of a non-terminal and then a list of
# valid productions for that nonterminal listed as the production
# elements
grammar = {
    GrammarElement.Reproduction: [
        [GrammarElement.Type, GrammarElement.Value, GrammarElement.OptValue]
    ],
    GrammarElement.Type: [
        [GrammarElement.TypeOne],
        [GrammarElement.TypeTwo],
        [GrammarElement.TypeUniform],
    ],
    GrammarElement.Value: [[GrammarElement.Number]],
    GrammarElement.OptValue: [[GrammarElement.Number], [GrammarElement.Epsilon]],
    GrammarElement.Number: [
        [GrammarElement.Digit],
        [GrammarElement.Digit, GrammarElement.Number],
    ],
    GrammarElement.Digit: [
        [GrammarElement.DigitZero],
        [GrammarElement.DigitOne],
        [GrammarElement.DigitTwo],
        [GrammarElement.DigitThree],
        [GrammarElement.DigitFour],
        [GrammarElement.DigitFive],
        [GrammarElement.DigitSix],
        [GrammarElement.DigitSeven],
        [GrammarElement.DigitEight],
        [GrammarElement.DigitNine],
        [GrammarElement.RandomDigit],
    ],
}


def is_nonterminal(val: GrammarElement) -> bool:
    return val in nonterminals


def is_terminal(val: GrammarElement) -> bool:
    return val not in nonterminals
