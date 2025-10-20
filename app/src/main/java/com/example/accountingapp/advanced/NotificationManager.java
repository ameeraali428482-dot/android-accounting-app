package com.example.accountingapp.advanced;

import android.content.Context;
import android.content.Intent;
import com.example.accountingapp.BackupRestoreActivity;

public class NotificationManager {
    
    private Context context;

    public NotificationManager(Context context) {
        this.context = context;
    }

    public void showRestoreNotification() {
        Intent restoreIntent = new Intent(context, BackupRestoreActivity.class);
        restoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(restoreIntent);
    }

    public void showMergeNotification() {
        Intent mergeIntent = new Intent(context, BackupRestoreActivity.class);
        mergeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mergeIntent);
    }
}
