package com.github.ciselab.metric.metrics;

import com.github.ciselab.metric.Metric;

import java.util.ArrayList;
import java.util.List;

public class EditDistance extends Metric {

    public EditDistance(String resultPath) {
        super("EditDistance", resultPath);
    }

    @Override
    public double calculateScore() {
        List<String> lines = readPredictions(path);
        scores = new ArrayList<>();
        float score = 0;
        for(String i: lines) {
            if(i.contains("Original") && i.contains("predicted")) {
                String[] t = i.split(", ");
                String original = t[0].split(": ")[1];
                String predicted = t[1].split(": ")[1];
                float distance = 1/(editDistance(original, predicted)+1);
                score += distance; // when the editdistance is larger the resulting score will be lower.
                scores.add(distance);
            }
        }
        return score/lines.size();
    }

    /**
     * Calculate the edit distance between two strings.
     * Code gotten from: https://www.programcreek.com/2013/12/edit-distance-in-java/
     * @param original the original word.
     * @param predicted the predicted word.
     * @return the edit distance.
     */
    public float editDistance(String original, String predicted) {
        int len1 = original.length();
        int len2 = predicted.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        for (int i = 0; i < len1; i++) {
            char charA = original.charAt(i);
            for (int j = 0; j < len2; j++) {
                char charB = predicted.charAt(j);
                if (charA == charB) {
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = Math.min(replace, insert);
                    min = Math.min(delete, min);
                    dp[i + 1][j + 1] = min;
                }
            }
        }
        return dp[len1][len2];
    }
}
