package com.github.ciselab.lampion.guided.algorithms;

import com.github.ciselab.lampion.core.transformations.Transformer;
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
import org.json.simple.JSONArray;
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

    private List<Transformer> transformers = new ArrayList<>();
    private Optional<Double> fitness = Optional.empty(); // Empty while not calculated or reset
    private Map<Metric, Double> metrics;
    private List<MetamorphicIndividual> parents = new ArrayList<>();
    private int generation;

    /**
     * Constructor for a Metamorphic Individual.
     *
     * @param gen        the genotype support, holding information e.g. on how to get metrics
     * @param generation the generation this individual was created
     */
    public MetamorphicIndividual(GenotypeSupport gen, int generation) {
        genotypeSupport = gen;
        metricCache = gen.getMetricCache();
        metrics = new HashMap<>();
        this.generation = generation;
    }

    /**
     * Populate the current metamorphic individual.
     * The key and seed are randomly generated according to the maximum transformer value and the random generator.
     *
     * @param randomGenerator     the random generator used for this run.
     * @param length              the length.
     */
    public void populateIndividual(RandomGenerator randomGenerator, int length) {
        transformers.clear();
        metrics = new HashMap<>();
        for (int i = 0; i < length; i++) {
            transformers.add(genotypeSupport.createRandomTransformer(randomGenerator));
        }
    }

    /**
     * Get generation of current individual.
     *
     * @return the generation.
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Set parents for this individual.
     *
     * @param parent1 parent 1.
     * @param parent2 parent 2.
     */
    public void setParents(MetamorphicIndividual parent1, MetamorphicIndividual parent2) {
        parents.add(parent1);
        parents.add(parent2);
    }

    /**
     * Get the parents of this individual.
     *
     * @return the parents.
     */
    public List<MetamorphicIndividual> getParents() {
        return parents;
    }

    /**
     * Get the length of this metamorphic individual.
     *
     * @return the length.
     */
    public int getLength() {
        return transformers.size();
    }

    public Optional<String> getJavaPath() {
        return this.javaPath;
    }

    public void setJavaPath(String path) {
        this.javaPath = Optional.of(path);
    }

    public Optional<String> getResultPath() {
        return this.resultPath;
    }

    public void setResultPath(String path) {
        this.resultPath = Optional.of(path);
    }

    /**
     * Get the transformer at an index.
     *
     * @param index the index.
     * @return the transformer.
     */
    public Transformer getGene(int index) {
        return transformers.get(index);
    }

    /**
     * Get the transformers for this metamorphic individual.
     *
     * @return the list of transformers.
     */
    public List<Transformer> getTransformers() {
        return transformers;
    }

    /**
     * Change the transformer at an index.
     *
     * @param index the index.
     * @param gene  the transformer.
     */
    public void setGene(int index, BaseTransformer gene) {
        transformers.set(index, gene);
        fitness = Optional.empty();
    }

    /**
     * Add a new transformer to the list and set the fitness back to -1;
     *
     * @param gene the transformer to add.
     */
    public void addGene(Transformer gene) {
        transformers.add(gene);
        fitness = Optional.empty();
    }

    /**
     * If the length is not max, increase the length of this metamorphic individual by one.
     *
     * @param maxGeneLength the max length for the transformer list.
     * @param randomGen     the random generator used for this run.
     */
    public void increase(int maxGeneLength, RandomGenerator randomGen) {
        if (getLength() < maxGeneLength) {
            Transformer newTransformer = genotypeSupport.createRandomTransformer(randomGen);
            fitness = Optional.empty();
            if (metricCache.getDir(transformers).isPresent()) {
                List<Transformer> t = new ArrayList<>();
                t.add(newTransformer);
                String oldDir = metricCache.getDir(transformers).get() + "/test";
                String name = genotypeSupport.runTransformations(this, oldDir);
                this.setJavaPath(name);
                inferMetrics();

                transformers.add(newTransformer);
                metricCache.storeFiles(this, name, metrics);
            } else {
                transformers.add(newTransformer);
            }
            logger.debug("The gene " + this.hashCode() + " has increased its size to " + this.getLength());
        }
    }

    /**
     * Sets the Metrics of this Individual.
     * This includes running Code2Vec, if there is no existing results-file.
     * If there are result-files, the metrics are re-applied on the result files.
     * <p>
     * As a side-effect, the Result-Path is set and the metrics are filled for this individual.
     */
    protected Map<Metric, Double> inferMetrics() {
        Map<Metric, Double> intermediateMetrics;
        if (javaPath.isEmpty()) {
            String jPath = genotypeSupport.runTransformations(this, genotypeSupport.getInitialDataset());
            this.setJavaPath(jPath);
        }
        if (this.resultPath.isEmpty()) {
            if (this.javaPath.isEmpty())
                setJavaPath(genotypeSupport.runTransformations(this, genotypeSupport.getInitialDataset()));
            String destination = javaPath.get() + "/results/";

            String resultDirectory =
                    genotypeSupport.runCode2vec(this.javaPath.get(), destination);
            this.setResultPath(resultDirectory);
            intermediateMetrics =
                    metricCache.getMetrics().stream()
                            .filter(m -> !m.isSecondary())
                            .collect(Collectors.toMap(Function.identity(), m -> m.apply(this)));
        } else {
            intermediateMetrics =
                    metricCache.getMetrics().stream()
                            .filter(m -> !m.isSecondary())
                            .collect(Collectors.toMap(Function.identity(), m -> m.apply(this)));
        }

        setMetrics(intermediateMetrics);

        return intermediateMetrics;
    }

    /**
     * Decrease the amount of transformers for this metamorphic individual.
     *
     * @param randomGen the random generator used for this run.
     */
    public void decrease(RandomGenerator randomGen) {
        if (getLength() > 1) {
            int drop = randomGen.nextInt(0, getLength());
            transformers.remove(drop);
            logger.debug("The gene " + hexHash() + " has decreased its size to " + this.getLength());
        }
    }

    /**
     * Get the fitness of this metamorphic individual. If it does not exist calculate it.
     * Every metric has a certain weight specified in the config. These weights are normalized for all included metrics.
     * The score of a weight is then multiplied by its weights and added to the fitness of this individual.
     * The resulting fitness of all metrics will be between 0 and 1.
     * Note: Calculation likely needs to run in the file-system
     *
     * @return the fitness of this metamorphic individual.
     */
    public double getFitness() {
        /*
        We have three cases:
        1.) Fitness is already known to individual - return it.
        2.) Fitness is not known to Individual, but to cache. Return it.
        3.) Fitness needs to be read from FileSystem
        3a.) Java Files are not created - create them first
        3b.) Java Files are there, but Result Files are not read
         */
        if (fitness.isPresent()) {
            return this.fitness.get();
        }
        if (fetchFitness().isPresent()) {
            fitness = fetchFitness();
            return fitness.get();
        }
        logger.trace("The gene " + hexHash() + " needs to calculate its fitness");
        if (javaPath.isEmpty()) {
            String name = genotypeSupport.runTransformations(this, genotypeSupport.getInitialDataset());
            setJavaPath(name);
        }
        inferMetrics();
        metricCache.storeMetricResults(this, metrics);
        inferFitness();

        logger.debug("The gene " + hexHash() + " has calculated its fitness, it is: " + fitness.get());
        return fitness.get();
    }


    /**
     * Set the metrics of this metamorphic individual.
     *
     * @param results the metrics to set.
     */
    public void setMetrics(Map<Metric, Double> results) {
        this.metrics = results;
        fitness = Optional.of(calculateFitness(results));
    }

    /**
     * Calculate the global fitness of the metrics with the weights for each metric.
     * Note: Metrics that can be bigger than 1 are ignored for Fitness Calculation, as they would scew the results.
     * Note: Applying the Metrics likely runs in the file system, looking for result files.
     *
     * @return The global fitness.
     */
    private double inferFitness() {
        var results =  new HashMap<Metric,Double>();
        for (Metric m : metricCache.getActiveMetrics()){
            results.put(m,m.apply(this));
        };

        metricCache.putMetricResults(this,results);

        return calculateFitness(results);
    }

    /**
     * Tries to calculate Fitness from the cache,
     * if this individual was not stored in the cache return empty.
     *
     * @return the calculated metrics from cache, if possible. Empty otherwise.
     */
    private Optional<Double> fetchFitness() {
        var cached = metricCache.getMetricResults(this);
        if (cached.isPresent()){
            Map<Metric,Double> cachedResults = cached.get();
            return Optional.of(calculateFitness(cachedResults));
        } else
            return Optional.empty();
    }


    private double calculateFitness(Map<Metric,Double> entries){
        Double fitness = 0.0;
        for (Map.Entry<Metric,Double> entry : entries.entrySet()){
            boolean isActiveMetric = metricCache.getActiveMetrics().contains(entry.getKey());
            if(!entry.getKey().canBeBiggerThanOne() && isActiveMetric){
                fitness += entry.getValue() * entry.getKey().getWeight();
            }
        }

        // If the Fitness is negative, we are trying to maximize:
        // We "flip" the fitness to 1-fitness
        fitness = metricCache.doMaximize() ? 1 - Math.abs(fitness) : fitness;

        return fitness;
    }

    /**
     * Put current individual in the json object or adjust current object and write to file.
     */
    public void writeIndividualJSON() {
        JSONObject jsonIndividual = new JSONObject();
        if (jsonPath.isPresent()) {
            // Call adjust json function to say age += 1
            jsonIndividual = adjustJSONIndividual(jsonPath.get());
            // if the object is empty we shouldn't write it to file
            if (jsonIndividual.isEmpty())
                return;
        } else {
            if (resultPath.isPresent()) { // Should be able to do this with the javaPath
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
     *
     * @param fileName the file of the JSON object.
     * @return the new JSON object.
     */
    private JSONObject adjustJSONIndividual(String fileName) {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName)) {
            JSONObject jsonIndividual = (JSONObject) jsonParser.parse(reader);
            int age = (int) jsonIndividual.get("age");
            jsonIndividual.replace("age", age, age + 1);
            return jsonIndividual;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    /**
     * Create new JSON object with all the data for this individual.
     *
     * @return the json object.
     */
    public JSONObject createNewJSON() {
        JSONObject jsonIndividual = new JSONObject();
        metrics.forEach((key, value) -> jsonIndividual.put(key.getName(), value));
        jsonIndividual.put("age", 1);
        jsonIndividual.put("introduced_generation", getGeneration());
        List<MetamorphicIndividual> parents = getParents();
        if (!parents.isEmpty()) {
            jsonIndividual.put("parent_2", individualToJSON(parents.get(1)));
            jsonIndividual.put("parent_1", individualToJSON(parents.get(0)));
        }
        jsonIndividual.put("hash_with_lifetime", hashWithLifetime(1));
        jsonIndividual.put("hash", hashCode());
        jsonIndividual.put("genotype", individualToJSON(this));
        return jsonIndividual;
    }

    /**
     * Transforms metamorphic individual to a JSON compatible string.
     *
     * @param individual the individual.
     * @return the JSON compatible string.
     */
    private JSONArray individualToJSON(MetamorphicIndividual individual) {
        JSONArray jsonArray = new JSONArray();
        for (Transformer transformer : individual.getTransformers()) {
            JSONObject jsonTransformer = new JSONObject();
            String[] temp = transformer.getClass().toString().split("\\.");
            String name = temp[temp.length - 1];
            jsonTransformer.put("transformer", name);
            jsonTransformer.put("seed", transformer.getSeed());
            jsonArray.add(jsonTransformer);
        }
        return jsonArray;
    }

    @Override
    public String toString() {
        String geneString = "[";
        for (Transformer i : transformers) {
            String[] temp = i.getClass().toString().split("\\.");
            String addition = temp[temp.length - 1];
            geneString += addition + ", ";
        }
        if (geneString.length() < 5) {
            return "[]";
        } else {
            return geneString.substring(0, geneString.length() - 2) + "]";
        }
    }

    /**
     * We make a separation between the identity and the concept of an individual.
     * If you have the same list of transformers, just at a different generation, you will create the same genotype.
     * To keep track of time of individuals, we have "hashWithLifetime"
     *
     * @return
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((transformers == null) ? 0 : transformers.hashCode());
        return result;
    }

    public int hashWithLifetime(int lifetime) {
        final int prime = 31;
        int result = this.hashCode();
        result = prime * result + lifetime ^ lifetime;
        return result;
    }

    /**
     * Calculates the hashCode and returns it as Hexadezimal String.
     * Note: This also handles "Error-Cases" if the Hashes Hex is smaller than 0.
     * @return The HashCode as Hexadezimal, cut to 6 Digits
     */
    public String hexHash() {
        final int DEFAULT_LENGTH = 6;
        String base = Integer.toHexString(this.hashCode());
        if (base.length()>=DEFAULT_LENGTH){
            return base.substring(0,DEFAULT_LENGTH);
        } else {
            return "0".repeat(DEFAULT_LENGTH-base.length()) + base;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MetamorphicIndividual)) {
            return false;
        }
        MetamorphicIndividual sO = (MetamorphicIndividual) o;
        if (sO == this) {
            return true;
        }
        if (this.getLength() != sO.getLength())
            return false;
        // Step by Step Comparison of Transformers
        // Note: Comparing Hashes is not "allowed",
        // as equals needs to be different from hashCode for some of Javas Fall-Back Logic on HashCollision
        for (int i = 0; i < this.getLength(); i++){
            if(!this.getGene(i).equals(sO.getGene(i))){
                return false;
            }
        }
        return true;
    }

}
