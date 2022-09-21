package com.github.ciselab.lampion.guided.algorithms;

import com.github.ciselab.lampion.core.transformations.transformers.BaseTransformer;
import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.support.GenotypeSupport;
import com.github.ciselab.lampion.guided.support.MetricCache;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class creates a metamorphic individual for the metamorphic population.
 * It can increase the size or change the genotype of this metamorphic individual.
 */
public class MetamorphicIndividual {

    private final Logger logger = LogManager.getLogger(MetamorphicIndividual.class);
    private GenotypeSupport genotypeSupport;
    private MetricCache metricCache;

    // Path to where the altered Java Files are Stored (empty until transformers are run)
    protected Optional<String> javaPath = Optional.empty();
    // Path to where the individual is displayed in a JSON format
    protected Optional<String> jsonPath = Optional.empty();
    // Path to where the output of Code2Vec is stored for this Individual (empty if Code2Vec was not run)
    protected Optional<String> resultPath = Optional.empty();

    private List<BaseTransformer> transformers = new ArrayList<>();
    private Optional<Double> fitness = Optional.empty(); // Empty while not calculated or reset
    private Map<Metric,Double> metrics;
    private List<MetamorphicIndividual> parents = new ArrayList<>();
    private int generation;

    /**
     * Constructor for a Metamorphic Individual.
     * @param gen the genotype support, holding information e.g. on how to get metrics
     * @param generation the generation this individual was created
     */
    public MetamorphicIndividual (GenotypeSupport gen, int generation) {
        genotypeSupport = gen;
        metricCache = gen.getMetricCache();
        metrics = new HashMap<>();
        this.generation = generation;
    }

    /**
     * Populate the current metamorphic individual.
     * The key and seed are randomly generated according to the maximum transformer value and the random generator.
     * @param randomGenerator the random generator used for this run.
     * @param length the length.
     * @param maxTransformerValue the maximum transformer key value.
     */
    public void populateIndividual(RandomGenerator randomGenerator, int length, int maxTransformerValue) {
        transformers.clear();
        metrics = new HashMap<>();
        for(int i = 0; i < length; i++) {
            int key = randomGenerator.nextInt(0, maxTransformerValue+1);
            int seed = randomGenerator.nextInt();
            transformers.add(genotypeSupport.createTransformers(key, seed));
        }
    }

    /**
     * Get generation of current individual.
     * @return the generation.
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Set parents for this individual.
     * @param parent1 parent 1.
     * @param parent2 parent 2.
     */
    public void setParents(MetamorphicIndividual parent1, MetamorphicIndividual parent2) {
        parents.add(parent1);
        parents.add(parent2);
    }

    /**
     * Get the parents of this individual.
     * @return the parents.
     */
    public List<MetamorphicIndividual> getParents() {
        return parents;
    }

    /**
     * Get the length of this metamorphic individual.
     * @return the length.
     */
    public int getLength() {
        return transformers.size();
    }

    public Optional<String> getJavaPath(){return this.javaPath;}
    public void setJavaPath(String path){this.javaPath = Optional.of(path);}

    public Optional<String> getResultPath(){return this.resultPath;}
    public void setResultPath(String path){this.resultPath = Optional.of(path);}

    /**
     * Get the transformer at an index.
     * @param index the index.
     * @return the transformer.
     */
    public BaseTransformer getGene(int index) {
        return transformers.get(index);
    }

    /**
     * Get the transformers for this metamorphic individual.
     * @return the list of transformers.
     */
    public List<BaseTransformer> getTransformers() {
        return transformers;
    }

    /**
     * Change the transformer at an index.
     * @param index the index.
     * @param gene the transformer.
     */
    public void setGene(int index, BaseTransformer gene) {
        transformers.set(index, gene);
        fitness = Optional.empty();
    }

    /**
     * Add a new transformer to the list and set the fitness back to -1;
     * @param gene the transformer to add.
     */
    public void addGene(BaseTransformer gene) {
        transformers.add(gene);
        fitness = Optional.empty();
    }

