package com.yahoo.example.docproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistogramMerge {

    Map<String, Map<String, Integer>> domainToMergedSubjectHistogram = new HashMap<>();
    Map<String, Map<String, Integer>> domainToMergedBodyHistogram = new HashMap<>();
    Map<String, List<String>> domainToEmails = new HashMap<>();
    Map<String, Integer> mergedSubjectHistogram = new HashMap<>();
    Map<String, Integer> mergedBodyHistogram = new HashMap<>();
    List<String> emails = new ArrayList<>();


    public HistogramMerge() {}
    public HistogramMerge(List<Results> results) {
        for (Results result : results) {
            add(result);
        }
    }

    public void add(Results result) {
        String domain = result.domain;
        mergeWith(domain,  result.subjectHistogram, domainToMergedSubjectHistogram);
        mergeWith(domain,  result.bodyHistogram, domainToMergedBodyHistogram);
        mergeWith(result.subjectHistogram, mergedSubjectHistogram);
        mergeWith(result.bodyHistogram, mergedBodyHistogram);
        List<String> emailsForDomain = domainToEmails.computeIfAbsent(domain, (d) -> new ArrayList<>());
        emailsForDomain.addAll(result.emails);
        this.emails.addAll(result.emails);
    }

    void mergeWith(Map<String, Integer> resultMap, Map<String, Integer> mergeInto) {
        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            Integer count = mergeInto.get(entry.getKey());
            Integer additionCount = entry.getValue();
            if (count == null) { count = 0;}
            if (additionCount == null) { additionCount = 0;}
            mergeInto.put(entry.getKey(), count + additionCount);
        }
    }

    void mergeWith(String domain, Map<String, Integer> resultMap, Map<String, Map<String, Integer>> mergeInto) {
        Map<String, Integer> subject = mergeInto.computeIfAbsent(domain, (d) -> new HashMap<>());
        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            Integer count = subject.get(entry.getKey());
            if (count == null) {
                count = 0;
            }
            count += entry.getValue();
            subject.put(entry.getKey(), count);
        }
    }

    @Override
    public String toString() {
        return "HistogramMerge{" +
                "domainToMergedSubjectHistogram=" + domainToMergedSubjectHistogram +
                ", domainToMergedBodyHistogram=" + domainToMergedBodyHistogram +
                ", domainToEmails=" + domainToEmails +
                ", mergedSubjectHistogram=" + mergedSubjectHistogram +
                ", mergedBodyHistogram=" + mergedBodyHistogram +
                ", emails=" + emails +
                '}';
    }
}
