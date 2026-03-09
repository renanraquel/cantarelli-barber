package com.gamecatarellibarber.repository;

import com.gamecatarellibarber.entity.HallOfFameEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HallOfFameRepository extends JpaRepository<HallOfFameEntry, Long> {

    /** Ordenação cronológica: ano e mês ascendente (jan → dez, mais antigo → mais recente). */
    List<HallOfFameEntry> findAllByOrderByYearAscMonthAsc();

    Optional<HallOfFameEntry> findFirstByOrderByYearDescMonthDesc();
}

