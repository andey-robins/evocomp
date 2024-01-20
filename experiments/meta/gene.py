class Gene:

    codons = []
    draw_idx = 0

    wraps = 0
    max_wraps = 0

    def __init__(self, gene: list) -> None:
        self.codons = gene
        self.draw_idx = 0
        self.wraps = 0
        self.max_wraps = self.draw_next()

    def draw_next(self) -> int:
        """
        Returns the next codon in the gene, or -1 if the gene is inexpressible
        currently returns -1 when it fails to draw a codon (i.e. too many wraps)
        """
        codon = self.codons[self.draw_idx]
        self.draw_idx += 1
        if self.draw_idx == len(self.codons):
            self.draw_idx = 0
            self.wraps += 1
        if self.wraps > self.max_wraps:
            # TODO: better error handling, we need to somehow
            # indicate this gene is inexpressible
            return -1
        return codon
