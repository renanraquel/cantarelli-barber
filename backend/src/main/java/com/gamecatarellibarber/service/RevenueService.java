package com.gamecatarellibarber.service;

import com.gamecatarellibarber.dto.RevenueEntryDto;
import com.gamecatarellibarber.entity.Barber;
import com.gamecatarellibarber.entity.Revenue;
import com.gamecatarellibarber.repository.BarberRepository;
import com.gamecatarellibarber.repository.RevenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RevenueService {

    private static final ZoneId ZONE_BRAZIL = ZoneId.of("America/Sao_Paulo");

    private final RevenueRepository revenueRepository;
    private final BarberRepository barberRepository;

    public RevenueService(RevenueRepository revenueRepository, BarberRepository barberRepository) {
        this.revenueRepository = revenueRepository;
        this.barberRepository = barberRepository;
    }

    /** Data de hoje no fuso do Brasil (semana/mês batem com o que o usuário vê). */
    private static LocalDate todayBrazil() {
        return LocalDate.now(ZONE_BRAZIL);
    }

    public Revenue save(Revenue revenue) {
        revenue.setId(null);
        return revenueRepository.save(revenue);
    }

    @Transactional(readOnly = true)
    public List<RevenueEntryDto> findAllOrderedByDateDesc(Long barberIdFilter) {
        List<Revenue> list = barberIdFilter != null
                ? revenueRepository.findByBarberIdOrderByDateDesc(barberIdFilter)
                : revenueRepository.findAllByOrderByDateDesc();
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResult<RevenueEntryDto> findPageOrderedByDateDesc(Long barberIdFilter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Revenue> pageResult = barberIdFilter != null
                ? revenueRepository.findByBarberIdOrderByDateDesc(barberIdFilter, pageable)
                : revenueRepository.findAllByOrderByDateDesc(pageable);
        List<RevenueEntryDto> content = pageResult.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageResult<>(content, pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.getNumber());
    }

    public static class PageResult<T> {
        private final List<T> content;
        private final long totalElements;
        private final int totalPages;
        private final int number;

        public PageResult(List<T> content, long totalElements, int totalPages, int number) {
            this.content = content;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.number = number;
        }

        public List<T> getContent() { return content; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getNumber() { return number; }
    }

    @Transactional(readOnly = true)
    public RevenueEntryDto getById(Long id) {
        Revenue rev = revenueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lançamento não encontrado: " + id));
        return toDto(rev);
    }

    public RevenueEntryDto update(Long id, Long barberId, BigDecimal value, LocalDate date) {
        Revenue rev = revenueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lançamento não encontrado: " + id));
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new IllegalArgumentException("Barbeiro não encontrado: " + barberId));
        rev.setBarber(barber);
        rev.setValue(value);
        rev.setDate(date);
        return toDto(revenueRepository.save(rev));
    }

    public void delete(Long id) {
        if (!revenueRepository.existsById(id)) {
            throw new IllegalArgumentException("Lançamento não encontrado: " + id);
        }
        revenueRepository.deleteById(id);
    }

    private RevenueEntryDto toDto(Revenue rev) {
        RevenueEntryDto dto = new RevenueEntryDto();
        dto.setId(rev.getId());
        dto.setBarberId(rev.getBarber().getId());
        dto.setBarberName(rev.getBarber().getName());
        dto.setValue(rev.getValue());
        dto.setDate(rev.getDate());
        return dto;
    }

    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> getCurrentWeekRevenuePerBarber() {
        LocalDate today = todayBrazil();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6); // Monday to Sunday (semana completa para incluir hoje quando for domingo)

        List<Revenue> revenues = revenueRepository.findByDateBetween(weekStart, weekEnd);
        Map<Long, BigDecimal> result = new HashMap<>();

        for (Revenue rev : revenues) {
            if (rev.getBarber() == null) continue;
            Long barberId = rev.getBarber().getId();
            BigDecimal current = result.getOrDefault(barberId, BigDecimal.ZERO);
            result.put(barberId, current.add(rev.getValue() != null ? rev.getValue() : BigDecimal.ZERO));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> getTodayRevenuePerBarber() {
        LocalDate today = todayBrazil();
        List<Revenue> revenues = revenueRepository.findByDateBetween(today, today);
        Map<Long, BigDecimal> result = new HashMap<>();
        for (Revenue rev : revenues) {
            if (rev.getBarber() == null) continue;
            Long barberId = rev.getBarber().getId();
            BigDecimal current = result.getOrDefault(barberId, BigDecimal.ZERO);
            result.put(barberId, current.add(rev.getValue() != null ? rev.getValue() : BigDecimal.ZERO));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> getCurrentMonthRevenuePerBarber() {
        LocalDate today = todayBrazil();
        YearMonth current = YearMonth.from(today);
        LocalDate monthStart = current.atDay(1);
        LocalDate monthEnd = current.atEndOfMonth();

        List<Revenue> revenues = revenueRepository.findByDateBetween(monthStart, monthEnd);
        Map<Long, BigDecimal> result = new HashMap<>();

        for (Revenue rev : revenues) {
            if (rev.getBarber() == null) continue;
            Long barberId = rev.getBarber().getId();
            BigDecimal currentSum = result.getOrDefault(barberId, BigDecimal.ZERO);
            result.put(barberId, currentSum.add(rev.getValue() != null ? rev.getValue() : BigDecimal.ZERO));
        }
        return result;
    }
}

