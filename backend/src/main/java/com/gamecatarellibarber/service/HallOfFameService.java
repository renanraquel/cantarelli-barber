package com.gamecatarellibarber.service;

import com.gamecatarellibarber.dto.HallOfFameDto;
import com.gamecatarellibarber.entity.Barber;
import com.gamecatarellibarber.entity.HallOfFameEntry;
import com.gamecatarellibarber.repository.BarberRepository;
import com.gamecatarellibarber.repository.HallOfFameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class HallOfFameService {

    private final HallOfFameRepository hallOfFameRepository;
    private final BarberRepository barberRepository;

    public HallOfFameService(HallOfFameRepository hallOfFameRepository, BarberRepository barberRepository) {
        this.hallOfFameRepository = hallOfFameRepository;
        this.barberRepository = barberRepository;
    }

    public HallOfFameDto create(HallOfFameDto dto) {
        Barber barber = barberRepository.findById(dto.getBarberId())
                .orElseThrow(() -> new IllegalArgumentException("Barber not found: " + dto.getBarberId()));

        HallOfFameEntry entry = new HallOfFameEntry();
        entry.setBarber(barber);
        entry.setMonth(dto.getMonth());
        entry.setYear(dto.getYear());
        entry.setPerformancePercent(dto.getPerformancePercent());

        HallOfFameEntry saved = hallOfFameRepository.save(entry);
        return toDto(saved);
    }

    public HallOfFameDto update(Long id, HallOfFameDto dto) {
        HallOfFameEntry entry = hallOfFameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hall of Fame entry not found: " + id));

        Barber barber = barberRepository.findById(dto.getBarberId())
                .orElseThrow(() -> new IllegalArgumentException("Barber not found: " + dto.getBarberId()));

        entry.setBarber(barber);
        entry.setMonth(dto.getMonth());
        entry.setYear(dto.getYear());
        entry.setPerformancePercent(dto.getPerformancePercent());

        return toDto(hallOfFameRepository.save(entry));
    }

    public void delete(Long id) {
        if (!hallOfFameRepository.existsById(id)) {
            throw new IllegalArgumentException("Hall of Fame entry not found: " + id);
        }
        hallOfFameRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<HallOfFameDto> findAllOrdered() {
        return hallOfFameRepository.findAllByOrderByYearAscMonthAsc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<HallOfFameDto> getLatest() {
        return hallOfFameRepository.findFirstByOrderByYearDescMonthDesc()
                .map(this::toDto);
    }

    private HallOfFameDto toDto(HallOfFameEntry entry) {
        HallOfFameDto dto = new HallOfFameDto();
        dto.setId(entry.getId());
        dto.setBarberId(entry.getBarber().getId());
        dto.setBarberName(entry.getBarber().getName());
        dto.setPhotoUrl(entry.getBarber().getPhotoUrl());
        dto.setMonth(entry.getMonth());
        dto.setYear(entry.getYear());
        dto.setPerformancePercent(entry.getPerformancePercent());
        return dto;
    }
}

