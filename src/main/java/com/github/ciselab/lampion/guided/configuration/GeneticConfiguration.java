package com.github.ciselab.lampion.guided.configuration;

public class GeneticConfiguration {

    private double crossoverRate = 0.7;
    private double mutationRate = 0.4;
    private double elitismRate = 0;
    private double increaseSizeRate = 0.7;

    private int maxGeneLength = 20;

    private int popSize = 10;
    private int tournamentSize = 4;

    private int maxSteadyGenerations = 35;


    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        if(crossoverRate <= 0 || crossoverRate>1){
            throw new IllegalArgumentException("Crossover-Rate must be between 0 and 1");
        }
        this.crossoverRate = crossoverRate;
    }

    /**
     * Determines the chance how likely it is for an individual to mutate.
     * Value is between 0 and 1.
     * @return the mutation rate, between 0 and 1
     */
    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        if(mutationRate <= 0 || mutationRate>1){
            throw new IllegalArgumentException("Mutation-Rate must be between 0 and 1");
        }
        this.mutationRate = mutationRate;
    }

    public double getElitismRate() {
        return elitismRate;
    }

    public void setElitismRate(double elitismRate) {
        if(elitismRate <= 0 || elitismRate>1){
            throw new IllegalArgumentException("Elitism-Rate must be between 0 and 1");
        }
        this.elitismRate = elitismRate;
    }

    /**
     * How likely are mutations to create more transformers?
     * Value is between 0 and 1.
     * @return chance of a mutation to grow the genotype
     */
    public double getIncreaseSizeRate() {
        return increaseSizeRate;
    }

    public void setIncreaseSizeRate(double increaseSizeRate) {
        if(increaseSizeRate <= 0 || increaseSizeRate>1){
            throw new IllegalArgumentException("Increase-Rate must be between 0 and 1");
        }
        this.increaseSizeRate = increaseSizeRate;
    }

    /**
     * The maximum gene length.
     * Value is bigger than 1.
     * In case of a reached maximum gene-length, mutation will default to shrink.
     * @return Maximum Number of Transformers per Gene.
     */
    public int getMaxGeneLength() {
        return maxGeneLength;
    }

    public void setMaxGeneLength(int maxGeneLength) {
        if(maxGeneLength<=0){
            throw new IllegalArgumentException("Gene Length must be bigger than 0");
        }
        this.maxGeneLength = maxGeneLength;
    }

    /**
     * How many individuals are in on population?
     * Value > 1
     * @return number of individuals in one population.
     */
    public int getPopSize() {
        return popSize;
    }

    public void setPopSize(int popSize) {
        if(popSize<=0){
            throw new IllegalArgumentException("Populationsize must be bigger than 0");
        }
        this.popSize = popSize;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize) {
        if(tournamentSize<=0){
            throw new IllegalArgumentException("TournamentSize must be bigger than 0");
        }
        this.tournamentSize = tournamentSize;
    }

    /**
     * Returns the allowed maximum steady generations of an experiment.
     * A generation is consider steady if the fitness does not improve.
     * Program first checks against steady generations, before the general used time is checked.
     * @return the number of generations that must be steady until program terminates.
     */
    public int getMaxSteadyGenerations() {
        return maxSteadyGenerations;
    }

    public void setMaxSteadyGenerations(int maxSteadyGenerations) {
        if(maxSteadyGenerations<1){
            throw new IllegalArgumentException("Steady Generations must be bigger or equals to 1");
        }
        this.maxSteadyGenerations = maxSteadyGenerations;
    }

}
