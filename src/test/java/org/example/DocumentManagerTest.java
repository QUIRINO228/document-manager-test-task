package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void testSave() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Document")
                .content("This is a test document.")
                .author(DocumentManager.Author.builder().id(UUID.randomUUID().toString()).name("Author").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        assertNotNull(savedDocument.getId());
        assertEquals("Test Document", savedDocument.getTitle());
    }

    @Test
    void testFindById() {
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Document")
                .content("This is a test document.")
                .author(DocumentManager.Author.builder().id(UUID.randomUUID().toString()).name("Author").build())
                .created(Instant.now())
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);
        Optional<DocumentManager.Document> foundDocument = documentManager.findById(savedDocument.getId());

        assertTrue(foundDocument.isPresent());
        assertEquals(savedDocument.getId(), foundDocument.get().getId());
    }

    @Test
    void testSearch() {
        DocumentManager.Author author = DocumentManager.Author.builder().id(UUID.randomUUID().toString()).name("Author").build();
        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("Test Document 1")
                .content("This is the first test document.")
                .author(author)
                .created(Instant.now())
                .build();
        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("Test Document 2")
                .content("This is the second test document.")
                .author(author)
                .created(Instant.now())
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Test Document"))
                .build();

        List<DocumentManager.Document> results = documentManager.search(request);

        assertEquals(2, results.size());
    }
}