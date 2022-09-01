package com.github.ciselab.support;

import java.util.*;

/**
 * This class implements the maintenance for the Pareto front.
 * It checks whether a new solution is Pareto dominant over the current solutions and is thus
 * able to add it to the Pareto front.
 * For more explanation on the Pareto front you can take a look at
 * https://en.wikipedia.org/wiki/Pareto_front
 */
public class ParetoFront {

    private final MetricCache metricCache;
    private Set<double[]> pareto = new HashSet<>();

    public ParetoFront(MetricCache cache) {
        this.metricCache = cache;
    }

    public Set<double[]> getPareto() {
        return pareto;
    }

    public void setPareto(Set<double[]> newPareto) {
        pareto = newPareto;
    }

    /**
     * Add to the Pareto set if no solution dominates the current solution.
     * @param solution the current solution.
     */
    public void addToParetoOptimum(double[] solution) {
        if(isIn(pareto, solution))
            return;
        for(double[] i: pareto) {
            if (paretoDominant(i, solution)) {
                return;
            }
        }
        List<double[]> toRemove = new ArrayList<>();
        for(double[] i: pareto) {
            // if solution is dominant over i then delete i
            if (paretoDominant(solution, i)) {
                toRemove.add(i);
            }
        }
        toRemove.forEach(pareto::remove);
        pareto.add(solution);
    }

    /**
     * Support method to see if a set has a certain element in it.
     * @param set the set.
     * @param find the element.
     * @return Whether the set has the find element in it.
     */
    public boolean isIn(Set<double[]> set, double[] find) {
        Iterator<double[]> i = set.iterator();
        while(i.hasNext()) {
            if(Arrays.equals(i.next(), find)){
                return true;
            }
        }
        return false;
    }

    /**
     * Check if solutionA is Pareto dominant over solutionB.
     * @param solutionA a solution.
     * @param solutionB a solution.
     * @return whether solutionA is pareto dominant over solutionB.
     */
    private boolean paretoDominant(double[] solutionA, double[] solutionB) {
        boolean dominant = false;
        for(int i = 0; i < solutionA.length; i++) {
            // Check if the objective function is maximizing
            if(metricCache.getObjectives()[i]) {
                // Early exit for when this particular score is worse, can never be Pareto
                // dominant in that case.
                if(solutionA[i] < solutionB[i])
                    return false;
                if(solutionA[i] > solutionB[i])
                    dominant = true;
            } else {
                // Another early exit for this method.
                if(solutionA[i] > solutionB[i])
                    return false;
                if(solutionA[i] < solutionB[i])
                    dominant = true;
            }
        }
        return dominant;
    }
}
