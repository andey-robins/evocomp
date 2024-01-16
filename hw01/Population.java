import java.io.*;

public class Population {

    public IFitnessFunction problem;

    private Parameters params;
    private Chromo[] members, children;

    public double defaultBest, defaultWorst;

    private int memberIndex[];
    private double memberFitness[];

    private BestChromo best;

    private int currentGeneration, currentRun;

    private FileWriter outputWriter;

    private static double fitnessStats[][]; // 0=Avg, 1=Best

    public Population(Parameters params) throws IOException {
        this.params = params;
        this.outputWriter = new FileWriter(params.expID + "_summary.txt");
        // create the summary object immediately to save info
        // about the experiment
        params.outputParameters(this.outputWriter);

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

        this.memberIndex = new int[params.popSize];
        this.memberFitness = new double[params.popSize];
        this.members = new Chromo[params.popSize];
        this.children = new Chromo[params.popSize];
        // this.bestOfGeneration = new BestChromo(params);
        // this.bestOfRun = new BestChromo(params);
        // this.bestOverall = new BestChromo(params);

        if (params.minORmax.equals("max")) {
            this.defaultBest = 0;
            this.defaultWorst = Double.POSITIVE_INFINITY;
        } else {
            this.defaultBest = Double.POSITIVE_INFINITY;
            this.defaultWorst = 0;
        }

        // Initialize First Generation
        for (int i = 0; i < params.popSize; i++) {
            this.members[i] = new Chromo(params);
            this.children[i] = new Chromo(params);
        }

        // bestOverall.rawFitness = this.defaultBest;
    }

    public BestChromo run() {
        this.best.rawFitness = this.defaultBest;

        for (this.currentGeneration = 0; this.currentGeneration < this.params.generations; this.currentGeneration++) {
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
                        bestOfGenG = currentGeneration;
                    }
                    if (member[i].rawFitness > bestOfRunChromo.rawFitness) {
                        bestOfRunChromo.copyTo(member[i]);
                        bestOfRunR = runCount;
                        bestOfRunG = currentGeneration;
                    }
                    if (member[i].rawFitness > bestOverAllChromo.rawFitness) {
                        bestOverAllChromo.copyTo(member[i]);
                        bestOverAllR = runCount;
                        bestOverAllG = currentGeneration;
                    }
                } else {
                    if (member[i].rawFitness < bestOfGenChromo.rawFitness) {
                        bestOfGenChromo.copyTo(member[i]);
                        bestOfGenR = runCount;
                        bestOfGenG = currentGeneration;
                    }
                    if (member[i].rawFitness < bestOfRunChromo.rawFitness) {
                        bestOfRunChromo.copyTo(member[i]);
                        bestOfRunR = runCount;
                        bestOfRunG = currentGeneration;
                    }
                    if (member[i].rawFitness < bestOverAllChromo.rawFitness) {
                        bestOverAllChromo.copyTo(member[i]);
                        bestOverAllR = runCount;
                        bestOverAllG = currentGeneration;
                    }
                }
            }

            // Accumulate fitness statistics
            fitnessStats[0][currentGeneration] += sumRawFitness / params.popSize;
            fitnessStats[1][currentGeneration] += bestOfGenChromo.rawFitness;

            averageRawFitness = sumRawFitness / params.popSize;
            stdevRawFitness = Math.sqrt(
                    Math.abs(sumRawFitness2 -
                            sumRawFitness * sumRawFitness / params.popSize)
                            /
                            (params.popSize - 1));

            // Output generation statistics to screen
            System.out.println(
                    runCount + "\t" + currentGeneration + "\t" + (int) bestOfGenChromo.rawFitness + "\t"
                            + averageRawFitness
                            + "\t" + stdevRawFitness);

            // Output generation statistics to summary file
            summaryOutput.write(" R ");
            Hwrite.right(runCount, 3, summaryOutput);
            summaryOutput.write(" G ");
            Hwrite.right(currentGeneration, 3, summaryOutput);
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
        }

        return new BestChromo(this.params);
    }

}
