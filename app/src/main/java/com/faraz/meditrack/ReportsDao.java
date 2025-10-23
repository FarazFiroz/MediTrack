package com.faraz.meditrack;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReportsDao {
    @Insert
    void insert(Report report);

    @Delete
    void delete(Report report);

    @Query("SELECT * FROM reports ORDER BY id DESC")
    List<Report> getAllReports();
}