    /**
     * If the length is not max, increase the length of this metamorphic individual by one.
     * @param maxGeneLength the max length for the transformer list.
     * @param randomGen the random generator used for this run.
     * @param maxValue the maximum value for the transformer keys.
     */
    public void increase(int maxGeneLength, RandomGenerator randomGen, int maxValue) {
        if(getLength() < maxGeneLength) {
            BaseTransformer newTransformer = genotypeSupport.createTransformers(randomGen.nextInt(1, maxValue+1), randomGen.nextInt());
            fitness = Optional.empty();
            if(metricCache.getDir(transformers).isPresent()) {
                List<BaseTransformer> t = new ArrayList<>();
                t.add(newTransformer);
                String oldDir = metricCache.getDir(transformers).get() + "/test";
                String name = genotypeSupport.runTransformations(this, oldDir);
                this.setJavaPath(name);
                inferMetrics();

                transformers.add(newTransformer);
                metricCache.storeFiles(this, name, metrics);
            } else {
                transformers.add(newTransformer);
                if (metricCache.getMetricResult(this).isPresent()) {
                    metrics = metricCache.getMetricResults(this).get();
                    fitness = Optional.of(calculateFitness());
                }
            }
            logger.debug("The gene " + this.hashCode() + " has increased its size to " + this.getLength());
        }
    }

    /**
     * Sets the Metrics of this Individual.
     * This includes running Code2Vec, if there is no existing results-file.
     * If there are result-files, the metrics are re-applied on the result files.
     *
     * As a side-effect, the Result-Path is set and the metrics are filled for this individual.
     */
    protected Map<Metric,Double> inferMetrics(){
        Map<Metric,Double> intermediateMetrics;
        if(javaPath.isEmpty()){
            String jPath = genotypeSupport.runTransformations(this, genotypeSupport.getCurrentDataset());
            this.setJavaPath(jPath);
        }
        if(this.resultPath.isEmpty()){
            if(this.javaPath.isEmpty())
                setJavaPath(genotypeSupport.runTransformations(this, genotypeSupport.getCurrentDataset()));
            String destination= javaPath.get() + "/results/";

            String resultDirectory =
                  genotypeSupport.runCode2vec(this.javaPath.get(),destination);
          this.setResultPath(resultDirectory);
          intermediateMetrics =
                  metricCache.getMetrics().stream()
                          .filter(m -> !m.isSecondary())
                          .collect(Collectors.toMap(Function.identity(),m -> m.apply(this)));
        } else {
            intermediateMetrics =
                    metricCache.getMetrics().stream()
                            .filter(m -> !m.isSecondary())
                            .collect(Collectors.toMap(Function.identity(),m -> m.apply(this)));
        }

        setMetrics(intermediateMetrics);

        return intermediateMetrics;
    }

    /**
     * Decrease the amount of transformers for this metamorphic individual.
     * @param randomGen the random generator used for this run.
     */
    public void decrease(RandomGenerator randomGen) {
        if(getLength() > 1) {
            int drop = randomGen.nextInt(0, getLength());
            transformers.remove(drop);
            logger.debug("The gene " + Integer.toHexString(this.hashCode()).substring(0,6) + " has decreased its size to " + this.getLength());
            if(metricCache.getMetricResults(this).isPresent()) {
                metrics = metricCache.getMetricResults(this).get();
                fitness = Optional.of(calculateFitness());
            } else {
                fitness = Optional.empty();
                metrics = new HashMap<>();
            }
        }
    }

    /**
     * Create a new gene, transformer, for the metamorphic individual.
     * @param key the key for the transformer.
     * @param random the random generator used for this run.
     * @return the transformer created.
     */
    public BaseTransformer createGene(int key, RandomGenerator random) {
        return genotypeSupport.createTransformers(key, random.nextInt());
    }

    /**
     * Get the fitness of this metamorphic individual. If it does not exist calculate it.
     * Every metric has a certain weight specified in the config. These weights are normalized for all included metrics.
     * The score of a weight is then multiplied by its weights and added to the fitness of this individual.
     * The resulting fitness of all metrics will be between 0 and 1.
     * @return the fitness of this metamorphic individual.
     */
    public double getFitness() {
        if (fitness.isEmpty() || fitness.get() < 0.0) {
            String name = genotypeSupport.runTransformations(this, genotypeSupport.getCurrentDataset());
            setJavaPath(name);
            inferMetrics();
            metricCache.fillFitness(this, metrics);
        }
        logger.debug("The gene " + Integer.toHexString(this.hashCode()).substring(0,6) +  " has calculated its fitness, it is: " + fitness.get());
        return fitness.get();
    }


