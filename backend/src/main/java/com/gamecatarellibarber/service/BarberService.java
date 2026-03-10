package com.gamecatarellibarber.service;

import com.gamecatarellibarber.entity.Barber;
import com.gamecatarellibarber.repository.BarberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BarberService {

    private final BarberRepository barberRepository;

    public BarberService(BarberRepository barberRepository) {
        this.barberRepository = barberRepository;
    }

    @Transactional(readOnly = true)
    public List<Barber> findAll() {
        return barberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Barber> findAllActive() {
        return barberRepository.findByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Barber getById(Long id) {
        return barberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Barber not found: " + id));
    }

    public Barber create(Barber barber) {
        barber.setId(null);
        return barberRepository.save(barber);
    }

    public Barber update(Long id, Barber updated) {
        Barber existing = getById(id);
        existing.setName(updated.getName());
        existing.setPhotoUrl(updated.getPhotoUrl());
        existing.setWeeklyGoal(updated.getWeeklyGoal());
        existing.setMonthlyGoal(updated.getMonthlyGoal());
        existing.setActive(updated.isActive());
        return barberRepository.save(existing);
    }

    public Barber updatePhotoUrl(Long id, String photoUrl) {
        Barber existing = getById(id);
        existing.setPhotoUrl(photoUrl);
        return barberRepository.save(existing);
    }

    /** Salva a foto no banco de dados e define photoUrl para o endpoint de recuperação. */
    public Barber storePhoto(Long id, byte[] photoData, String contentType) {
        Barber existing = getById(id);
        existing.setPhotoData(photoData);
        existing.setPhotoContentType(contentType);
        existing.setPhotoUrl("/api/barbers/" + id + "/photo");
        return barberRepository.save(existing);
    }
}

