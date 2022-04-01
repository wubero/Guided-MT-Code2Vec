package com.github.ciselab.metric;

public abstract class Metric implements Comparable<Metric> {

    protected double score;
    protected String name;

    public Metric(String metricName) {
        this.name = metricName;
        this.score = -1;
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
