package com.gamecatarellibarber.dto;

import java.math.BigDecimal;

public class WeeklyProgressDto {

    private Long barberId;
    private String barberName;
    private String photoUrl;

    // Always 100 for display
    private BigDecimal weeklyGoalPercent;

    private BigDecimal weeklyProgressPercent;
    private BigDecimal dailyGoalPercent;
    private BigDecimal remainingPercent;

    public Long getBarberId() {
        return barberId;
    }

    public void setBarberId(Long barberId) {
        this.barberId = barberId;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public BigDecimal getWeeklyGoalPercent() {
        return weeklyGoalPercent;
    }

    public void setWeeklyGoalPercent(BigDecimal weeklyGoalPercent) {
        this.weeklyGoalPercent = weeklyGoalPercent;
    }

    public BigDecimal getWeeklyProgressPercent() {
        return weeklyProgressPercent;
    }

    public void setWeeklyProgressPercent(BigDecimal weeklyProgressPercent) {
        this.weeklyProgressPercent = weeklyProgressPercent;
    }

    public BigDecimal getDailyGoalPercent() {
        return dailyGoalPercent;
    }

    public void setDailyGoalPercent(BigDecimal dailyGoalPercent) {
        this.dailyGoalPercent = dailyGoalPercent;
    }

    public BigDecimal getRemainingPercent() {
        return remainingPercent;
    }

    public void setRemainingPercent(BigDecimal remainingPercent) {
        this.remainingPercent = remainingPercent;
    }
}

