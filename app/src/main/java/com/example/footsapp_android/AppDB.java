package com.example.footsapp_android;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.footsapp_android.entities.Contact;
import com.example.footsapp_android.entities.Message;

@Database(entities = {Contact.class, Message.class}, version = 5)
public abstract class AppDB extends RoomDatabase {

    private static volatile AppDB INSTANCE;

    public abstract ContactDao contactDao();

    public abstract MessageDao messageDao();

    public static AppDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDB.class, "fooDB")
                            .allowMainThreadQueries().
                            fallbackToDestructiveMigration().
                            build();
                }
            }
        }
        return INSTANCE;
    }
}
