package com.github.ciselab.lampion.guided.program;

import com.github.ciselab.lampion.guided.configuration.Configuration;
import com.github.ciselab.lampion.guided.metric.Metric;
import com.github.ciselab.lampion.guided.metric.metrics.F1;
import com.github.ciselab.lampion.guided.program.Main;
import com.github.ciselab.lampion.guided.support.FileManagement;

import com.github.ciselab.lampion.guided.support.MetricCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class MainTest {

    @AfterEach
    public void after() {
        FileManagement.removeOtherDirs(FileManagement.dataDir);
    }

    /*TODO REIMPLEMENT
    @Tag("Slow")
    @Tag("File")
    @Test
    public void mainIntegrationTest() {
        Configuration config = new Configuration();
        config.genetic.setMaxSteadyGenerations(1);
        config.genetic.setPopSize(1);

        MetricCache cache = new MetricCache();
        Metric m = new F1();
        m.setWeight(1);
        cache.addMetric(m);

        Main.setConfig(config);
        Main.setCache(cache);

        Main.runSimpleGA();
    }
    
     */

}