    /**
     * Set the metrics of this metamorphic individual.
     * @param results the metrics to set.
     */
    public void setMetrics(Map<Metric,Double> results) {
        this.metrics = results;
        fitness = Optional.of(calculateFitness());
    }

    /**
     * Calculate the global fitness of the metrics with the weights for each metric.
     * Note: Metrics that can be bigger than 1 are ignored for Fitness Calculation, as they would scew the results.
     * @return The global fitness.
     */
    private double calculateFitness() {
        return metricCache.getActiveMetrics().stream()
                .filter(m -> !m.canBeBiggerThanOne())
                .mapToDouble(
                    m -> m.apply(this) * m.getWeight()
                )
                .sum();
    }

    /**
     * Put current individual in the json object or adjust current object and write to file.
     */
    public void writeIndividualJSON() {
        JSONObject jsonIndividual = new JSONObject();
        if(jsonPath.isPresent()) {
            // Call adjust json function to say age += 1
            jsonIndividual = adjustJSONIndividual(jsonPath.get());
            // if the object is empty we shouldn't write it to file
            if(jsonIndividual.isEmpty())
                return;
        } else {
            if(resultPath.isPresent()) { // Should be able to do this with the javaPath
                jsonPath = Optional.of(resultPath.get() + ".json");

                //Write JSON file
                jsonIndividual = createNewJSON();
            }
        }
        try (FileWriter file = new FileWriter(jsonPath.get())) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(jsonIndividual.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get current JSON object and increase the age attribute by one.
     * @param fileName the file of the JSON object.
     * @return the new JSON object.
     */
    private JSONObject adjustJSONIndividual(String fileName) {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName))
        {
            JSONObject jsonIndividual = (JSONObject) jsonParser.parse(reader);
            int age = (int) jsonIndividual.get("age");
            jsonIndividual.replace("age", age, age+1);
            return jsonIndividual;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    /**
     * Create new JSON object with all the data for this individual.
     * @return the json object.
     */
    private JSONObject createNewJSON() {
        JSONObject jsonIndividual = new JSONObject();
        metrics.forEach((key, value) -> jsonIndividual.put(key.getName(), value));
        jsonIndividual.put("age", "1");
        jsonIndividual.put("introduced_generation", getGeneration());
        List<MetamorphicIndividual> parents = getParents();
        if(!parents.isEmpty()) {
            jsonIndividual.put("parent_2", individualToJSON(parents.get(1)));
            jsonIndividual.put("parent_1", individualToJSON(parents.get(0)));
        }
        jsonIndividual.put("hash_with_lifetime", hashWithLifetime(1));
        jsonIndividual.put("hash", hash());
        jsonIndividual.put("genotype", individualToJSON(this));
        return jsonIndividual;
    }

    /**
     * Transforms metamorphic individual to a JSON compatible string.
     * @param individual the individual.
     * @return the JSON compatible string.
     */
    private String individualToJSON(MetamorphicIndividual individual) {
        String output = "[";
        for(BaseTransformer transformer: individual.getTransformers()) {
            String[] temp = transformer.getClass().toString().split("\\.");
            String addition = temp[temp.length-1];
            output += "{ transformer: " +  addition + ", seed: " + transformer.getSeed() + " }";
        }
        return output + "]";
    }

    @Override
    public String toString() {
        String geneString = "[";
        for (BaseTransformer i: transformers) {
            String[] temp = i.getClass().toString().split("\\.");
            String addition = temp[temp.length-1];
            geneString += addition + ", ";
        }
        if(geneString.length() < 5) {
            return "[]";
        } else {
            return geneString.substring(0, geneString.length() - 2) + "]";
        }
    }

    /**
     * We make a separation between the identity and the concept of an individual.
     * If you have the same list of transformers, just at a different generation, you will create the same genotype.
     * To keep track of time of individuals, we have "hashWithLifetime"
     * @return
     */
    public int hash() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((transformers == null) ? 0 : transformers.hashCode());
        return result;
    }

    public int hashWithLifetime(int lifetime) {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((transformers == null) ? 0 : transformers.hashCode());
        result = prime * result + lifetime ^ lifetime;
        return result;
    }
}
