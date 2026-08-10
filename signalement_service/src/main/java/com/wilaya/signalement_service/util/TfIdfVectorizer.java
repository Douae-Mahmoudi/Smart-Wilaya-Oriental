package com.wilaya.signalement_service.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TfIdfVectorizer {

    private final Map<String, Double> idfMap = new ConcurrentHashMap<>();
    private int totalDocuments = 0;


    public void rebuildIndex(List<String> processedDescriptions) {
        if (processedDescriptions.isEmpty()) {
            idfMap.clear();
            totalDocuments = 0;
            return;
        }

        Map<String, Integer> documentFrequency = new HashMap<>();
        for (String doc : processedDescriptions) {
            Set<String> uniqueTerms = new HashSet<>(Arrays.asList(doc.split(" ")));
            for (String term : uniqueTerms) {
                documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
            }
        }

        totalDocuments = processedDescriptions.size();
        idfMap.clear();
        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet()) {
            double idf = Math.log((totalDocuments + 1.0) / (entry.getValue() + 1.0)) + 1.0;
            idfMap.put(entry.getKey(), idf);
        }
    }


    public Map<String, Double> getTfIdfVector(String processedText) {
        String[] terms = processedText.split(" ");
        Map<String, Integer> termFrequency = new HashMap<>();
        for (String t : terms) {
            termFrequency.put(t, termFrequency.getOrDefault(t, 0) + 1);
        }

        Map<String, Double> vector = new HashMap<>();
        for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
            String term = entry.getKey();
            double tf = 1.0 + Math.log(entry.getValue());
            double idf = idfMap.getOrDefault(term, 1.0);
            vector.put(term, tf * idf);
        }
        return vector;
    }

    public int getTotalDocuments() {
        return totalDocuments;
    }
}