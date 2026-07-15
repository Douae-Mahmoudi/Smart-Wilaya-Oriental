package com.wilaya.signalement_service.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    private FileStorageService storageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        storageService = new FileStorageService(tempDir.toString());
    }

    @Test
    void testSauvegarderFichierValide() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "image content".getBytes());

        String path = storageService.sauvegarder(file);

        assertNotNull(path);
        assertTrue(path.startsWith("/signalements/photos/"));
        assertTrue(path.endsWith(".jpg"));
    }

    @Test
    void testSauvegarderFichierVideOuNullRetourneNull() {
        assertNull(storageService.sauvegarder(null));
        assertNull(storageService.sauvegarder(new MockMultipartFile("file", new byte[0])));
    }

    @Test
    void testSauvegarderTypeInvalideLanceException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());

        assertThrows(IllegalArgumentException.class, () -> storageService.sauvegarder(file));
    }

    @Test
    void testResoudreChemin() {
        String nomFichier = "test.jpg";
        Path path = storageService.resoudre(nomFichier);

        assertEquals(tempDir.toAbsolutePath().normalize().resolve(nomFichier), path);
    }
}