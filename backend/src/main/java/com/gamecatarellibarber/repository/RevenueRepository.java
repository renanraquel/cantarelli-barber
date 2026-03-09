package com.gamecatarellibarber.repository;

import com.gamecatarellibarber.entity.Barber;
import com.gamecatarellibarber.entity.Revenue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {

    List<Revenue> findByBarberAndDateBetween(Barber barber, LocalDate start, LocalDate end);

    List<Revenue> findByDateBetween(LocalDate start, LocalDate end);

    List<Revenue> findAllByOrderByDateDesc();

    List<Revenue> findByBarberIdOrderByDateDesc(Long barberId);

    Page<Revenue> findAllByOrderByDateDesc(Pageable pageable);

    Page<Revenue> findByBarberIdOrderByDateDesc(Long barberId, Pageable pageable);
}

