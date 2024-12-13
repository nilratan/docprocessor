package com.yahoo.example.docproc;

import java.util.HashMap;
import java.util.Map;

public class HistogramCalculator {
    public static Map<String, Integer> computeHistogram(String content) {
        Map<String, Integer> histogram = new HashMap<>();
        String[] words = content.split("\\W+");
        for (String word : words) {
            if (!word.isEmpty()) {
                histogram.put(word.toLowerCase(), histogram.getOrDefault(word.toLowerCase(), 0) + 1);
            }
        }
        return histogram;
    }

    public static void printHistogram(Map<String, Integer> histogram) {
        histogram.forEach((word, count) -> System.out.println(word + ": " + count));
    }
}
