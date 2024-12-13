package com.yahoo.example.docproc;

import java.util.List;
import java.util.Map;

public class Results {
    String domain;
    Map<String, Integer> subjectHistogram;
    Map<String, Integer> bodyHistogram;
    List<String> emails;


    public Results(String domain, Map<String, Integer> subjectHistogram, Map<String, Integer> bodyHistogram, List<String> emails) {
        this.domain = domain;
        this.subjectHistogram = subjectHistogram;
        this.bodyHistogram = bodyHistogram;
        this.emails = emails;
    }

    @Override
    public String toString() {
        return "Results{" +
                "domain='" + domain + '\'' +
                ", subjectHistogram=" + subjectHistogram +
                ", bodyHistogram=" + bodyHistogram +
                ", emails=" + emails +
                '}';
    }
}
