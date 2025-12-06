package com.example.demo;

import java.time.LocalDate;

public class TestDateProvider {
    private static LocalDate fixedDate = null;

    public static LocalDate now() {
        return fixedDate != null ? fixedDate : LocalDate.now();
    }

    public static void setFixedDate(LocalDate date) {
        fixedDate = date;
    }

    public static void reset() {
        fixedDate = null;
    }
}
