package com.faraz.meditrack;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = Report.class, version = 1)
public abstract class ReportDatabase extends RoomDatabase {
    public abstract ReportsDao reportsDao();
}
