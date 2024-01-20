Question:

Can reproduction rules be encoded within a gene?

Requirements:
- Gene object which can be evaluated for fitness
  - Two parts, a meta-data and data level
  - Fitness only comes from the data
  - Meta-data is only used during reproduction
- Population object which can evaluate a population of genes
  - and can also report intermediary stats information
- Reproduction function which:
  - Decodes the meta-data of each gene and
  - Derives a reproduction strategy for producing the offspring

Reproduction Options:
- One point crossover
- Two point crossover
- Uniform crossover

Grammar:
- Reproduction => Type Value e OptValue
- Type => "one" | "two" | "uni"
- Value => Number
- OptValue => Number | e
- Number => Digit | Digit, Number
- Digit => "1" | "2" | ... | "9" | "0" | "randDigit"

We then decode the reproduction rules from the meta-data gene with wrapping until we fully expand the meta-data gene or run into a stopping condition (to be later defined).

For one point crossover, the first digit needs to somehow map to a point on the codon somewhat randomly
For two point, the same but for both points
For uniform, the first value will be the bias for the coin

An idea which needs more vetting: throwing erros within the individual level of simulation and catching them in the population level as a way to easily handle bad genes and penalize their fitness simultaneously