package com.gamecatarellibarber.service;

import com.gamecatarellibarber.dto.WeeklyProgressDto;
import com.gamecatarellibarber.dto.WeeklyRankingDto;
import com.gamecatarellibarber.entity.Barber;
import com.gamecatarellibarber.repository.BarberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal SIX = BigDecimal.valueOf(6);

    private final BarberRepository barberRepository;
    private final RevenueService revenueService;

    public DashboardService(BarberRepository barberRepository, RevenueService revenueService) {
        this.barberRepository = barberRepository;
        this.revenueService = revenueService;
    }

    public List<WeeklyProgressDto> getWeeklyProgress() {
        List<Barber> barbers = barberRepository.findByActiveTrueOrderByNameAsc();
        Map<Long, BigDecimal> weeklyRevenueByBarber = revenueService.getCurrentWeekRevenuePerBarber();
        Map<Long, BigDecimal> todayRevenueByBarber = revenueService.getTodayRevenuePerBarber();

        return barbers.stream()
                .map(barber -> buildWeeklyProgress(
                        barber,
                        weeklyRevenueByBarber.getOrDefault(barber.getId(), BigDecimal.ZERO),
                        todayRevenueByBarber.getOrDefault(barber.getId(), BigDecimal.ZERO)))
                .collect(Collectors.toList());
    }

    public List<WeeklyRankingDto> getWeeklyRanking() {
        List<WeeklyProgressDto> progressList = getWeeklyProgress();

        List<WeeklyRankingDto> ranking = progressList.stream()
                .sorted(Comparator.comparing(WeeklyProgressDto::getWeeklyProgressPercent).reversed())
                .map(p -> {
                    WeeklyRankingDto dto = new WeeklyRankingDto();
                    dto.setBarberId(p.getBarberId());
                    dto.setBarberName(p.getBarberName());
                    dto.setPhotoUrl(p.getPhotoUrl());
                    dto.setWeeklyProgressPercent(p.getWeeklyProgressPercent());
                    return dto;
                })
                .collect(Collectors.toList());

        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setPosition(i + 1);
        }

        return ranking;
    }

    public List<WeeklyProgressDto> getMonthlyProgress() {
        List<Barber> barbers = barberRepository.findByActiveTrueOrderByNameAsc();
        Map<Long, BigDecimal> monthlyRevenueByBarber = revenueService.getCurrentMonthRevenuePerBarber();

        return barbers.stream()
                .map(barber -> buildMonthlyProgress(barber, monthlyRevenueByBarber.getOrDefault(barber.getId(), BigDecimal.ZERO)))
                .collect(Collectors.toList());
    }

    public List<WeeklyRankingDto> getMonthlyRanking() {
        List<WeeklyProgressDto> progressList = getMonthlyProgress();

        List<WeeklyRankingDto> ranking = progressList.stream()
                .sorted(Comparator.comparing(WeeklyProgressDto::getWeeklyProgressPercent).reversed())
                .map(p -> {
                    WeeklyRankingDto dto = new WeeklyRankingDto();
                    dto.setBarberId(p.getBarberId());
                    dto.setBarberName(p.getBarberName());
                    dto.setPhotoUrl(p.getPhotoUrl());
                    dto.setWeeklyProgressPercent(p.getWeeklyProgressPercent());
                    return dto;
                })
                .collect(Collectors.toList());

        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setPosition(i + 1);
        }

        return ranking;
    }

    private WeeklyProgressDto buildMonthlyProgress(Barber barber, BigDecimal monthlyRevenue) {
        WeeklyProgressDto dto = new WeeklyProgressDto();
        dto.setBarberId(barber.getId());
        dto.setBarberName(barber.getName());
        dto.setPhotoUrl(barber.getPhotoUrl());
        dto.setWeeklyGoalPercent(ONE_HUNDRED);
        dto.setDailyGoalPercent(BigDecimal.ZERO); // não usado no mensal

        BigDecimal monthlyGoal = barber.getMonthlyGoal();
        if (monthlyGoal == null || monthlyGoal.compareTo(BigDecimal.ZERO) <= 0) {
            dto.setWeeklyProgressPercent(BigDecimal.ZERO);
            dto.setRemainingPercent(ONE_HUNDRED);
            return dto;
        }

        BigDecimal monthlyProgressPercent = monthlyRevenue
                .multiply(ONE_HUNDRED)
                .divide(monthlyGoal, 2, RoundingMode.HALF_UP);

        if (monthlyProgressPercent.compareTo(BigDecimal.ZERO) < 0) {
            monthlyProgressPercent = BigDecimal.ZERO;
        }

        dto.setWeeklyProgressPercent(monthlyProgressPercent);

        BigDecimal remaining = ONE_HUNDRED.subtract(monthlyProgressPercent);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }
        dto.setRemainingPercent(remaining);

        return dto;
    }

    private WeeklyProgressDto buildWeeklyProgress(Barber barber, BigDecimal weeklyRevenue, BigDecimal todayRevenue) {
        WeeklyProgressDto dto = new WeeklyProgressDto();
        dto.setBarberId(barber.getId());
        dto.setBarberName(barber.getName());
        dto.setPhotoUrl(barber.getPhotoUrl());
        dto.setWeeklyGoalPercent(ONE_HUNDRED);

        BigDecimal weeklyGoal = barber.getWeeklyGoal();
        if (weeklyGoal == null || weeklyGoal.compareTo(BigDecimal.ZERO) <= 0) {
            dto.setWeeklyProgressPercent(BigDecimal.ZERO);
            dto.setRemainingPercent(ONE_HUNDRED);
            dto.setDailyGoalPercent(BigDecimal.ZERO);
            return dto;
        }

        BigDecimal weeklyProgressPercent = weeklyRevenue
                .multiply(ONE_HUNDRED)
                .divide(weeklyGoal, 2, RoundingMode.HALF_UP);

        if (weeklyProgressPercent.compareTo(BigDecimal.ZERO) < 0) {
            weeklyProgressPercent = BigDecimal.ZERO;
        }

        dto.setWeeklyProgressPercent(weeklyProgressPercent);

        BigDecimal remaining = ONE_HUNDRED.subtract(weeklyProgressPercent);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }
        dto.setRemainingPercent(remaining);

        BigDecimal dailyGoalValue = weeklyGoal.divide(SIX, 2, RoundingMode.HALF_UP);
        BigDecimal dailyGoalPercent;
        if (dailyGoalValue.compareTo(BigDecimal.ZERO) <= 0) {
            dailyGoalPercent = BigDecimal.ZERO;
        } else {
            dailyGoalPercent = todayRevenue
                    .multiply(ONE_HUNDRED)
                    .divide(dailyGoalValue, 2, RoundingMode.HALF_UP);
            if (dailyGoalPercent.compareTo(BigDecimal.ZERO) < 0) {
                dailyGoalPercent = BigDecimal.ZERO;
            }
        }
        dto.setDailyGoalPercent(dailyGoalPercent);

        return dto;
    }
}

