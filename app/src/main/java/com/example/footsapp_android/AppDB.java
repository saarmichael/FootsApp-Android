package com.example.footsapp_android;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    public abstract ContactDao contactDao();
}
