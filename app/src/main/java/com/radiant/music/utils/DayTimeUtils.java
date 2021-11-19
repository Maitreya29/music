package com.radiant.music.utils;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class DayTimeUtils {

    private static final int dayStart = 6; // 6AM
    private static final int dayEnd = 18; //6PM

    @NonNull
    public static DayTime getTimeOfDay() {
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= dayStart && hourOfDay < dayEnd)
            return DayTime.DAY;
        return DayTime.NIGHT;
    }

    public enum DayTime {
        DAY,
        NIGHT
    }
}