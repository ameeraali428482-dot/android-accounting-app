package com.example.androidapp;

import android.app.Application;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.DatabaseHelper;

public class App extends Application {
    private static App instance;
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        databaseHelper = new DatabaseHelper(this);
    }

    public static App getInstance() {
        return instance;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public static AppDatabase getDatabase() {
        return AppDatabase.getDatabase(instance);
    }
}
