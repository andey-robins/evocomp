
/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;

public class OneMax implements IFitnessFunction {

	public String name = "OneMax Problem";

	private Parameters params;

	public OneMax(Parameters p) {
		this.params = p;
	}

	// computes the fitness of the parameter chromosome X
	public void doRawFitness(Chromo X) {

		X.rawFitness = 0;
		for (int z = 0; z < this.params.numGenes * this.params.geneSize; z++) {
			if (X.chromo.charAt(z) == '1')
				X.rawFitness += 1;
		}
	}

	// prints an individual gene X to the summary file encapsulated
	// in the FileWriter output
	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException {

		for (int i = 0; i < this.params.numGenes; i++) {
			Hwrite.right(X.getGeneAlpha(i), 11, output);
		}
		output.write("   RawFitness");
		output.write("\n        ");
		for (int i = 0; i < this.params.numGenes; i++) {
			Hwrite.right(X.getPosIntGeneValue(i), 11, output);
		}
		Hwrite.right((int) X.rawFitness, 13, output);
		output.write("\n\n");
		return;
	}

	public String getName() {
		return this.name;
	}

}