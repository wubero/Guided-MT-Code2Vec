package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import com.github.ciselab.support.GenotypeSupport;
import java.io.File;
import java.util.List;

public class InputLength extends Metric {

    private final String filePath = GenotypeSupport.dir_path + "/code2vec/data/";

    private String dataset;

    public InputLength() {
        super("InputLength");
    }

    @Override
    public double calculateScore() {
        if(dataset != null) {
            // should read all files not the dataset...
            int count = 0;
            try {
                File file = new File(filePath + dataset + "/test");
                for (File i : file.listFiles()) {
                    List<String> lines = readPredictions(i.getPath());
                    count += lines.size();
                }
                return count;
            } catch(NullPointerException e) {
                logger.debug("Couldn't get files input length set to 0.");
            }
        }
        return 0;
    }

    public void setDataSet(String dir) {
        dataset = dir;
    }
}
