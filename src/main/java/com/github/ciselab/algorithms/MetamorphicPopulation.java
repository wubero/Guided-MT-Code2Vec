package com.github.ciselab.algorithms;

import com.github.ciselab.support.GenotypeSupport;
import java.util.random.RandomGenerator;

public class MetamorphicPopulation {

    MetamorphicIndividual[] individuals;
    GenotypeSupport genotypeSupport;

    /**
     * Initialize Metamorphic population, the initial population will be half of length 1 and half of length 2.
     * After that the evolution begins.
     * @param popSize the population size.
     * @param randomGenerator the random generator. This is kept the same everywhere for testing purposes.
     * @param maxValue the maximum transformer value.
     * @param initialize whether the population should be initialized or just created as an object.
     */
    public MetamorphicPopulation(int popSize, RandomGenerator randomGenerator, int maxValue, boolean initialize
            , GenotypeSupport gen) {
        genotypeSupport = gen;
        individuals = new MetamorphicIndividual[popSize];
        if(initialize) {
            int cutOff = popSize/2;
            for (int i = 0; i < cutOff; i++) {
                MetamorphicIndividual individual = new MetamorphicIndividual(genotypeSupport);
                individual.populateIndividual(randomGenerator, 1, maxValue);
                saveIndividual(i, individual);
            }
            for (int j = cutOff; j < popSize; j++) {
                MetamorphicIndividual individual = new MetamorphicIndividual(genotypeSupport);
                individual.populateIndividual(randomGenerator, 2, maxValue);
                saveIndividual(j, individual);
            }
        }
    }

    /**
     * Get the average size of this population.
     * @return the average size.
     */
    public int getAverageSize() {
        int sum = 0;
        for(MetamorphicIndividual i: individuals) {
            sum += i.getLength();
        }
        return sum/individuals.length;
    }

    /**
     * Get a metamorphic individual based on the index.
     * @param index the population index.
     * @return the metamorphic individual.
     */
    public MetamorphicIndividual getIndividual(int index) {
        return individuals[index];
    }

    /**
     * Changes a metamorphic individual at a given index.
     * @param index the index.
     * @param individual the metamorphic individual.
     */
    public void saveIndividual(int index, MetamorphicIndividual individual) {
        individuals[index] = individual;
    }

    /**
     * Get the fittest metamorphic individual of the metamorphic population.
     * @return the fittest metamorphic individual.
     */
    public MetamorphicIndividual getFittest() {
        MetamorphicIndividual fittest = individuals[0];
        // Loop through individuals to find fittest
        for (int i = 1; i < size(); i++) {
            if(genotypeSupport.getConfigManagement().getMaximize()) {
                if (fittest.getFitness() < getIndividual(i).getFitness())
                    fittest = getIndividual(i);
            } else {
                if (fittest.getFitness() > getIndividual(i).getFitness())
                    fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    /**
     * Get the size of the population.
     * @return the size of the population.
     */
    public int size() {
        return individuals.length;
    }

    @Override
    public String toString() {
        String output = "MetamorphicPopulation{";
        for (MetamorphicIndividual indiv: individuals)
            output += indiv.toString() + ", ";
        if(!output.equals("MetamorphicPopulation{"))
            return output.substring(0, output.length()-2) + "}";
        else
            return output;
    }
}
