import java.io.*;

public class Population {

    public IFitnessFunction problem;

    private Parameters params;
    private Chromo[] members, children;

    public double defaultBest, defaultWorst;

    private int memberIndex[];
    private double memberFitness[];
    private static int TmemberIndex;
    private static double TmemberFitness;

    public BestChromo bestOverall;
    private BestChromo bestOfGeneration, bestOfRun;

    private int currentGeneration, currentRun;

    private FileWriter outputWriter;

    private static double fitnessStats[][]; // 0=Avg, 1=Best

    private double sumRawFitness;
    private double sumRawFitness2; // sum of squares of fitness
    private double sumSclFitness;
    private double sumProFitness;

    public Population(Parameters params) throws IOException {
        this.params = params;
        this.outputWriter = new FileWriter(params.expID + "_summary.txt");
        // create the summary object immediately to save info
        // about the experiment
        params.outputParameters(this.outputWriter);
        this.bestOfGeneration = new BestChromo(params);
        this.bestOfRun = new BestChromo(params);
        this.bestOverall = new BestChromo(params);
        this.bestOverall.rawFitness = this.defaultBest;
    }

    public void initializePopulation() throws IOException {
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
        this.bestOfGeneration = new BestChromo(params);
        this.bestOfRun = new BestChromo(params);
        // best Overall doesn't get reset when we re-initialize the population since we
        // want to keep track of it's best overall for as long as the population object
        // exists

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

        this.bestOfGeneration.rawFitness = this.defaultBest;
        this.bestOfRun.rawFitness = this.defaultBest;
    }

    public BestChromo run() {

        for (this.currentGeneration = 0; this.currentGeneration < this.params.generations; this.currentGeneration++) {
            this.sumProFitness = 0;
            this.sumSclFitness = 0;
            this.sumRawFitness = 0;
            this.sumRawFitness2 = 0;
            this.bestOfGeneration.rawFitness = defaultBest;

            // Test Fitness of Each Member
            for (int i = 0; i < params.popSize; i++) {

                this.members[i].rawFitness = 0;
                this.members[i].sclFitness = 0;
                this.members[i].proFitness = 0;

                problem.doRawFitness(this.members[i]);

                sumRawFitness = sumRawFitness + this.members[i].rawFitness;
                sumRawFitness2 = sumRawFitness2 +
                        this.members[i].rawFitness * this.members[i].rawFitness;

                if (params.minORmax.equals("max")) {
                    if (this.members[i].rawFitness > this.bestOfRun.rawFitness) {
                        this.bestOfGeneration.copyTo(this.members[i]);
                        bestOfGenR = runCount;
                        bestOfGenG = currentGeneration;
                    }
                    if (this.members[i].rawFitness > bestOfRunChromo.rawFitness) {
                        bestOfRunChromo.copyTo(this.members[i]);
                        bestOfRunR = runCount;
                        bestOfRunG = currentGeneration;
                    }
                    if (this.members[i].rawFitness > bestOverAllChromo.rawFitness) {
                        bestOverAllChromo.copyTo(this.members[i]);
                        bestOverAllR = runCount;
                        bestOverAllG = currentGeneration;
                    }
                } else {
                    if (this.members[i].rawFitness < bestOfGenChromo.rawFitness) {
                        bestOfGenChromo.copyTo(this.members[i]);
                        bestOfGenR = runCount;
                        bestOfGenG = currentGeneration;
                    }
                    if (this.members[i].rawFitness < bestOfRunChromo.rawFitness) {
                        bestOfRunChromo.copyTo(this.members[i]);
                        bestOfRunR = runCount;
                        bestOfRunG = currentGeneration;
                    }
                    if (this.members[i].rawFitness < bestOverAllChromo.rawFitness) {
                        bestOverAllChromo.copyTo(this.members[i]);
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

        Hwrite.left(bestOfRunR, 4, summaryOutput);
        Hwrite.right(bestOfRunG, 4, summaryOutput);

        return this.bestOfRun;
    }

    public void doPrintGenes(Chromo X) throws IOException {
        problem.doPrintGenes(X, this.outputWriter);
    }

    public void printBestGenes() throws IOException {
        Hwrite.left("B", 8, this.outputWriter);
        this.doPrintGenes(this.bestOfRun);
    }

    public void printStatistics() throws IOException {
        // Output Fitness Statistics matrix
        this.outputWriter.write("Gen                 AvgFit              BestFit \n");
        for (int i = 0; i < params.generations; i++) {
            Hwrite.left(i, 15, this.outputWriter);
            Hwrite.left(fitnessStats[0][i] / params.numRuns, 20, 2, this.outputWriter);
            Hwrite.left(fitnessStats[1][i] / params.numRuns, 20, 2, this.outputWriter);
            this.outputWriter.write("\n");
        }

        this.outputWriter.write("\n");
        this.outputWriter.close();
    }
}
