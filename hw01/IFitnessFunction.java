
/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;

interface IFitnessFunction {

	// computes the fitness of the parameter chromosome X
	public void doRawFitness(Chromo X);

	// prints an individual gene X to the summary file encapsulated
	// in the FileWriter output
	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException;

	public String getName();
}
