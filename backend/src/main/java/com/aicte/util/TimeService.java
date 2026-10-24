package com.aicte.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

public class TimeService {

    private static final AtomicReference<LocalDateTime> simulatedTime = new AtomicReference<>(null);

    private TimeService() {}

    public static LocalDateTime now() {
        LocalDateTime sim = simulatedTime.get();
        return sim != null ? sim : LocalDateTime.now();
    }

    public static LocalDate today() {
        return now().toLocalDate();
    }

    public static void setSimulatedDate(LocalDateTime dateTime) {
        simulatedTime.set(dateTime);
    }

    public static void advanceDays(int days) {
        LocalDateTime current = now();
        simulatedTime.set(current.plusDays(days));
    }

    public static void reset() {
        simulatedTime.set(null);
    }

    public static boolean isSimulated() {
        return simulatedTime.get() != null;
    }
}
