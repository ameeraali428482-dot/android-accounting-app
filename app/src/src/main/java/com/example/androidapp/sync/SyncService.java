package com.example.androidapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Company;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class SyncService extends Service {

    private static final String TAG = "SyncService";
    private FirebaseFirestore db;
    private AppDatabase appDatabase;
    private List<ListenerRegistration> listeners;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        listeners = new ArrayList<>();
        Log.d(TAG, "SyncService created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SyncService started");
        startListeningForCompanyChanges();
        startListeningForRepairChanges();
        startListeningForOrderChanges();
        startListeningForNotificationChanges();
        // Start other listeners for other entities here
        return START_STICKY;
    }

    private void startListeningForCompanyChanges() {
        ListenerRegistration companyListener = db.collection("companies")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Company company = dc.getDocument().toObject(Company.class);
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New company: " + company.getName());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.companyDao().insert(company);
                                });
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified company: " + company.getName());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.companyDao().update(company);
                                });
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed company: " + company.getName());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.companyDao().delete(company);
                                });
                                break;
                        }
                    }
                });
        listeners.add(companyListener);
    }

    private void startListeningForRepairChanges() {
        ListenerRegistration repairListener = db.collection("repairs")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Repair repair = dc.getDocument().toObject(Repair.class);
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New repair: " + repair.getTitle());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.repairDao().insert(repair);
                                });
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified repair: " + repair.getTitle());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.repairDao().update(repair);
                                });
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed repair: " + repair.getTitle());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.repairDao().delete(repair);
                                });
                                break;
                        }
                    }
                });
        listeners.add(repairListener);
    }

    private void startListeningForOrderChanges() {
        ListenerRegistration orderListener = db.collection("orders")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Order order = dc.getDocument().toObject(Order.class);
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New order: " + order.getId());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.orderDao().insert(order);
                                });
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified order: " + order.getId());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.orderDao().update(order);
                                });
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed order: " + order.getId());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.orderDao().delete(order);
                                });
                                break;
                        }
                    }
                });
        listeners.add(orderListener);
    }

    private void startListeningForNotificationChanges() {
        ListenerRegistration notificationListener = db.collection("notifications")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Notification notification = dc.getDocument().toObject(Notification.class);
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New notification: " + notification.getTitle());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.notificationDao().insert(notification);
                                });
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified notification: " + notification.getTitle());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.notificationDao().update(notification);
                                });
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed notification: " + notification.getTitle());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.notificationDao().delete(notification);
                                });
                                break;
                        }
                    }
                });
        listeners.add(notificationListener);
    }

    private void startListeningForTrophyChanges() {
        ListenerRegistration trophyListener = db.collection("trophies")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Trophy trophy = dc.getDocument().toObject(Trophy.class);
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "New trophy: " + trophy.getName());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.trophyDao().insert(trophy);
                                });
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified trophy: " + trophy.getName());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.trophyDao().update(trophy);
                                });
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed trophy: " + trophy.getName());
                                AppDatabase.databaseWriteExecutor.execute(() -> {
                                    appDatabase.trophyDao().delete(trophy);
                                });
                                break;
                        }
                    }
                });
        listeners.add(trophyListener);
    }

    // Add similar methods for other entities (e.g., Users, Items, Invoices, etc.)
    // private void startListeningForUserChanges() { ... }
    // private void startListeningForItemChanges() { ... }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (ListenerRegistration listener : listeners) {
            listener.remove();
        }
        Log.d(TAG, "SyncService destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
