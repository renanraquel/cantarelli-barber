package com.gamecatarellibarber.controller;

import com.gamecatarellibarber.entity.Barber;
import com.gamecatarellibarber.service.BarberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/barbers")
@CrossOrigin
public class BarberController {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5L * 1024L * 1024L;

    private final BarberService barberService;

    public BarberController(BarberService barberService) {
        this.barberService = barberService;
    }

    @GetMapping
    public List<Barber> getAll() {
        return barberService.findAll();
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        Barber barber = barberService.getById(id);
        if (barber.getPhotoData() == null || barber.getPhotoData().length == 0) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = MediaType.parseMediaType(
                barber.getPhotoContentType() != null ? barber.getPhotoContentType() : "image/jpeg");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setCacheControl("max-age=86400");
        return ResponseEntity.ok()
                .headers(headers)
                .body(barber.getPhotoData());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Barber create(@Valid @RequestBody Barber barber) {
        return barberService.create(barber);
    }

    @PutMapping("/{id}")
    public Barber update(@PathVariable Long id, @Valid @RequestBody Barber barber) {
        return barberService.update(id, barber);
    }

    @PostMapping(value = "/{id}/photo", consumes = "multipart/form-data")
    public Barber uploadPhoto(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de foto vazio.");
        }
        if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Tipo de arquivo não suportado. Use JPG, PNG ou WEBP.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Arquivo muito grande. Máximo 5MB.");
        }
        try {
            return barberService.storePhoto(id, file.getBytes(), file.getContentType());
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao salvar a foto.", e);
        }
    }
}

