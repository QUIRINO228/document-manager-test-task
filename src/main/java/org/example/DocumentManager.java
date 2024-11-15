package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DocumentManager {

    private final Map<String, Document> documentStore = new ConcurrentHashMap<>();

    public Document save(Document document) {
        if (document.getId() == null) {
            document.setId(UUID.randomUUID().toString());
        }
        documentStore.put(document.getId(), document);
        return document;
    }

    public List<Document> search(SearchRequest request) {
        return documentStore.values().stream()
                .filter(doc -> request.getTitlePrefixes() == null || request.getTitlePrefixes().stream().anyMatch(prefix -> doc.getTitle().startsWith(prefix)))
                .filter(doc -> request.getContainsContents() == null || request.getContainsContents().stream().anyMatch(content -> doc.getContent().contains(content)))
                .filter(doc -> request.getAuthorIds() == null || request.getAuthorIds().contains(doc.getAuthor().getId()))
                .filter(doc -> request.getCreatedFrom() == null || !doc.getCreated().isBefore(request.getCreatedFrom()))
                .filter(doc -> request.getCreatedTo() == null || !doc.getCreated().isAfter(request.getCreatedTo()))
                .collect(Collectors.toList());
    }

    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documentStore.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}