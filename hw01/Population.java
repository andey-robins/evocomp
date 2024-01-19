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

    private int bestOfGenRunNum, bestOfGenGenNum;

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

        this.currentRun = 0;

        this.bestOfGeneration = new BestChromo(params);
        this.bestOfRun = new BestChromo(params);
        this.bestOverall = new BestChromo(params);
        this.bestOverall.rawFitness = this.defaultBest;
    }

    /**
     * Sets up the population for a new run of the experiment by re-sampling from
     * the initial population. Should always be called before run() is called. This
     * is currently not strictly enforced by the API
     */
    public void initializePopulation() {
        // Set up Fitness Statistics matrix
        fitnessStats = new double[2][this.params.generations];
        for (int i = 0; i < this.params.generations; i++) {
            fitnessStats[0][i] = 0;
            fitnessStats[1][i] = 0;
        }

        if (this.params.problemType.equals("NM")) {
            try {
                problem = new NumberMatch(this.params);
            } catch (Exception e) {
                System.out.println(
                        "Exception occurred while initializing population for problme NumberMatch: " + e.getMessage());
                System.exit(1);
            }
        } else if (this.params.problemType.equals("OM")) {
            problem = new OneMax(this.params);
        } else {
            System.out.println("Invalid Problem Type");
            System.out.println("Valid options are 'NM' for NumberMatch or 'OM' for OneMax");
            System.exit(1);
        }

        this.memberIndex = new int[this.params.popSize];
        this.memberFitness = new double[this.params.popSize];
        this.members = new Chromo[this.params.popSize];
        this.children = new Chromo[this.params.popSize];
        this.bestOfGeneration = new BestChromo(this.params);
        this.bestOfRun = new BestChromo(this.params);
        // best Overall doesn't get reset when we re-initialize the population since we
        // want to keep track of it's best overall for as long as the population object
        // exists

        if (this.params.minORmax.equals("max")) {
            this.defaultBest = 0;
            this.defaultWorst = Double.POSITIVE_INFINITY;
        } else {
            this.defaultBest = Double.POSITIVE_INFINITY;
            this.defaultWorst = 0;
        }

        // Initialize First Generation
        for (int i = 0; i < this.params.popSize; i++) {
            this.members[i] = new Chromo(this.params);
            this.children[i] = new Chromo(this.params);
        }

        this.bestOfGeneration.rawFitness = this.defaultBest;
        this.bestOfRun.rawFitness = this.defaultBest;

        this.currentRun++;
    }

    /**
     * Executes a "run" of evolution for the given population, executing for a
     * number of generations defined by the parameters provided when initializing
     * the population. This method should not be called before initializePopulation
     * 
     * @return The best chromosome found during the run
     */
    public BestChromo run() throws IOException {

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

                this.updateBestIfBetter(this.members[i], this.bestOfGeneration);
                this.updateBestIfBetter(this.members[i], this.bestOfRun);
                this.updateBestIfBetter(this.members[i], this.bestOverall);
            }

            // Accumulate fitness statistics
            double averageRawFitness = sumRawFitness / params.popSize;
            fitnessStats[0][this.currentGeneration] += averageRawFitness;
            fitnessStats[1][this.currentGeneration] += this.bestOfGeneration.rawFitness;

            double stdevRawFitness = Math.sqrt(
                    Math.abs(sumRawFitness2 -
                            sumRawFitness * sumRawFitness / params.popSize)
                            /
                            (params.popSize - 1));

            // Output generation statistics to screen
            System.out.println(
                    this.currentRun + "\t" + this.currentGeneration + "\t" + (int) this.bestOfGeneration.rawFitness
                            + "\t"
                            + averageRawFitness
                            + "\t" + stdevRawFitness);

            // Output generation statistics to summary file
            this.outputWriter.write(" R ");
            Hwrite.right(this.currentRun, 3, this.outputWriter);
            this.outputWriter.write(" G ");
            Hwrite.right(currentGeneration, 3, this.outputWriter);
            Hwrite.right((int) this.bestOfGeneration.rawFitness, 7, this.outputWriter);
            Hwrite.right(averageRawFitness, 11, 3, this.outputWriter);
            Hwrite.right(stdevRawFitness, 11, 3, this.outputWriter);
            this.outputWriter.write("\n");

            // scale fitness of each member and sum
            switch (params.scaleType) {
                case 0: // No change to raw fitness
                    for (int i = 0; i < params.popSize; i++) {
                        this.members[i].sclFitness = this.members[i].rawFitness + .000001;
                        sumSclFitness += this.members[i].sclFitness;
                    }
                    break;

                case 1: // Fitness not scaled. Only inverted.
                    for (int i = 0; i < params.popSize; i++) {
                        this.members[i].sclFitness = 1 / (this.members[i].rawFitness + .000001);
                        sumSclFitness += this.members[i].sclFitness;
                    }
                    break;

                case 2: // Fitness scaled by Rank (Maximizing fitness)

                    // Copy genetic data to temp array
                    for (int i = 0; i < params.popSize; i++) {
                        memberIndex[i] = i;
                        memberFitness[i] = this.members[i].rawFitness;
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
                        this.members[memberIndex[i]].sclFitness = i;
                        sumSclFitness += this.members[memberIndex[i]].sclFitness;
                    }

                    break;

                case 3: // Fitness scaled by Rank (minimizing fitness)

                    // Copy genetic data to temp array
                    for (int i = 0; i < params.popSize; i++) {
                        memberIndex[i] = i;
                        memberFitness[i] = this.members[i].rawFitness;
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
                        this.members[memberIndex[i]].sclFitness = i;
                        sumSclFitness += this.members[memberIndex[i]].sclFitness;
                    }

                    break;

                default:
                    System.out.println("ERROR - No scaling method selected");
            }

            // *********************************************************************
            // ****** PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM *******
            // *********************************************************************

            for (int i = 0; i < params.popSize; i++) {
                this.members[i].proFitness = this.members[i].sclFitness / sumSclFitness;
                sumProFitness = sumProFitness + this.members[i].proFitness;
            }

            // crossover and create next generation
            int parent1 = -1;
            int parent2 = -1;

            // Assumes always two offspring per mating
            for (int i = 0; i < params.popSize; i = i + 2) {

                // Select Two Parents
                parent1 = this.selectParent();
                parent2 = parent1;
                while (parent2 == parent1) {
                    parent2 = this.selectParent();
                }

                // Crossover Two Parents to Create Two Children
                double randnum = Search.rng.nextDouble();
                if (randnum < params.xoverRate) {
                    this.mateParents(parent1, parent2, this.members[parent1], this.members[parent2], this.children[i],
                            this.children[i + 1]);
                } else {
                    this.children[i] = new Chromo(this.members[parent1]);
                    this.children[i + 1] = new Chromo(this.members[parent2]);
                }
            } // End Crossover

            // Mutate Children
            for (int i = 0; i < params.popSize; i++) {
                this.children[i].doMutation();
            }

            // Swap Children with Last Generation
            for (int i = 0; i < params.popSize; i++) {
                this.members[i].copyTo(this.children[i]);
            }
        }

        Hwrite.left(this.bestOfGenRunNum, 4, this.outputWriter);
        Hwrite.right(this.bestOfGenGenNum, 4, this.outputWriter);

        return this.bestOfRun;
    }

    // Select a parent for crossover
    public int selectParent() {

        double rWheel = 0;
        int j = 0;
        double randnum;

        switch (this.params.selectType) {
            case 1: // Proportional Selection
                randnum = Search.rng.nextDouble();
                for (j = 0; j < params.popSize; j++) {
                    rWheel = rWheel + this.members[j].proFitness;
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
    public void mateParents(int pnum1, int pnum2, Chromo parent1, Chromo parent2, Chromo child1, Chromo child2) {

        int xoverPoint1;

        switch (this.params.xoverType) {

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

    protected void updateBestIfBetter(Chromo curr, Chromo best) {
        if (this.params.minORmax.equals("max")) {
            if (curr.rawFitness > best.rawFitness) {
                curr.copyTo(best);
            }
        } else {
            if (curr.rawFitness < best.rawFitness) {
                curr.copyTo(best);
            }
        }

        this.bestOfGenRunNum = this.currentRun;
        this.bestOfGenGenNum = this.currentGeneration;
    }
}
