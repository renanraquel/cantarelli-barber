package com.gamecatarellibarber.dto;

import java.math.BigDecimal;

public class HallOfFameDto {

    private Long id;
    private Long barberId;
    private String barberName;
    private String photoUrl;
    private int month;
    private int year;
    private BigDecimal performancePercent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getPerformancePercent() {
        return performancePercent;
    }

    public void setPerformancePercent(BigDecimal performancePercent) {
        this.performancePercent = performancePercent;
    }
}

