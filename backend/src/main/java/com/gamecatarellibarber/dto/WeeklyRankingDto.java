package com.gamecatarellibarber.dto;

import java.math.BigDecimal;

public class WeeklyRankingDto {

    private int position;
    private Long barberId;
    private String barberName;
    private String photoUrl;
    private BigDecimal weeklyProgressPercent;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

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

    public BigDecimal getWeeklyProgressPercent() {
        return weeklyProgressPercent;
    }

    public void setWeeklyProgressPercent(BigDecimal weeklyProgressPercent) {
        this.weeklyProgressPercent = weeklyProgressPercent;
    }
}

