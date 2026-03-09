package com.gamecatarellibarber.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class PhotoStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Path uploadsDir;

    public PhotoStorageService(@Value("${app.uploads.dir:./uploads}") String uploadsDir) {
        this.uploadsDir = Path.of(uploadsDir).toAbsolutePath().normalize();
    }

    public String storeBarberPhoto(Long barberId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de foto vazio.");
        }
        if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Tipo de arquivo não suportado. Use JPG, PNG ou WEBP.");
        }
        if (file.getSize() > 5L * 1024L * 1024L) {
            throw new IllegalArgumentException("Arquivo muito grande. Máximo 5MB.");
        }

        try {
            Files.createDirectories(uploadsDir);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar a pasta de uploads.", e);
        }

        String ext = extensionForContentType(file.getContentType());
        String safeName = "barber-" + barberId + "-" + UUID.randomUUID() + ext;

        Path destination = uploadsDir.resolve(safeName).normalize();
        if (!destination.startsWith(uploadsDir)) {
            throw new IllegalStateException("Caminho de upload inválido.");
        }

        try {
            Files.write(destination, file.getBytes());
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao salvar o arquivo.", e);
        }

        return "/uploads/" + safeName;
    }

    private String extensionForContentType(String contentType) {
        String ct = contentType.toLowerCase(Locale.ROOT);
        return switch (ct) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}

