
/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.util.*;

public class Search {

	public static Random rng = new Random();

	public static Chromo[] member;
	public static Chromo[] child;

	public static double averageRawFitness;
	public static double stdevRawFitness;

	public static int currentGeneration;
	public static int runCount;
	private static double randnum;

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

	// Select a parent for crossover
	public static int selectParent(Parameters params) {

		double rWheel = 0;
		int j = 0;

		switch (params.selectType) {

			case 1: // Proportional Selection
				randnum = Search.rng.nextDouble();
				for (j = 0; j < params.popSize; j++) {
					rWheel = rWheel + member[j].proFitness;
					if (randnum < rWheel)
						return (j);
				}
				break;

			case 3: // Random Selection
				randnum = Search.rng.nextDouble();
				j = (int) (randnum * params.popSize);
				return (j);

			case 2: // Tournament Selection

			default:
				System.out.println("ERROR - No selection method selected");
		}
		return (-1);
	}

	// Produce a new child from two parents. The children and parents are both
	// modified in place without any protections, so ensure that the parents are not
	// reused
	public static void mateParents(int pnum1, int pnum2, Chromo parent1, Chromo parent2, Chromo child1, Chromo child2,
			Parameters params) {

		int xoverPoint1;

		switch (params.xoverType) {

			case 1: // Single Point Crossover

				// Select crossover point
				xoverPoint1 = 1 + (int) (Search.rng.nextDouble() * (params.numGenes * params.geneSize - 1));

				// Create child chromosome from parental material
				child1.chromo = parent1.chromo.substring(0, xoverPoint1) + parent2.chromo.substring(xoverPoint1);
				child2.chromo = parent2.chromo.substring(0, xoverPoint1) + parent1.chromo.substring(xoverPoint1);
				break;

			case 2: // Two Point Crossover

			case 3: // Uniform Crossover

			default:
				System.out.println("ERROR - Bad crossover method selected");
		}

		// fitness values haven't been calculated, so set them to -1 as a sentinel value
		child1.rawFitness = -1;
		child1.sclFitness = -1;
		child1.proFitness = -1;
		child2.rawFitness = -1;
		child2.sclFitness = -1;
		child2.proFitness = -1;
	}
}
