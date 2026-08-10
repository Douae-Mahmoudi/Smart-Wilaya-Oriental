package com.wilaya.signalement_service.config;

import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.repository.SignalementRepository;
import com.wilaya.signalement_service.util.TextPreprocessor;
import com.wilaya.signalement_service.util.TfIdfVectorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TfIdfIndexInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(TfIdfIndexInitializer.class);

    private final SignalementRepository repository;
    private final TfIdfVectorizer vectorizer;

    public TfIdfIndexInitializer(SignalementRepository repository, TfIdfVectorizer vectorizer) {
        this.repository = repository;
        this.vectorizer = vectorizer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeIndex() {
        LOG.info("Rebuilding TF‑IDF index from existing signalements...");
        List<String> descriptions = repository.findAll().stream()
                .map(Signalement::getDescription)
                .filter(desc -> desc != null && !desc.isBlank())
                .map(TextPreprocessor::stem)
                .collect(Collectors.toList());
        vectorizer.rebuildIndex(descriptions);
        LOG.info("Index rebuilt with {} documents", vectorizer.getTotalDocuments());
    }
}