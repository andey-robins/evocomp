
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

	/**
	 * Default constructor
	 * 
	 * @param p The parameters associated with the current chrom
	 */
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

	/**
	 * Copy constructor
	 * 
	 * @param parent The Chromo object to copy
	 */
	public Chromo(Chromo parent) {
		this.params = parent.params;
		this.chromo = parent.chromo;

		// initialize to -1 because none of these can be calculated yet
		this.rawFitness = -1;
		this.sclFitness = -1;
		this.proFitness = -1;
	}

	/**
	 * @param geneID The gene to get the value of
	 * @return the binary string representation of the gene's alpha value (???)
	 */
	public String getGeneAlpha(int geneID) {
		int start = geneID * this.params.geneSize;
		int end = (geneID + 1) * this.params.geneSize;
		String geneAlpha = this.chromo.substring(start, end);
		return (geneAlpha);
	}

	/**
	 * @param geneID The gene to get the value of
	 * @return the integer value of the gene, encoded as a two's complement number
	 */
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

	/**
	 * @param geneID The gene to get the value of
	 * @return the positive integer value of the gene
	 */
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

	/**
	 * Mutate the Chromo object based on the mutation type and rate specified in the
	 * parameter associated with the object
	 */
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

	/**
	 * Copy the calling object into the target parameter
	 * 
	 * @param target The object to copy into
	 */
	public void copyTo(Chromo target) {

		target.chromo = this.chromo;

		target.rawFitness = this.rawFitness;
		target.sclFitness = this.sclFitness;
		target.proFitness = this.proFitness;
	}

}
