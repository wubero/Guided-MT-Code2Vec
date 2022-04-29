package com.github.ciselab.simpleGA;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import java.util.Arrays;
import java.util.random.RandomGenerator;

public class MetamorphicPopulation {

    MetamorphicIndividual[] individuals;

    /**
     * Initialize Metamorphic population, the initial population will be half of length 1 and half of length 2.
     * After that the evolution begins.
     * @param popSize the population size.
     * @param r the random generator. This is kept the same everywhere for testing purposes.
     * @param maxValue the maximum transformer value.
     * @param initialize whether the population should be initialized or just created as an object.
     */
    public MetamorphicPopulation(int popSize, RandomGenerator r, int maxValue, boolean initialize) {
        individuals = new MetamorphicIndividual[popSize];
        if(initialize) {
            int cutOff = popSize/2;
            for (int i = 0; i < cutOff; i++) {
                MetamorphicIndividual individual = new MetamorphicIndividual();
                individual.createIndividual(r, 2, maxValue);
                saveIndividual(i, individual);
            }
            for (int j = cutOff; j < popSize; j++) {
                MetamorphicIndividual individual = new MetamorphicIndividual();
                individual.createIndividual(r, 2, maxValue);
                saveIndividual(j, individual);
            }
        }
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
        for (int i = 0; i < size(); i++) {
            if (fittest.getFitness() <= getIndividual(i).getFitness()) {
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
        return output.substring(0, output.length()-2) + "}";
    }
}
