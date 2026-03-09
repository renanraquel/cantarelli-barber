package com.gamecatarellibarber.controller;

import com.gamecatarellibarber.dto.RevenueEntryDto;
import com.gamecatarellibarber.entity.Barber;
import com.gamecatarellibarber.entity.Revenue;
import com.gamecatarellibarber.repository.BarberRepository;
import com.gamecatarellibarber.service.RevenueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/revenue")
@CrossOrigin
public class RevenueController {

    private final RevenueService revenueService;
    private final BarberRepository barberRepository;

    public RevenueController(RevenueService revenueService, BarberRepository barberRepository) {
        this.revenueService = revenueService;
        this.barberRepository = barberRepository;
    }

    public record RevenueRequest(Long barberId, BigDecimal value, String date) {
    }

    @GetMapping
    public RevenueService.PageResult<RevenueEntryDto> list(
            @RequestParam(required = false) Long barberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return revenueService.findPageOrderedByDateDesc(barberId, page, size);
    }

    @GetMapping("/{id}")
    public RevenueEntryDto getById(@PathVariable Long id) {
        return revenueService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Revenue create(@Valid @RequestBody RevenueRequest request) {
        Barber barber = barberRepository.findById(request.barberId())
                .orElseThrow(() -> new IllegalArgumentException("Barber not found: " + request.barberId()));

        Revenue revenue = new Revenue();
        revenue.setBarber(barber);
        revenue.setValue(request.value());
        revenue.setDate(LocalDate.parse(request.date()));
        return revenueService.save(revenue);
    }

    @PutMapping("/{id}")
    public RevenueEntryDto update(@PathVariable Long id, @Valid @RequestBody RevenueRequest request) {
        return revenueService.update(id, request.barberId(), request.value(), LocalDate.parse(request.date()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        revenueService.delete(id);
    }

    @GetMapping("/week")
    public Map<Long, BigDecimal> getWeekRevenue() {
        return revenueService.getCurrentWeekRevenuePerBarber();
    }
}

