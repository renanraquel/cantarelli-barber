package com.gamecatarellibarber.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "barbers")
public class Barber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String photoUrl;

    @Lob
    @Column(name = "photo_data")
    private byte[] photoData;

    @Column(name = "photo_content_type", length = 100)
    private String photoContentType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal weeklyGoal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyGoal;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public byte[] getPhotoData() {
        return photoData;
    }

    public void setPhotoData(byte[] photoData) {
        this.photoData = photoData;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public BigDecimal getWeeklyGoal() {
        return weeklyGoal;
    }

    public void setWeeklyGoal(BigDecimal weeklyGoal) {
        this.weeklyGoal = weeklyGoal;
    }

    public BigDecimal getMonthlyGoal() {
        return monthlyGoal;
    }

    public void setMonthlyGoal(BigDecimal monthlyGoal) {
        this.monthlyGoal = monthlyGoal;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

