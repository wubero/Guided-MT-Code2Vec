package com.github.ciselab.lampion.guided.configuration;

/**
 * This is the top-level class for all configurations.
 * I thought about having getters and setters, but I think it's fine to just have it accessible like this.
 */
public class Configuration {

    public GeneticConfiguration genetic = new GeneticConfiguration();
    public LampionConfiguration lampion = new LampionConfiguration();
    public ProgramConfiguration program = new ProgramConfiguration();

}
