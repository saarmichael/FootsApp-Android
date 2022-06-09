package com.example.footsapp_android;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.footsapp_android.entities.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    public abstract ContactDao contactDao();
}
