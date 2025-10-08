package com.example.androidapp;

import android.app.Application;
import android.content.Intent;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.sync.SyncService;

public class App extends Application {

    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getDatabase(this);
        startService(new Intent(this, SyncService.class));
    }

    public static AppDatabase getDatabase() {
        return database;
    }
}
