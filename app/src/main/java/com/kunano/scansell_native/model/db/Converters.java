package com.kunano.scansell_native.model.db;

import android.os.Build;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class Converters {
    @TypeConverter
    public static LocalDate fromTimestamp(Long value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return value == null ? null : Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    @TypeConverter
    public static Long dateToTimestamp(LocalDate date) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date == null ? null : date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
        }
        return null;
    }
}