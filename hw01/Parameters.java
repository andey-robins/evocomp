
/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;

public class Parameters {

	public String expID;
	public String problemType;

	public String dataInputFileName;

	public int numRuns;
	public int generations;
	public int popSize;

	public int genCap;
	public int fitCap;

	public String minORmax;
	public int selectType;
	public int scaleType;

	public int xoverType;
	public double xoverRate;
	public int mutationType;
	public double mutationRate;

	public long seed;
	public int numGenes;
	public int geneSize;

	public Parameters(String parmfilename) throws java.io.IOException {

		BufferedReader parmInput = new BufferedReader(new FileReader(parmfilename));

		this.expID = parmInput.readLine().substring(30);
		this.problemType = parmInput.readLine().substring(30);

		this.dataInputFileName = parmInput.readLine().substring(30);

		this.numRuns = Integer.parseInt(parmInput.readLine().substring(30).trim());
		this.generations = Integer.parseInt(parmInput.readLine().substring(30).trim());
		this.popSize = Integer.parseInt(parmInput.readLine().substring(30).trim());

		this.selectType = Integer.parseInt(parmInput.readLine().substring(30).trim());
		this.scaleType = Integer.parseInt(parmInput.readLine().substring(30).trim());

		this.xoverType = Integer.parseInt(parmInput.readLine().substring(30).trim());
		this.xoverRate = Double.parseDouble(parmInput.readLine().substring(30).trim());
		this.mutationType = Integer.parseInt(parmInput.readLine().substring(30).trim());
		this.mutationRate = Double.parseDouble(parmInput.readLine().substring(30).trim());

		this.seed = Long.parseLong(parmInput.readLine().substring(30).trim());
		this.numGenes = Integer.parseInt(parmInput.readLine().substring(30).trim());
		this.geneSize = Integer.parseInt(parmInput.readLine().substring(30).trim());

		parmInput.close();

		if (scaleType == 0 || scaleType == 2)
			this.minORmax = "max";
		else
			this.minORmax = "min";

	}

	public void outputParameters(FileWriter output) throws java.io.IOException {

		output.write("Experiment ID                :  " + this.expID + "\n");
		output.write("Problem Type                 :  " + this.problemType + "\n");

		output.write("Data Input File Name         :  " + this.dataInputFileName + "\n");

		output.write("Number of Runs               :  " + this.numRuns + "\n");
		output.write("Generations per Run          :  " + this.generations + "\n");
		output.write("Population Size              :  " + this.popSize + "\n");

		output.write("Selection Method             :  " + this.selectType + "\n");
		output.write("Fitness Scaling Type         :  " + this.scaleType + "\n");
		output.write("Min or Max Fitness           :  " + this.minORmax + "\n");

		output.write("Crossover Type               :  " + this.xoverType + "\n");
		output.write("Crossover Rate               :  " + this.xoverRate + "\n");
		output.write("Mutation Type                :  " + this.mutationType + "\n");
		output.write("Mutation Rate                :  " + this.mutationRate + "\n");

		output.write("Random Number Seed           :  " + this.seed + "\n");
		output.write("Number of Genes/Points       :  " + this.numGenes + "\n");
		output.write("Size of Genes                :  " + this.geneSize + "\n");

		output.write("\n\n");

	}
}
