package com.zhaizq.aio.common.utils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public final static Date START_OF_THE_WORLD = Date.from(LocalDateTime.MIN.withYear(1970).atZone(ZoneId.systemDefault()).toInstant());
    public final static Date END_OF_THE_WORLD = Date.from(LocalDateTime.MAX.withYear(2099).withNano(0).atZone(ZoneId.systemDefault()).toInstant());

    public static Date startOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date endOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date addDays(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, offset);
        return calendar.getTime();
    }

    public static Date addMonths(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, offset);
        return calendar.getTime();
    }

    public static Date nextTime(Date basicDate, LocalTime time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(basicDate);

        if (basicDate.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().isAfter(time))
            calendar.add(Calendar.DATE, 1);

        calendar.set(Calendar.HOUR_OF_DAY, time.getHour());
        calendar.set(Calendar.MINUTE, time.getMinute());
        return calendar.getTime();
    }

    public static long diffDays(Date afterDate, Date beforeDate) {
        LocalDate before = Instant.ofEpochMilli(beforeDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate after = Instant.ofEpochMilli(afterDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        return after.toEpochDay() - before.toEpochDay();
    }

    public static int diffMonths(Date afterDate, Date beforeDate) {
        LocalDate before = Instant.ofEpochMilli(beforeDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate after = Instant.ofEpochMilli(afterDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        return (after.getYear() - before.getYear()) * 12 + after.getMonthValue() - before.getMonthValue() - (after.getDayOfMonth() < before.getDayOfMonth() ? 1 : 0);
    }

    public static Date min(Date... dates) {
        if (dates == null || dates.length == 0) return null;
        if (dates.length == 1) return dates[0];

        return Arrays.stream(dates).min(Date::compareTo).orElse(null);
    }

    public static Date max(Date... dates) {
        if (dates == null || dates.length == 0) return null;
        if (dates.length == 1) return dates[0];

        return Arrays.stream(dates).max(Date::compareTo).orElse(null);
    }

    public static String format(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }
}