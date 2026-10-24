package com.aicte.service;

import com.aicte.util.TimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class TimeSimulationService {

    public LocalDateTime getCurrentTime() {
        return TimeService.now();
    }

    public LocalDateTime advanceDays(int days) {
        TimeService.advanceDays(days);
        log.info("Advanced simulated time by {} days. New simulated time: {}", days, TimeService.now());
        return TimeService.now();
    }

    public LocalDateTime setSimulatedDate(LocalDateTime date) {
        TimeService.setSimulatedDate(date);
        log.info("Set simulated time to: {}", date);
        return TimeService.now();
    }

    public LocalDateTime reset() {
        TimeService.reset();
        log.info("Reset simulated time to system clock: {}", TimeService.now());
        return TimeService.now();
    }
}
