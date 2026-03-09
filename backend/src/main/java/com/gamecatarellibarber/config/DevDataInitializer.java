package com.gamecatarellibarber.config;

import com.gamecatarellibarber.entity.Barber;
import com.gamecatarellibarber.entity.HallOfFameEntry;
import com.gamecatarellibarber.entity.Revenue;
import com.gamecatarellibarber.repository.BarberRepository;
import com.gamecatarellibarber.repository.HallOfFameRepository;
import com.gamecatarellibarber.repository.RevenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@Profile("dev")
public class DevDataInitializer {

    @Bean
    public CommandLineRunner seedData(BarberRepository barberRepository,
                                      RevenueRepository revenueRepository,
                                      HallOfFameRepository hallOfFameRepository) {
        return args -> {
            if (barberRepository.count() > 0) {
                return;
            }

            Barber felipe = new Barber();
            felipe.setName("Felipe");
            felipe.setPhotoUrl("https://placehold.co/200x200?text=Felipe");
            felipe.setWeeklyGoal(new BigDecimal("3000"));
            felipe.setMonthlyGoal(new BigDecimal("12000"));
            felipe.setActive(true);

            Barber andre = new Barber();
            andre.setName("André");
            andre.setPhotoUrl("https://placehold.co/200x200?text=Andre");
            andre.setWeeklyGoal(new BigDecimal("2800"));
            andre.setMonthlyGoal(new BigDecimal("11000"));
            andre.setActive(true);

            Barber joao = new Barber();
            joao.setName("João");
            joao.setPhotoUrl("https://placehold.co/200x200?text=Joao");
            joao.setWeeklyGoal(new BigDecimal("2500"));
            joao.setMonthlyGoal(new BigDecimal("10000"));
            joao.setActive(true);

            barberRepository.saveAll(List.of(felipe, andre, joao));

            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.minus(5, ChronoUnit.DAYS);

            for (int i = 0; i < 6; i++) {
                LocalDate date = weekStart.plusDays(i);

                Revenue r1 = new Revenue();
                r1.setBarber(felipe);
                r1.setDate(date);
                r1.setValue(new BigDecimal("600").add(BigDecimal.valueOf(i * 20L)));

                Revenue r2 = new Revenue();
                r2.setBarber(andre);
                r2.setDate(date);
                r2.setValue(new BigDecimal("500").add(BigDecimal.valueOf(i * 15L)));

                Revenue r3 = new Revenue();
                r3.setBarber(joao);
                r3.setDate(date);
                r3.setValue(new BigDecimal("420").add(BigDecimal.valueOf(i * 10L)));

                revenueRepository.saveAll(List.of(r1, r2, r3));
            }

            HallOfFameEntry hof = new HallOfFameEntry();
            hof.setBarber(felipe);
            hof.setMonth(today.getMonthValue());
            hof.setYear(today.getYear());
            hof.setPerformancePercent(new BigDecimal("112.5"));
            hallOfFameRepository.save(hof);
        };
    }
}

