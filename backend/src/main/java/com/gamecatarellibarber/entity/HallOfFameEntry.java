package com.gamecatarellibarber.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "hall_of_fame")
public class HallOfFameEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "barber_id")
    private Barber barber;

    @Column(name = "entry_month", nullable = false)
    private int month;

    @Column(name = "entry_year", nullable = false)
    private int year;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal performancePercent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Barber getBarber() {
        return barber;
    }

    public void setBarber(Barber barber) {
        this.barber = barber;
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

