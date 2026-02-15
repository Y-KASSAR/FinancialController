package com.example.financialcontroller.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class CategoryEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String color; // Hex code string
    public String iconName;

    public CategoryEntity(String name, String color, String iconName) {
        this.name = name;
        this.color = color;
        this.iconName = iconName;
    }
}