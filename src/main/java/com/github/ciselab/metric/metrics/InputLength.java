package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;
import com.github.ciselab.support.GenotypeSupport;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class InputLength extends Metric {

    private final String filePath = GenotypeSupport.dir_path + "/code2vec/data/";

    private static String dataset;

    public InputLength() {
        super("Input_length");
    }

    @Override
    public double CalculateScore() {
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
                System.out.println("Couldn't get files input length set to 0.");
            }
        }
        return 0;
    }

    public static void setDataSet(String dir) {
        dataset = dir;
    }
}
