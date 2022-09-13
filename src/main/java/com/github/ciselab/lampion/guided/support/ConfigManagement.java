package com.github.ciselab.lampion.guided.support;

import static com.github.ciselab.lampion.guided.support.GenotypeSupport.dir_path;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigManagement {

    private final MetricCache metricCache;
    private final BashRunner bashRunner;
    public String configFile = dir_path + "/src/main/resources/config.properties";
    private long seed = 200;
    private boolean removeAllComments = false;
    private TransformationScope transformationScope = TransformationScope.perClass;
    private boolean maximize = true;
    private final Logger logger = LogManager.getLogger(ConfigManagement.class);
    private boolean useGA = true;
    private boolean dataPointSpecific = true;

    public ConfigManagement(MetricCache metricCache, BashRunner bashRunner) {
        this.metricCache = metricCache;
        this.bashRunner = bashRunner;
    }

    public boolean getDataPointSpecific() {
        return dataPointSpecific;
    }

    public boolean getUseGa() {
        return useGA;
    }

    public long getSeed() {
        return seed;
    }

    public boolean getRemoveAllComments() {
        return removeAllComments;
    }

    public TransformationScope getTransformationScope() {
        return transformationScope;
    }

    public boolean getMaximize() {
        return maximize;
    }

    public void setConfigFile(String config) {
        configFile = config;
    }

    public void setMaximize(boolean max) {
        maximize = max;
    }

    /**
     * Initialize global fields with config file data.
     */
    public Properties initializeFields() throws FileNotFoundException {
        Properties prop = new Properties();
        try {
            InputStream input = new FileInputStream(configFile);
            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            logger.error("Cannot load property file.");
            throw new FileNotFoundException("Could not find (or read) Configuration-File!");
        }

        if(prop.get("useGA") != null)
            useGA = prop.get("useGA").equals("true");
        else
            useGA = false;
        if(prop.get("dataPointSpecific") != null)
            dataPointSpecific = prop.get("dataPointSpecific").equals("true");
        else
            dataPointSpecific = false;
        if(prop.get("Optimization_objective") != null)
            maximize = prop.get("Optimization_objective").equals("max");
        if(prop.get("seed") != null)
            if(prop.getProperty("seed").equals("-1")) {
                logger.info("Generation random seed because no valid seed was given");
                seed = new Random().nextLong();
            } else
                seed = Long.parseLong((String) prop.get("seed"));
        if(prop.get("removeAllComments")!=null){
            removeAllComments = Boolean.parseBoolean((String) prop.get("removeAllComments"));
        }
        if(prop.get("transformationscope") != null){
            transformationScope = TransformationScope.valueOf(prop.getProperty("transformationscope"));
            if(!prop.getProperty("transformationscope").equals("global"))
                logger.debug("Transformation scope is not global, this might not be desired.");
        }
        if(prop.get("bash") != null)
            bashRunner.setPath_bash((String) prop.get("bash"));

        for(Metric.Name n: Metric.Name.values()) {
            if (n == Metric.Name.UNIMPLEMENTED)
                continue;
            var metric = createMetric(n);
            try {
                metric.setWeight(Float.parseFloat(prop.getProperty(n.toString())));
            } catch (Exception e){
                // This can happen in case of bad parsing, or missing property.
                // Just do nothing, go on with keeping the Metric at default weight 0
            }
            metricCache.addMetric(metric);
        }

        metricCache.initWeights(maximize);
        return prop;
    }

    /**
     * Create a new metric from the specified name.
     * @param name the metric name.
     * @return The new metric.
     */
    private Metric createMetric(Metric.Name name) {
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

    private Metric createMetric(String name){
        Metric.Name resolved = Metric.resolveName(name);
        return createMetric(resolved);
    }

    /*
    Note: Old Paths

    MRR + PMRR: dir_path + "/code2vec/results.txt"
    F1, Prec, Recall: dir_path + "/code2vec/F1_score_log.txt"
    len(Prediction),edit_distance: dir_path + "/code2vec/predicted_words.txt"
     */
}
