package com.wilaya.signalement_service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> TYPES_AUTORISES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/jpg"
    );

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload.dir:uploads/signalements}") String uploadDirProperty) {
        this.uploadDir = Paths.get(uploadDirProperty).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de créer le répertoire d'upload", e);
        }
    }


    public String sauvegarder(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            return null;
        }

        String contentType = fichier.getContentType();
        if (contentType == null || !TYPES_AUTORISES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Type de fichier non autorisé. Formats acceptés : JPEG, PNG, WEBP.");
        }

        String extension = extraireExtension(fichier.getOriginalFilename());
        String nomFichier = UUID.randomUUID() + extension;

        try {
            Path cible = this.uploadDir.resolve(nomFichier);
            Files.copy(fichier.getInputStream(), cible, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Échec de l'enregistrement du fichier", e);
        }

        return "/signalements/photos/" + nomFichier;
    }

    public Path resoudre(String nomFichier) {
        return uploadDir.resolve(nomFichier).normalize();
    }

    private String extraireExtension(String nomOriginal) {
        if (nomOriginal == null || !nomOriginal.contains(".")) {
            return "";
        }
        return nomOriginal.substring(nomOriginal.lastIndexOf('.'));
    }
}