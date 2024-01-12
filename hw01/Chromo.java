
/******************************************************************************
 * A Teaching GA Developed by Hal Stringer & Annie Wu, UCF
 * Version 2, January 18, 2004
 *******************************************************************************/

public class Chromo {

	public String chromo;
	public double rawFitness;
	public double sclFitness;
	public double proFitness;

	private static double randnum;
	private Parameters params;

	public Chromo(Parameters p) {

		this.params = p;

		// Set gene values to a randum sequence of 1's and 0's
		char geneBit;
		chromo = "";
		for (int i = 0; i < this.params.numGenes; i++) {
			for (int j = 0; j < this.params.geneSize; j++) {
				randnum = Search.rng.nextDouble();
				if (randnum > 0.5)
					geneBit = '0';
				else
					geneBit = '1';
				this.chromo = chromo + geneBit;
			}
		}

		// initialize to -1 because none of these can be calculated yet
		this.rawFitness = -1;
		this.sclFitness = -1;
		this.proFitness = -1;
	}

	public Chromo(Chromo parent) {
		this.params = parent.params;
		this.chromo = parent.chromo;

		// initialize to -1 because none of these can be calculated yet
		this.rawFitness = -1;
		this.sclFitness = -1;
		this.proFitness = -1;
	}

	// Get Alpha Represenation of a Gene
	public String getGeneAlpha(int geneID) {
		int start = geneID * this.params.geneSize;
		int end = (geneID + 1) * this.params.geneSize;
		String geneAlpha = this.chromo.substring(start, end);
		return (geneAlpha);
	}

	// Get Integer Value of a Gene (Positive or Negative, 2's Compliment) ****
	public int getIntGeneValue(int geneID) {
		String geneAlpha = "";
		int geneValue;
		char geneSign;
		char geneBit;
		geneValue = 0;
		geneAlpha = getGeneAlpha(geneID);
		for (int i = this.params.geneSize - 1; i >= 1; i--) {
			geneBit = geneAlpha.charAt(i);
			if (geneBit == '1')
				geneValue = geneValue + (int) Math.pow(2.0, this.params.geneSize - i - 1);
		}
		geneSign = geneAlpha.charAt(0);
		if (geneSign == '1')
			geneValue = geneValue - (int) Math.pow(2.0, this.params.geneSize - 1);
		return (geneValue);
	}

	// Get Integer Value of a Gene (Positive only)
	public int getPosIntGeneValue(int geneID) {
		String geneAlpha = "";
		int geneValue;
		char geneBit;
		geneValue = 0;
		geneAlpha = getGeneAlpha(geneID);
		for (int i = this.params.geneSize - 1; i >= 0; i--) {
			geneBit = geneAlpha.charAt(i);
			if (geneBit == '1')
				geneValue = geneValue + (int) Math.pow(2.0, this.params.geneSize - i - 1);
		}
		return (geneValue);
	}

	// Mutate a Chromosome Based on Mutation Type
	public void doMutation() {

		String mutChromo = "";
		char x;

		switch (this.params.mutationType) {

			case 1:
				for (int j = 0; j < (this.params.geneSize * this.params.numGenes); j++) {
					x = this.chromo.charAt(j);
					randnum = Search.rng.nextDouble();
					if (randnum < this.params.mutationRate) {
						if (x == '1')
							x = '0';
						else
							x = '1';
					}
					mutChromo = mutChromo + x;
				}
				this.chromo = mutChromo;
				break;

			default:
				System.out.println("ERROR - No mutation method selected");
		}
	}

	// Copy this chromosome into the target chromosome
	public void copyTo(Chromo target) {

		target.chromo = this.chromo;

		target.rawFitness = this.rawFitness;
		target.sclFitness = this.sclFitness;
		target.proFitness = this.proFitness;
	}

}
