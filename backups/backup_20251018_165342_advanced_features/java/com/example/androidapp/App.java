package com.example.androidapp;

import android.app.Application;
import android.content.Intent;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.sync.SyncService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class App extends Application {
    private static App instance;
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = AppDatabase.getDatabase(this);

        // تفعيل التخزين المؤقت لـ Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        startService(new Intent(this, SyncService.class));
    }

    public static App getInstance() {
        return instance;
    }

    public static AppDatabase getDatabaseHelper() {
        return database;
    }
}
