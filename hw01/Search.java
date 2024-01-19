
/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.util.*;

public class Search {

	public static Random rng = new Random();

	public static int runCount;

	public static void main(String[] args) throws java.io.IOException {

		Calendar dateAndTime = Calendar.getInstance();
		Date startTime = dateAndTime.getTime();

		if (args.length != 1) {
			System.out.println("\nUsage: java Search <parameter file name>\n");
			return;
		}

		System.out.println("\nLoading parameter file: " + args[0] + "\n");
		Parameters params = null;
		try {
			params = new Parameters(args[0]);
		} catch (Exception e) {
			System.out.println("Exception occurred while reading parameters file: " + e.getMessage());
			return;
		}

		rng.setSeed(params.seed);

		Population population = new Population(params);

		// we execute multiple runs in order to avoid one-off sampling problems
		for (runCount = 1; runCount <= params.numRuns; runCount++) {
			System.out.println();

			// initialize will reset our sampling of the population and allow for repeated
			// sampling using additional 'runs' by invoking the run method.
			population.initializePopulation();
			BestChromo bestOfRun = population.run();

			// output goes to both the summary file through this message as well as standard
			// out to report while evaluation is ongoing

			population.doPrintGenes(bestOfRun);

			System.out.println(runCount + "\t" + "B" + "\t" + (int) bestOfRun.rawFitness);
		}

		population.printBestGenes();

		// output runtime statistics
		System.out.println();
		System.out.println("Start:  " + startTime);
		dateAndTime = Calendar.getInstance();
		Date endTime = dateAndTime.getTime();
		System.out.println("End  :  " + endTime);
	}
}
