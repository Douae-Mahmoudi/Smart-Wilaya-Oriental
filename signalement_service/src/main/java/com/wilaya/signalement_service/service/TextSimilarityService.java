package com.wilaya.signalement_service.service;

import com.wilaya.signalement_service.util.TextPreprocessor;
import com.wilaya.signalement_service.util.TfIdfVectorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TextSimilarityService {

    private static final Logger log = LoggerFactory.getLogger(TextSimilarityService.class);

    private final TfIdfVectorizer vectorizer;

    @Value("${signalement.doublon.similarity.threshold:0.6}")
    private double similarityThreshold;

    public TextSimilarityService(TfIdfVectorizer vectorizer) {
        this.vectorizer = vectorizer;
    }

    public double computeSimilarity(String desc1, String desc2) {
        if (desc1 == null || desc2 == null) return 0.0;
        String stem1 = TextPreprocessor.stem(desc1);
        String stem2 = TextPreprocessor.stem(desc2);
        if (stem1.isBlank() || stem2.isBlank()) return 0.0;

        Map<String, Double> vec1 = vectorizer.getTfIdfVector(stem1);
        Map<String, Double> vec2 = vectorizer.getTfIdfVector(stem2);

        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Map.Entry<String, Double> e1 : vec1.entrySet()) {
            Double v2 = vec2.get(e1.getKey());
            if (v2 != null) {
                dot += e1.getValue() * v2;
            }
            norm1 += e1.getValue() * e1.getValue();
        }
        for (Double v : vec2.values()) {
            norm2 += v * v;
        }

        if (norm1 == 0 || norm2 == 0) return 0.0;
        double similarity = dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
        log.info("Similarité calculée : {}", similarity);
        return similarity;
    }

    public boolean areSimilar(String desc1, String desc2) {
        return computeSimilarity(desc1, desc2) >= similarityThreshold;
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }
}