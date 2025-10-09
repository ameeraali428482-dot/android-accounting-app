package com.example.androidapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
// ✅ تصحيح: إضافة الاستيرادات المفقودة التي سببت الخطأ
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport; // تم الإبقاء على هذا على الرغم من أنه لم يكن في كودك الأصلي لأنه ضروري في بعض البنى
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.util.Collections;

public class GoogleDriveService {

    private static final String TAG = "GoogleDriveService";
    private GoogleSignInClient googleSignInClient;
    private Drive driveService;
    private Context context;

    public GoogleDriveService(Context context) {
        this.context = context;
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, signInOptions);
    }

    public Intent getSignInIntent() {
        return googleSignInClient.getSignInIntent();
    }

    public void initializeDriveClient(String accountName) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context,
                Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccountName(accountName);
        driveService = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("Android Accounting App")
                .build();
        Log.d(TAG, "Google Drive client initialized.");
    }

    public Drive getDriveService() {
        return driveService;
    }

    public void uploadFile(String filePath, String mimeType, String folderName, DriveServiceCallback callback) {
        if (driveService == null) {
            callback.onFailure(new IllegalStateException("Google Drive service not initialized."));
            return;
        }

        new Thread(() -> {
            try {
                File fileContent = new File(filePath);
                com.google.api.client.http.FileContent mediaContent = new com.google.api.client.http.FileContent(mimeType, fileContent);

                // Check if folder exists, if not, create it
                String folderId = getOrCreateFolderId(folderName);

                com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                fileMetadata.setName(fileContent.getName());
                fileMetadata.setParents(Collections.singletonList(folderId));

                com.google.api.services.drive.model.File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id,name")
                        .execute();

                callback.onSuccess(uploadedFile.getName() + " uploaded with ID: " + uploadedFile.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error uploading file to Google Drive", e);
                callback.onFailure(e);
            }
        }).start();
    }

    private String getOrCreateFolderId(String folderName) throws Exception {
        // Search for the folder
        Drive.Files.List request = driveService.files().list()
                .setQ("mimeType=\'application/vnd.google-apps.folder\' and name=\'" + folderName + "\'")
                .setSpaces("drive");
        List<com.google.api.services.drive.model.File> files = request.execute().getFiles();

        if (files != null && !files.isEmpty()) {
            return files.get(0).getId(); // Folder found
        } else {
            // Folder not found, create it
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(folderName);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            com.google.api.services.drive.model.File folder = driveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            return folder.getId();
        }
    }

    public interface DriveServiceCallback {
        void onSuccess(String message);
        void onFailure(Exception e);
    }

    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(context) != null;
    }

    public void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        driveService = null;
                        Log.d(TAG, "Signed out from Google Drive.");
                    } else {
                        Log.e(TAG, "Error signing out from Google Drive", task.getException());
                    }
                });
    }
}
