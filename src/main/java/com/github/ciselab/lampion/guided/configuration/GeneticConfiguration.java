package com.github.ciselab.lampion.guided.configuration;

public class GeneticConfiguration {

    private double crossoverRate = 0.7;
    private double mutationRate = 0.4;
    private double elitismRate = 0;
    private double increaseSizeRate = 0.7;

    private int maxGeneLength = 20;

    private int popSize = 10;
    private int tournamentSize = 4;

    private int maxSteadyGenerations = 2;


    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public double getElitismRate() {
        return elitismRate;
    }

    public void setElitismRate(double elitismRate) {
        this.elitismRate = elitismRate;
    }

    public double getIncreaseSizeRate() {
        return increaseSizeRate;
    }

    public void setIncreaseSizeRate(double increaseSizeRate) {
        this.increaseSizeRate = increaseSizeRate;
    }

    public int getMaxGeneLength() {
        return maxGeneLength;
    }

    public void setMaxGeneLength(int maxGeneLength) {
        this.maxGeneLength = maxGeneLength;
    }

    public int getPopSize() {
        return popSize;
    }

    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public int getMaxSteadyGenerations() {
        return maxSteadyGenerations;
    }

    public void setMaxSteadyGenerations(int maxSteadyGenerations) {
        this.maxSteadyGenerations = maxSteadyGenerations;
    }

}
