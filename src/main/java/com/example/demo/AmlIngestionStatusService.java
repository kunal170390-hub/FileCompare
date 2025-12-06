package com.example.demo;

import java.time.*;
import java.time.temporal.*;

public class AmlIngestionStatusService {

    public enum AmlIngestionStatus {
        NOT_STARTED,
        COMPLETED,
        PAST_DUE
    }

    /**
     * Returns AML Model Ingestion Status based on:
     * - lastIngestedDate
     * - today's date
     * - ingestion period (starting last Monday of February)
     */
    public static AmlIngestionStatus getStatus(LocalDate today, LocalDate lastIngestedDate, boolean newIngestionOccurred) {

        //LocalDate today = LocalDate.now();
        LocalDate ingestionPeriodStart = getLastMondayOfFebruary(today.getYear());

        long daysSinceLastIngest = 
            lastIngestedDate == null ? 
            Long.MAX_VALUE : 
            ChronoUnit.DAYS.between(lastIngestedDate, today);

        boolean beforePeriod = today.isBefore(ingestionPeriodStart);
        boolean duringPeriod = today.isEqual(ingestionPeriodStart) || today.isAfter(ingestionPeriodStart);
        // Assuming ingestion period ends after February (can adjust)
        boolean afterPeriod = today.getMonthValue() > Month.FEBRUARY.getValue();

        // -------------------------
        // BEFORE INGESTION PERIOD
        // -------------------------
        if (beforePeriod) {
            if (daysSinceLastIngest < 365) {
                return AmlIngestionStatus.COMPLETED;
            } else {
                return AmlIngestionStatus.NOT_STARTED;
            }
        }

        // -------------------------
        // DURING INGESTION PERIOD
        // -------------------------
        if (duringPeriod && !afterPeriod) {
            if (newIngestionOccurred) {
                return AmlIngestionStatus.COMPLETED;
            }
        if (daysSinceLastIngest >= 365) {
                return AmlIngestionStatus.NOT_STARTED;
            }
            return AmlIngestionStatus.COMPLETED;
        }

        // -------------------------
        // AFTER INGESTION PERIOD
        // -------------------------
        if (afterPeriod) {
            if (daysSinceLastIngest >= 365) {
                return AmlIngestionStatus.PAST_DUE;
            } else {
                return AmlIngestionStatus.COMPLETED;
            }
        }

        // Default fallback — should never occur
        return AmlIngestionStatus.NOT_STARTED;
    }

    /**
     * Computes the last Monday of February for a given year.
     */
    public static LocalDate getLastMondayOfFebruary(int year) {
        LocalDate lastDayOfFeb = LocalDate.of(year, 2, 28);

        // If leap year adjust to Feb 29
        if (Year.of(year).isLeap()) {
            lastDayOfFeb = LocalDate.of(year, 2, 29);
        }

        // Go backward to the last Monday
        return lastDayOfFeb.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }


    // Example usage:
    public static void main(String[] args) {
    	LocalDate today = LocalDate.now();
        //LocalDate lastIngest = LocalDate.now().minusDays(400);
        LocalDate lastIngest = LocalDate.now().plusDays(150);
        System.out.println("lastIngest = " + lastIngest);
        AmlIngestionStatus status = getStatus(today, lastIngest, false);
        System.out.println("AML Status = " + status);
    }
}