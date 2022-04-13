package com.github.ciselab.metric;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Metric implements Comparable<Metric> {

    protected double score;
    protected String name;
    protected final String defaultPath = "C:/Users/Ruben-pc/Documents/Master_thesis/Guided-MT-Code2Vec/code2vec/results.txt";

    public Metric(String metricName) {
        this.name = metricName;
        this.score = -1;
    }

    public List<String> readPredictions(String filePath) {
        List<String> predictions = new ArrayList<>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(filePath));
            String line = bf.readLine();
            while(line != null) {
                predictions.add(line);
                line = bf.readLine();
            }
            bf.close();
        } catch(IOException e) {
            System.out.println("Couldn't read file");
        }
        return predictions;
    }

    public abstract double CalculateScore();

    @Override
    public int compareTo(Metric other) {
        if(this.score > other.score)
            return 0;
        else if(score < other.score)
            return 1;
        else
            return -1;
    }
}
