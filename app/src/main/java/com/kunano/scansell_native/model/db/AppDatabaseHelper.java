package com.kunano.scansell_native.model.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Business.class}, version = 1)
public abstract class AppDatabaseHelper extends RoomDatabase {
    public abstract BusinessDao businessDao();
}