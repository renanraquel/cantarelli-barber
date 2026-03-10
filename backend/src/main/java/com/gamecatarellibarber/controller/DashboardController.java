package com.gamecatarellibarber.controller;

import com.gamecatarellibarber.dto.WeeklyProgressDto;
import com.gamecatarellibarber.dto.WeeklyRankingDto;
import com.gamecatarellibarber.service.DashboardService;
import com.gamecatarellibarber.service.RevenueService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/progress")
    public List<WeeklyProgressDto> getWeeklyProgress() {
        return dashboardService.getWeeklyProgress();
    }

    @GetMapping("/ranking/week")
    public List<WeeklyRankingDto> getWeeklyRanking() {
        return dashboardService.getWeeklyRanking();
    }

    @GetMapping("/dashboard/progress/month")
    public List<WeeklyProgressDto> getMonthlyProgress() {
        return dashboardService.getMonthlyProgress();
    }

    @GetMapping("/ranking/month")
    public List<WeeklyRankingDto> getMonthlyRanking() {
        return dashboardService.getMonthlyRanking();
    }

    @GetMapping("/ranking/week/available-weeks")
    public List<RevenueService.WeekInfo> getAvailableWeeks(@RequestParam(defaultValue = "8") int limit) {
        return dashboardService.getAvailableWeeks(Math.min(limit, 20));
    }

    @GetMapping("/ranking/week/history")
    public List<WeeklyRankingDto> getWeeklyRankingHistory(@RequestParam String weekStart) {
        LocalDate date = LocalDate.parse(weekStart);
        return dashboardService.getWeeklyRankingForWeek(date);
    }
}

