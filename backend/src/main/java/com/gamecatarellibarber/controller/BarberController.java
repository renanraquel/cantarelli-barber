package com.gamecatarellibarber.controller;

import com.gamecatarellibarber.entity.Barber;
import com.gamecatarellibarber.service.BarberService;
import com.gamecatarellibarber.service.PhotoStorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbers")
@CrossOrigin
public class BarberController {

    private final BarberService barberService;
    private final PhotoStorageService photoStorageService;

    public BarberController(BarberService barberService, PhotoStorageService photoStorageService) {
        this.barberService = barberService;
        this.photoStorageService = photoStorageService;
    }

    @GetMapping
    public List<Barber> getAll() {
        return barberService.findAll();
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
        String publicPath = photoStorageService.storeBarberPhoto(id, file);
        return barberService.updatePhotoUrl(id, publicPath);
    }
}

