
/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;

public class NumberMatch implements IFitnessFunction {

	public String name = "Number Match Problem";

	private Parameters params;

	// Assumes no more than 100 values in the data file
	public static int[] testValue = new int[100];

	public NumberMatch(Parameters p) throws java.io.IOException {

		this.params = p;

		// Create Table of X values from input file
		BufferedReader input = new BufferedReader(new FileReader(this.params.dataInputFileName));
		for (int i = 0; i < this.params.numGenes; i++) {
			testValue[i] = Integer.parseInt(input.readLine().trim());
		}
		input.close();
	}

	// computes the fitness of the parameter chromosome X
	public void doRawFitness(Chromo X) {

		double difference = 0;
		for (int j = 0; j < this.params.numGenes; j++) {
			difference = (double) Math.abs(X.getIntGeneValue(j) - testValue[j]);
			X.rawFitness = X.rawFitness + difference;
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
			Hwrite.right(X.getIntGeneValue(i), 11, output);
		}
		Hwrite.right((int) X.rawFitness, 13, output);
		output.write("\n\n");
		return;
	}

	public String getName() {
		return this.name;
	}
}
