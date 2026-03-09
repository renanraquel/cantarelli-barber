package com.gamecatarellibarber.controller;

import com.gamecatarellibarber.dto.HallOfFameDto;
import com.gamecatarellibarber.service.HallOfFameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hall-of-fame")
@CrossOrigin
public class HallOfFameController {

    private final HallOfFameService hallOfFameService;

    public HallOfFameController(HallOfFameService hallOfFameService) {
        this.hallOfFameService = hallOfFameService;
    }

    @GetMapping
    public List<HallOfFameDto> getAll() {
        return hallOfFameService.findAllOrdered();
    }

    @GetMapping("/latest")
    public Optional<HallOfFameDto> getLatest() {
        return hallOfFameService.getLatest();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HallOfFameDto create(@RequestBody HallOfFameDto dto) {
        return hallOfFameService.create(dto);
    }

    @PutMapping("/{id}")
    public HallOfFameDto update(@PathVariable Long id, @RequestBody HallOfFameDto dto) {
        return hallOfFameService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        hallOfFameService.delete(id);
    }
}

