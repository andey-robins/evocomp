
/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;

public class Search {

	public static IFitnessFunction problem;

	public static Chromo[] member;
	public static Chromo[] child;

	public static Chromo bestOfGenChromo;
	public static int bestOfGenR;
	public static int bestOfGenG;
	public static Chromo bestOfRunChromo;
	public static int bestOfRunR;
	public static int bestOfRunG;
	public static Chromo bestOverAllChromo;
	public static int bestOverAllR;
	public static int bestOverAllG;

	public static double sumRawFitness;
	public static double sumRawFitness2; // sum of squares of fitness
	public static double sumSclFitness;
	public static double sumProFitness;
	public static double defaultBest;
	public static double defaultWorst;

	public static double averageRawFitness;
	public static double stdevRawFitness;

	public static int G;
	public static int runCount;
	public static Random rng = new Random();
	private static double randnum;

	private static int memberIndex[];
	private static double memberFitness[];
	private static int TmemberIndex;
	private static double TmemberFitness;

	private static double fitnessStats[][]; // 0=Avg, 1=Best

	public static void main(String[] args) throws java.io.IOException {

		Calendar dateAndTime = Calendar.getInstance();
		Date startTime = dateAndTime.getTime();

		// Read Parameter File
		if (args.length == 0) {
			System.out.println("\nUsage: java Search <parameter file name>\n");
			return;
		}
		System.out.println("\nParameter File Name is: " + args[0] + "\n");
		Parameters params = new Parameters(args[0]);

		// Write Parameters To Summary Output File
		String summaryFileName = params.expID + "_summary.txt";
		FileWriter summaryOutput = new FileWriter(summaryFileName);
		params.outputParameters(summaryOutput);

		// Set up Fitness Statistics matrix
		fitnessStats = new double[2][params.generations];
		for (int i = 0; i < params.generations; i++) {
			fitnessStats[0][i] = 0;
			fitnessStats[1][i] = 0;
		}

		if (params.problemType.equals("NM")) {
			problem = new NumberMatch(params);
		} else if (params.problemType.equals("OM")) {
			problem = new OneMax(params);
		} else {
			System.out.println("Invalid Problem Type");
			System.out.println("Valid options are 'NM' for NumberMatch or 'OM' for OneMax");
			return;
		}

		System.out.println(problem.getName());

		// Initialize RNG, array sizes and other objects
		rng.setSeed(params.seed);
		memberIndex = new int[params.popSize];
		memberFitness = new double[params.popSize];
		member = new Chromo[params.popSize];
		child = new Chromo[params.popSize];
		bestOfGenChromo = new Chromo(params);
		bestOfRunChromo = new Chromo(params);
		bestOverAllChromo = new Chromo(params);

		if (params.minORmax.equals("max")) {
			defaultBest = 0;
			defaultWorst = Double.POSITIVE_INFINITY;
		} else {
			defaultBest = Double.POSITIVE_INFINITY;
			defaultWorst = 0;
		}

		bestOverAllChromo.rawFitness = defaultBest;

		// Start program for multiple runs
		for (runCount = 1; runCount <= params.numRuns; runCount++) {

			bestOfRunChromo.rawFitness = defaultBest;
			System.out.println();

			// Initialize First Generation
			for (int i = 0; i < params.popSize; i++) {
				member[i] = new Chromo(params);
				child[i] = new Chromo(params);
			}

			// Begin Each Run
			for (G = 0; G < params.generations; G++) {

				sumProFitness = 0;
				sumSclFitness = 0;
				sumRawFitness = 0;
				sumRawFitness2 = 0;
				bestOfGenChromo.rawFitness = defaultBest;

				// Test Fitness of Each Member
				for (int i = 0; i < params.popSize; i++) {

					member[i].rawFitness = 0;
					member[i].sclFitness = 0;
					member[i].proFitness = 0;

					problem.doRawFitness(member[i]);

					sumRawFitness = sumRawFitness + member[i].rawFitness;
					sumRawFitness2 = sumRawFitness2 +
							member[i].rawFitness * member[i].rawFitness;

					if (params.minORmax.equals("max")) {
						if (member[i].rawFitness > bestOfGenChromo.rawFitness) {
							bestOfGenChromo.copyTo(member[i]);
							bestOfGenR = runCount;
							bestOfGenG = G;
						}
						if (member[i].rawFitness > bestOfRunChromo.rawFitness) {
							bestOfRunChromo.copyTo(member[i]);
							bestOfRunR = runCount;
							bestOfRunG = G;
						}
						if (member[i].rawFitness > bestOverAllChromo.rawFitness) {
							bestOverAllChromo.copyTo(member[i]);
							bestOverAllR = runCount;
							bestOverAllG = G;
						}
					} else {
						if (member[i].rawFitness < bestOfGenChromo.rawFitness) {
							bestOfGenChromo.copyTo(member[i]);
							bestOfGenR = runCount;
							bestOfGenG = G;
						}
						if (member[i].rawFitness < bestOfRunChromo.rawFitness) {
							bestOfRunChromo.copyTo(member[i]);
							bestOfRunR = runCount;
							bestOfRunG = G;
						}
						if (member[i].rawFitness < bestOverAllChromo.rawFitness) {
							bestOverAllChromo.copyTo(member[i]);
							bestOverAllR = runCount;
							bestOverAllG = G;
						}
					}
				}

				// Accumulate fitness statistics
				fitnessStats[0][G] += sumRawFitness / params.popSize;
				fitnessStats[1][G] += bestOfGenChromo.rawFitness;

				averageRawFitness = sumRawFitness / params.popSize;
				stdevRawFitness = Math.sqrt(
						Math.abs(sumRawFitness2 -
								sumRawFitness * sumRawFitness / params.popSize)
								/
								(params.popSize - 1));

				// Output generation statistics to screen
				System.out.println(
						runCount + "\t" + G + "\t" + (int) bestOfGenChromo.rawFitness + "\t" + averageRawFitness
								+ "\t" + stdevRawFitness);

				// Output generation statistics to summary file
				summaryOutput.write(" R ");
				Hwrite.right(runCount, 3, summaryOutput);
				summaryOutput.write(" G ");
				Hwrite.right(G, 3, summaryOutput);
				Hwrite.right((int) bestOfGenChromo.rawFitness, 7, summaryOutput);
				Hwrite.right(averageRawFitness, 11, 3, summaryOutput);
				Hwrite.right(stdevRawFitness, 11, 3, summaryOutput);
				summaryOutput.write("\n");

				// scale fitness of each member and sum
				switch (params.scaleType) {

					case 0: // No change to raw fitness
						for (int i = 0; i < params.popSize; i++) {
							member[i].sclFitness = member[i].rawFitness + .000001;
							sumSclFitness += member[i].sclFitness;
						}
						break;

					case 1: // Fitness not scaled. Only inverted.
						for (int i = 0; i < params.popSize; i++) {
							member[i].sclFitness = 1 / (member[i].rawFitness + .000001);
							sumSclFitness += member[i].sclFitness;
						}
						break;

					case 2: // Fitness scaled by Rank (Maximizing fitness)

						// Copy genetic data to temp array
						for (int i = 0; i < params.popSize; i++) {
							memberIndex[i] = i;
							memberFitness[i] = member[i].rawFitness;
						}
						// Bubble Sort the array by floating point number
						for (int i = params.popSize - 1; i > 0; i--) {
							for (int j = 0; j < i; j++) {
								if (memberFitness[j] > memberFitness[j + 1]) {
									TmemberIndex = memberIndex[j];
									TmemberFitness = memberFitness[j];
									memberIndex[j] = memberIndex[j + 1];
									memberFitness[j] = memberFitness[j + 1];
									memberIndex[j + 1] = TmemberIndex;
									memberFitness[j + 1] = TmemberFitness;
								}
							}
						}
						// Copy ordered array to scale fitness fields
						for (int i = 0; i < params.popSize; i++) {
							member[memberIndex[i]].sclFitness = i;
							sumSclFitness += member[memberIndex[i]].sclFitness;
						}

						break;

					case 3: // Fitness scaled by Rank (minimizing fitness)

						// Copy genetic data to temp array
						for (int i = 0; i < params.popSize; i++) {
							memberIndex[i] = i;
							memberFitness[i] = member[i].rawFitness;
						}
						// Bubble Sort the array by floating point number
						for (int i = 1; i < params.popSize; i++) {
							for (int j = (params.popSize - 1); j >= i; j--) {
								if (memberFitness[j - i] < memberFitness[j]) {
									TmemberIndex = memberIndex[j - 1];
									TmemberFitness = memberFitness[j - 1];
									memberIndex[j - 1] = memberIndex[j];
									memberFitness[j - 1] = memberFitness[j];
									memberIndex[j] = TmemberIndex;
									memberFitness[j] = TmemberFitness;
								}
							}
						}
						// Copy array order to scale fitness fields
						for (int i = 0; i < params.popSize; i++) {
							member[memberIndex[i]].sclFitness = i;
							sumSclFitness += member[memberIndex[i]].sclFitness;
						}

						break;

					default:
						System.out.println("ERROR - No scaling method selected");
				}

				// *********************************************************************
				// ****** PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM *******
				// *********************************************************************

				for (int i = 0; i < params.popSize; i++) {
					member[i].proFitness = member[i].sclFitness / sumSclFitness;
					sumProFitness = sumProFitness + member[i].proFitness;
				}

				// crossover and create next generation
				int parent1 = -1;
				int parent2 = -1;

				// Assumes always two offspring per mating
				for (int i = 0; i < params.popSize; i = i + 2) {

					// Select Two Parents
					parent1 = selectParent(params);
					parent2 = parent1;
					while (parent2 == parent1) {
						parent2 = selectParent(params);
					}

					// Crossover Two Parents to Create Two Children
					randnum = rng.nextDouble();
					if (randnum < params.xoverRate) {
						mateParents(parent1, parent2, member[parent1], member[parent2], child[i], child[i + 1],
								params);
					} else {
						child[i] = new Chromo(member[parent1]);
						child[i + 1] = new Chromo(member[parent2]);
					}
				} // End Crossover

				// Mutate Children
				for (int i = 0; i < params.popSize; i++) {
					child[i].doMutation();
				}

				// Swap Children with Last Generation
				for (int i = 0; i < params.popSize; i++) {
					member[i].copyTo(child[i]);
				}

			} // Repeat the above loop for each generation

			Hwrite.left(bestOfRunR, 4, summaryOutput);
			Hwrite.right(bestOfRunG, 4, summaryOutput);

			problem.doPrintGenes(bestOfRunChromo, summaryOutput);

			System.out.println(runCount + "\t" + "B" + "\t" + (int) bestOfRunChromo.rawFitness);

		} // End of a Run

		Hwrite.left("B", 8, summaryOutput);

		problem.doPrintGenes(bestOverAllChromo, summaryOutput);

		// Output Fitness Statistics matrix
		summaryOutput.write("Gen                 AvgFit              BestFit \n");
		for (int i = 0; i < params.generations; i++) {
			Hwrite.left(i, 15, summaryOutput);
			Hwrite.left(fitnessStats[0][i] / params.numRuns, 20, 2, summaryOutput);
			Hwrite.left(fitnessStats[1][i] / params.numRuns, 20, 2, summaryOutput);
			summaryOutput.write("\n");
		}

		summaryOutput.write("\n");
		summaryOutput.close();

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
