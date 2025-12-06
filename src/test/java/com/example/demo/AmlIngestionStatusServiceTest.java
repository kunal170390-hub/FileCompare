package com.example.demo;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class AmlIngestionStatusServiceTest {

    @Test
    void testBeforePeriod_LastIngestLessThan365() {
        // Arrange
        LocalDate today = LocalDate.of(2025, 2, 1);
        TestDateProvider.setFixedDate(today);

        LocalDate lastIngest = today.minusDays(200);

        // Act
        var result = AmlIngestionStatusService.getStatus(today,lastIngest,false);

        // Assert
        assertEquals(AmlIngestionStatusService.AmlIngestionStatus.COMPLETED, result);
    }
    
    @Test
    void testBeforePeriod_LastIngestMoreThan365() {
        // Arrange
        LocalDate today = LocalDate.of(2025, 2, 1);
        TestDateProvider.setFixedDate(today);

        LocalDate lastIngest = today.minusDays(400);

        // Act
        var result = AmlIngestionStatusService.getStatus(today,lastIngest,false);

        // Assert
        assertEquals(AmlIngestionStatusService.AmlIngestionStatus.NOT_STARTED, result);
    }


    @Test
    void testDuringPeriod_NewIngestion_Completes() {
        LocalDate today = AmlIngestionStatusService.getLastMondayOfFebruary(2025);
        TestDateProvider.setFixedDate(today);

        LocalDate lastIngest = today.minusDays(500);

        var result = AmlIngestionStatusService.getStatus(today,lastIngest,true);

        assertEquals(AmlIngestionStatusService.AmlIngestionStatus.COMPLETED, result);
    }

    @Test
    void testDuringPeriod_NoNewIngestion_LastIngestMoreThan365() {
        LocalDate today = AmlIngestionStatusService.getLastMondayOfFebruary(2025);
        TestDateProvider.setFixedDate(today);

        LocalDate lastIngest = today.minusDays(500);

        var result = AmlIngestionStatusService.getStatus(today,lastIngest,false);

        assertEquals(AmlIngestionStatusService.AmlIngestionStatus.NOT_STARTED, result);
    }

    @Test
    void testAfterPeriod_LastIngestMoreThan365() {
        LocalDate today = LocalDate.of(2025, 3, 10);  // After February
        TestDateProvider.setFixedDate(today);

        LocalDate lastIngest = today.minusDays(500);

        var result = AmlIngestionStatusService.getStatus(today,lastIngest,false);

        assertEquals(AmlIngestionStatusService.AmlIngestionStatus.PAST_DUE, result);
    }

    @Test
    void testAfterPeriod_LastIngestLessThan365() {
        LocalDate today = LocalDate.of(2025, 3, 10);
        TestDateProvider.setFixedDate(today);

        LocalDate lastIngest = today.minusDays(200);

        var result = AmlIngestionStatusService.getStatus(today,lastIngest,false);

        assertEquals(AmlIngestionStatusService.AmlIngestionStatus.COMPLETED, result);
    }

    @Test
    void testBoundary_LastIngestExactly365() {
        LocalDate today = LocalDate.of(2025, 3, 10);
        TestDateProvider.setFixedDate(today);

        LocalDate lastIngest = today.minusDays(365);

        var result = AmlIngestionStatusService.getStatus(today,lastIngest,false);

        assertEquals(AmlIngestionStatusService.AmlIngestionStatus.PAST_DUE, result);
    }

    @Test
    void testNullLastIngestDate() {
        LocalDate today = LocalDate.of(2025, 3, 10);
        TestDateProvider.setFixedDate(today);

        var result = AmlIngestionStatusService.getStatus(today,null,false);

        assertEquals(AmlIngestionStatusService.AmlIngestionStatus.PAST_DUE, result);
    }

    @Test
    void testLeapYear_LastMondayOfFebruary() {
        LocalDate lastMonday = AmlIngestionStatusService.getLastMondayOfFebruary(2024);

        assertEquals(LocalDate.of(2024, 2, 26), lastMonday);
    }
}
