package com.github.ciselab.lampion.guided.configuration;

import com.github.ciselab.lampion.core.program.Engine.TransformationScope;
import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.metric.metrics.EditDistance;
import com.github.ciselab.lampion.guided.metric.metrics.F1;
import com.github.ciselab.lampion.guided.metric.metrics.InputLength;
import com.github.ciselab.lampion.guided.metric.metrics.MRR;
import com.github.ciselab.lampion.guided.metric.metrics.PercentageMRR;
import com.github.ciselab.lampion.guided.metric.metrics.Precision;
import com.github.ciselab.lampion.guided.metric.metrics.PredictionLength;
import com.github.ciselab.lampion.guided.metric.metrics.Recall;
import com.github.ciselab.lampion.guided.metric.metrics.Transformations;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import com.github.ciselab.lampion.guided.support.MetricCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigManagement {

    private static final Logger logger = LogManager.getLogger(ConfigManagement.class);

    /**
     * Tries to read the config.properties at a given path.
     * Generates a configuration with default values first, new values overwrite given defaults.
     * @param path the path to the .properties file
     * @return the read configuration.
     */
    public static Configuration readConfig(String path) throws IOException {
        var config = new Configuration();

        var prop = readProperties(path);

        /*
        ==================== General Attributes ============================
         */
        if(prop.get("useGA") != null)
            config.program.setUseGA(Boolean.parseBoolean(prop.get("useGA").toString()));
        if(prop.get("bash") != null)
            config.program.setBashPath((String) prop.get("bash"));
        if(prop.get("maxtime") != null)
            config.program.setMaxTimeInMin(Integer.parseInt(prop.get("maxtime").toString()));
        // Data, model and other paths are passed to the program by args

        if(prop.get("seed") != null) {
            if (prop.getProperty("seed").equals("-1")) {
                logger.info("Generating random seed");
                config.program.setSeed(new Random().nextLong());
            } else
                config.program.setSeed(Long.parseLong((String) prop.get("seed")));
        }
        /*
        ==================== Genetic Attributes ============================
         */

        if(prop.get("crossoverrate") != null){
            config.genetic.setCrossoverRate(Double.parseDouble(prop.get("crossoverrate").toString()));
        }
        if(prop.get("elitismrate") != null){
            config.genetic.setElitismRate(Double.parseDouble(prop.get("elitismrate").toString()));
        }
        if(prop.get("mutationrate") != null){
            config.genetic.setMutationRate(Double.parseDouble(prop.get("mutationrate").toString()));
        }
        if(prop.get("increaserate") != null){
            config.genetic.setIncreaseSizeRate(Double.parseDouble(prop.get("increaserate").toString()));
        }
        if(prop.get("growthfactor") != null){
            config.genetic.setGrowthFactor(Double.parseDouble(prop.get("growthfactor").toString()));
        }

        if(prop.get("maxgenelength") != null){
            config.genetic.setMaxGeneLength(Integer.parseInt(prop.get("maxgenelength").toString()));
        }
        if(prop.get("populationsize") != null){
            config.genetic.setPopSize(Integer.parseInt(prop.get("populationsize").toString()));
        }
        if(prop.get("tournamentsize") != null){
            config.genetic.setTournamentSize(Integer.parseInt(prop.get("tournamentsize").toString()));
        }
        if(prop.get("maxsteadygenerations") != null){
            config.genetic.setMaxSteadyGenerations(Integer.parseInt(prop.get("maxsteadygenerations").toString()));
        }

        /*
        ==================== Lampion Attributes ============================
         */
        if(prop.get("removeAllComments")!=null){
            config.lampion.setRemoveAllComments(Boolean.parseBoolean((String) prop.get("removeAllComments")));
        }
        if(prop.get("transformationscope") != null){
            var transformationScope = TransformationScope.valueOf(prop.getProperty("transformationscope"));
            config.lampion.setTransformationScope(transformationScope);
            if(!prop.getProperty("transformationscope").equals("global"))
                logger.debug("Transformation scope is not global, this might not be desired.");
        }

        return config;
    }

    public static MetricCache initializeMetricCache(String path) throws IOException {
        MetricCache cache = new MetricCache();
        var prop = readProperties(path);

        for(Metric.Name n: Metric.Name.values()) {
            if (n == Metric.Name.UNIMPLEMENTED)
                continue;
            var metric = createMetric(n);
            try {
                String weightProp = prop.getProperty(n.toString());
                var weight =Float.parseFloat(weightProp);
                metric.setWeight(weight);
            } catch (Exception e) {
                logger.warn("Issue in Parsing Weight of " + n + " - Continuing with weight 0",e);
                // This can happen in case of bad parsing, or missing property.
                // Just do nothing, go on with keeping the Metric at default weight 0
            }
            cache.addMetric(metric);
        }
        cache.initWeights();
        for(Metric metric: cache.getMetrics()) {
            metric.setObjective(Float.parseFloat(prop.get(metric.getName()).toString()) > 0);
        }

        return cache;
    }

    private static Properties readProperties(String path) throws IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream(path);

        prop.load(input);
        input.close();

        return prop;
    }

    /**
     * Create a new metric from the specified name.
     * @param name the metric name.
     * @return The new metric.
     */
    private static Metric createMetric(Metric.Name name) {
        switch (name) {
            case MRR:
                return new MRR();
            case F1:
                return new F1();
            case PMRR:
                return new PercentageMRR();
            case PREC:
                return new Precision();
            case REC:
                return new Recall();
            case EDITDIST:
                return new EditDistance();
            case INPUTLENGTH:
                return new InputLength();
            case PREDLENGTH:
                return new PredictionLength();
            case TRANSFORMATIONS:
                return new Transformations();
            default:
                logger.error("Metric name not a correct metric.");
                throw new IllegalArgumentException("Metric name not a correct metric.");
        }
    }

}
