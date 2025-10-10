package com.example.androidapp.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.ui.main.MainActivity;
import com.example.androidapp.utils.PermissionManager;

import java.util.concurrent.CompletableFuture;

/**
 * Base activity that provides permission checking functionality
 * All activities that require permission checks should extend this class
 */
public abstract class PermissionCheckActivity extends AppCompatActivity {
    
    protected PermissionManager permissionManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionManager = new PermissionManager(this);
    }
    
    /**
     * Override this method to specify required permissions for the activity
     * @return Array of required permission names
     */
    protected abstract String[] getRequiredPermissions();
    
    /**
     * Override this method to specify if ANY of the permissions is sufficient (OR logic)
     * Default is ALL permissions required (AND logic)
     * @return true if ANY permission is sufficient, false if ALL permissions required
     */
    protected boolean isAnyPermissionSufficient() {
        return false;
    }
    
    /**
     * Called when user has required permissions
     */
    protected abstract void onPermissionGranted();
    
    /**
     * Called when user doesn't have required permissions
     */
    protected void onPermissionDenied() {
        Toast.makeText(this, "ليس لديك الصلاحية للوصول إلى هذه الصفحة", Toast.LENGTH_LONG).show();
        finish();
    }
    
    /**
     * Check permissions and proceed accordingly
     */
    protected void checkPermissions() {
        String[] requiredPermissions = getRequiredPermissions();
        
        if (requiredPermissions == null || requiredPermissions.length == 0) {
            onPermissionGranted();
            return;
        }
        
        CompletableFuture<Boolean> permissionCheck;
        
        if (isAnyPermissionSufficient()) {
            permissionCheck = permissionManager.hasAnyPermission(requiredPermissions);
        } else {
            permissionCheck = permissionManager.hasAllPermissions(requiredPermissions);
        }
        
        permissionCheck.thenAccept(hasPermission -> {
            runOnUiThread(() -> {
                if (hasPermission) {
                    onPermissionGranted();
                } else {
                    onPermissionDenied();
                }
            });
        });
    }
    
    /**
     * Check if user has a specific permission
     */
    protected void checkPermission(String permission, PermissionCallback callback) {
        permissionManager.hasPermission(permission).thenAccept(hasPermission -> {
            runOnUiThread(() -> callback.onResult(hasPermission));
        });
    }
    
    /**
     * Check if user has any of the specified permissions
     */
    protected void checkAnyPermission(String[] permissions, PermissionCallback callback) {
        permissionManager.hasAnyPermission(permissions).thenAccept(hasPermission -> {
            runOnUiThread(() -> callback.onResult(hasPermission));
        });
    }
    
    /**
     * Check if user has all of the specified permissions
     */
    protected void checkAllPermissions(String[] permissions, PermissionCallback callback) {
        permissionManager.hasAllPermissions(permissions).thenAccept(hasPermission -> {
            runOnUiThread(() -> callback.onResult(hasPermission));
        });
    }
    
    /**
     * Show or hide a view based on permission
     */
    protected void showViewIfHasPermission(View view, String permission) {
        checkPermission(permission, hasPermission -> {
            view.setVisibility(hasPermission ? View.VISIBLE : View.GONE);
        });
    }
    
    /**
     * Enable or disable a view based on permission
     */
    protected void enableViewIfHasPermission(View view, String permission) {
        checkPermission(permission, hasPermission -> {
            view.setEnabled(hasPermission);
            view.setAlpha(hasPermission ? 1.0f : 0.5f);
        });
    }
    
    /**
     * Execute an action only if user has permission
     */
    protected void executeIfHasPermission(String permission, Runnable action) {
        checkPermission(permission, hasPermission -> {
            if (hasPermission) {
                action.run();
            } else {
                Toast.makeText(this, "ليس لديك الصلاحية لتنفيذ هذا الإجراء", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Execute an action only if user has permission, with custom denied message
     */
    protected void executeIfHasPermission(String permission, Runnable action, String deniedMessage) {
        checkPermission(permission, hasPermission -> {
            if (hasPermission) {
                action.run();
            } else {
                Toast.makeText(this, deniedMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Navigate to activity only if user has permission
     */
    protected void navigateIfHasPermission(String permission, Class<?> targetActivity) {
        checkPermission(permission, hasPermission -> {
            if (hasPermission) {
                Intent intent = new Intent(this, targetActivity);
                startActivity(intent);
            } else {
                Toast.makeText(this, "ليس لديك الصلاحية للوصول إلى هذه الصفحة", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Check if current user is super admin
     */
    protected void checkIfSuperAdmin(PermissionCallback callback) {
        CompletableFuture.supplyAsync(() -> permissionManager.isSuperAdmin())
                .thenAccept(isSuperAdmin -> runOnUiThread(() -> callback.onResult(isSuperAdmin)));
    }
    
    /**
     * Show admin-only views
     */
    protected void showAdminViews(View... views) {
        checkIfSuperAdmin(isSuperAdmin -> {
            for (View view : views) {
                view.setVisibility(isSuperAdmin ? View.VISIBLE : View.GONE);
            }
        });
    }
    
    /**
     * Callback interface for permission checks
     */
    public interface PermissionCallback {
        void onResult(boolean hasPermission);
    }
}
