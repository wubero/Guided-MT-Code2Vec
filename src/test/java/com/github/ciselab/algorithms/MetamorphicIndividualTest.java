package com.github.ciselab.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.ciselab.lampion.core.transformations.transformers.AddNeutralElementTransformer;
import com.github.ciselab.lampion.core.transformations.transformers.IfTrueTransformer;
import com.github.ciselab.lampion.guided.algorithms.MetamorphicIndividual;
import com.github.ciselab.lampion.guided.configuration.ConfigManagement;
import com.github.ciselab.lampion.guided.support.FileManagement;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class MetamorphicIndividualTest {

/*
TODO: REIMPLEMENT
    @BeforeEach
    public void setUp() throws FileNotFoundException {
        genotypeSupport = new GenotypeSupport(new MetricCache());
        ConfigManagement configManagement = genotypeSupport.getConfigManagement();
        configManagement.setConfigFile("src/test/resources/config.properties");
        configManagement.initializeFields();
        individual = new MetamorphicIndividual(genotypeSupport, 0);
    }

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
    }

    @Test
    public void createIndividualTest() {
        RandomGenerator r = new SplittableRandom(101010);
        assertTrue(individual.getTransformers().isEmpty());
        individual.populateIndividual(r, 2, 6);
        assertFalse(individual.getTransformers().isEmpty());
    }

    @Test
    public void increaseIndividualLength() {
        RandomGenerator r = new SplittableRandom(101010);
        individual.populateIndividual(r, 2, 6);
        assertEquals(individual.getTransformers().size(), 2);
        individual.increase(10, r, 6);
        assertEquals(individual.getTransformers().size(), 3);
    }

    @Test
    public void decreaseIndividualLength() {
        RandomGenerator r = new SplittableRandom(101010);
        individual.populateIndividual(r, 2, 6);
        assertEquals(individual.getTransformers().size(), 2);
        individual.decrease(r);
        assertEquals(individual.getTransformers().size(), 1);
    }

    @Test
    public void individualJSONTest() {
        RandomGenerator r = new SplittableRandom(101010);
        MetamorphicIndividual parent1 = new MetamorphicIndividual(genotypeSupport, 0);
        parent1.populateIndividual(r, 2, 6);
        MetamorphicIndividual parent2 = new MetamorphicIndividual(genotypeSupport, 0);
        parent2.populateIndividual(r, 2, 6);
        MetamorphicIndividual child = new MetamorphicIndividual(genotypeSupport, 1);
        child.populateIndividual(r, 3, 6);
        child.setParents(parent1, parent2);
        JSONObject json = child.createNewJSON();
        assertTrue(json.containsKey("parent_1"));
        assertTrue(json.containsKey("genotype"));
        assertEquals(1, json.get("introduced_generation"));
        assertEquals("class org.json.simple.JSONArray", json.get("genotype").getClass().toString());
        JSONArray arr = (JSONArray) json.get("genotype");
        assertTrue(((JSONObject) arr.get(0)).containsKey("transformer"));
    }

    @Test
    public void individualJSON_withoutParents_Test() {
        RandomGenerator r = new SplittableRandom(101010);
        MetamorphicIndividual child = new MetamorphicIndividual(genotypeSupport, 1);
        child.populateIndividual(r, 3, 6);
        JSONObject json = child.createNewJSON();
        assertFalse(json.containsKey("parent_1"));
        assertTrue(json.containsKey("genotype"));
        assertEquals(1, json.get("introduced_generation"));
        assertEquals("class org.json.simple.JSONArray", json.get("genotype").getClass().toString());
        JSONArray arr = (JSONArray) json.get("genotype");
        assertTrue(((JSONObject) arr.get(0)).containsKey("transformer"));
    }

    @Tag("Slow")
    @Tag("File")
    @Test
    public void extendExistingDirectory_withTransformerTest() {
        RandomGenerator r = new SplittableRandom(101010);
        individual.populateIndividual(r, 2, 6);
        individual.getFitness();
        individual.addGene(individual.createGene(3, r));
        individual.getFitness();
        assertEquals(individual.getGene(2).getClass(), AddNeutralElementTransformer.class);
        individual.setGene(2, individual.createGene(0, r));
        assertEquals(individual.getGene(2).getClass(), IfTrueTransformer.class);
    }

 */
}
