package com.faraz.meditrack;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reports")
public class Report {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String imageUri;
    public String date;

    Report(String title, String imageUri, String date){
        this.title = title;
        this.imageUri = imageUri;
        this.date = date;
    }
}
