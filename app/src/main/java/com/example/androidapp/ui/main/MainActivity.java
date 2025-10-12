package com.example.androidapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.ui.account.AccountListActivity;
import com.example.androidapp.ui.auth.LoginActivity;
import com.example.androidapp.ui.campaign.CampaignListActivity;
import com.example.androidapp.ui.chat.ChatListActivity;
import com.example.androidapp.ui.companysettings.CompanySettingsActivity;
import com.example.androidapp.ui.connection.ConnectionListActivity;
import com.example.androidapp.ui.customer.CustomerListActivity;
import com.example.androidapp.ui.employee.EmployeeListActivity;
import com.example.androidapp.ui.invoice.InvoiceListActivity;
import com.example.androidapp.ui.item.ItemListActivity;
import com.example.androidapp.ui.journalentry.JournalEntryListActivity;
import com.example.androidapp.ui.notification.NotificationListActivity;
import com.example.androidapp.ui.order.OrderListActivity;
import com.example.androidapp.ui.payroll.PayrollListActivity;
import com.example.androidapp.ui.pointtransaction.PointTransactionListActivity;
import com.example.androidapp.ui.reminder.ReminderListActivity;
import com.example.androidapp.ui.repair.RepairListActivity;
import com.example.androidapp.ui.reports.ReportsActivity;
import com.example.androidapp.ui.reward.RewardListActivity;
import com.example.androidapp.ui.role.RoleListActivity;
import com.example.androidapp.ui.sharedlink.SharedLinkListActivity;
import com.example.androidapp.ui.supplier.SupplierListActivity;
import com.example.androidapp.ui.trophy.TrophyListActivity;
import com.example.androidapp.ui.userreward.UserRewardListActivity;
import com.example.androidapp.ui.voucher.VoucherListActivity;
import com.example.androidapp.utils.GoogleDriveService;
import com.example.androidapp.utils.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.example.androidapp.utils.AIManager;
import android.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.LayoutInflater;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;









public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private TextView welcomeTextView;
    private Button logoutButton;
    private Button itemsButton;
    private Button customersButton;
    private Button suppliersButton;
    private Button invoicesButton;
    private Button reportsButton;
    private Button googleDriveButton;
    private Button aiAnalysisButton;

    private GoogleDriveService googleDriveService;
    private AIManager aiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getApplicationContext());
        googleDriveService = new GoogleDriveService(this);
        aiManager = new AIManager();


        if (!sessionManager.isLoggedIn()) {
            logoutUser();
        } else {
            HashMap<String, String> userDetails = sessionManager.getUserDetails();
            String username = userDetails.get(SessionManager.KEY_USERNAME);
            welcomeTextView.setText("مرحباً بك، " + (username != null ? username : "مستخدم") + "!");
        }

        logoutButton.setOnClickListener(v -> logoutUser());

        itemsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ItemListActivity.class)));

        customersButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CustomerListActivity.class)));

        suppliersButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SupplierListActivity.class)));

        invoicesButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, InvoiceListActivity.class)));

        reportsButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ReportsActivity.class)));




















        googleDriveButton.setOnClickListener(v -> signInToGoogleDrive());
        aiAnalysisButton.setOnClickListener(v -> showAIAnalysisDialog());
    }

    private void logoutUser() {
        mAuth.signOut();
        sessionManager.logoutUser();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void signInToGoogleDrive() {
        if (!googleDriveService.isSignedIn()) {
            startActivityForResult(googleDriveService.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        } else {
            Toast.makeText(this, "تم تسجيل الدخول بالفعل إلى Google Drive", Toast.LENGTH_SHORT).show();
            performGoogleDriveBackup();
        }
    }

    private void showAIAnalysisDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_ai_analysis, null);
        builder.setView(dialogView);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ai_analysis_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        analysisTypeSpinner.setAdapter(adapter);

        AlertDialog dialog = builder.create();

        analyzeButton.setOnClickListener(v -> {
            String selectedAnalysisType = analysisTypeSpinner.getSelectedItem().toString();
            String financialDataJson = financialDataEditText.getText().toString();

            // Map Arabic analysis type to English key for Firebase Function
            String firebaseAnalysisType = "";
            switch (selectedAnalysisType) {
                case "توقع التدفق النقدي":
                    firebaseAnalysisType = "cash_flow_prediction";
                    break;
                case "تصنيف المصروفات":
                    firebaseAnalysisType = "expense_categorization";
                    break;
                case "كشف الاحتيال":
                    firebaseAnalysisType = "fraud_detection";
                    break;
                case "نقاط الصحة المالية":
                    firebaseAnalysisType = "financial_health_score";
                    break;
                case "توصيات الميزانية":
                    firebaseAnalysisType = "budget_recommendations";
                    break;
            }

            Map<String, Object> financialDataMap = new HashMap<>();
            if (!financialDataJson.isEmpty()) {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, Object>>() {}.getType();
                    financialDataMap = gson.fromJson(financialDataJson, type);
                } catch (Exception e) {
                    Toast.makeText(this, "صيغة JSON للبيانات المالية غير صالحة.", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "JSON parsing error", e);
                    return;
                }
            }

            dialog.dismiss();
            performAIAnalysis(firebaseAnalysisType, financialDataMap);
        });

        dialog.show();
    }

    private void performAIAnalysis(String analysisType, Map<String, Object> financialData) {
        String companyId = sessionManager.getCompanyId();
        if (companyId == null) {
            Toast.makeText(this, "معرف الشركة غير صالح للتحليل.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "جاري تحليل البيانات المالية بواسطة الذكاء الاصطناعي...", Toast.LENGTH_LONG).show();

        aiManager.analyzeFinancialData(companyId, analysisType, financialData, new AIManager.AIManagerCallback() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                Log.d("MainActivity", "AI Analysis successful: " + result);
                Toast.makeText(MainActivity.this, "تم التحليل بنجاح: " + result.get("analysisType"), Toast.LENGTH_LONG).show();
                showAIAnalysisResultDialog(result);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("MainActivity", "AI Analysis failed", e);
                Toast.makeText(MainActivity.this, "فشل التحليل: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == RESULT_OK && data != null) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                task.addOnSuccessListener(googleSignInAccount -> {
                    if (googleSignInAccount.getEmail() != null) {
                        googleDriveService.initializeDriveClient(googleSignInAccount.getEmail());
                        Toast.makeText(this, "تم تسجيل الدخول بنجاح إلى Google Drive", Toast.LENGTH_SHORT).show();
                        // Now you can perform backup or other Drive operations
                        // performGoogleDriveBackup();
                    } else {
                        Toast.makeText(this, "فشل تسجيل الدخول إلى Google Drive: لا يوجد بريد إلكتروني", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Log.e("MainActivity", "Google Sign-in failed", e);
                    Toast.makeText(this, "فشل تسجيل الدخول إلى Google Drive: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(this, "فشل تسجيل الدخول إلى Google Drive", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAIAnalysisResultDialog(Map<String, Object> result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_ai_analysis_result, null);
        builder.setView(dialogView);


        String analysisType = (String) result.get("analysisType");
        String analysisResult = (String) result.get("result");

        titleTextView.setText("نتيجة تحليل: " + analysisType);
        resultTextView.setText(analysisResult);

        AlertDialog dialog = builder.create();

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void performGoogleDriveBackup() {
        if (googleDriveService.getDriveService() == null) {
            Toast.makeText(this, "يرجى تسجيل الدخول إلى Google Drive أولاً.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "جاري عمل نسخة احتياطية إلى Google Drive...", Toast.LENGTH_LONG).show();

        // Get the database file path
        String dbPath = getApplicationContext().getDatabasePath("business_database").getAbsolutePath();
        String dbWalPath = dbPath + "-wal";
        String dbShmPath = dbPath + "-shm";

        // Upload main database file
        googleDriveService.uploadFile(dbPath, "application/x-sqlite3", "AccountingAppBackups", new GoogleDriveService.DriveServiceCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d("MainActivity", "Backup successful: " + message);
                Toast.makeText(MainActivity.this, "تم عمل نسخة احتياطية لقاعدة البيانات بنجاح.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("MainActivity", "Backup failed", e);
                Toast.makeText(MainActivity.this, "فشل عمل نسخة احتياطية لقاعدة البيانات: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Upload -wal file if it exists
        java.io.File dbWalFile = new java.io.File(dbWalPath);
        if (dbWalFile.exists()) {
            googleDriveService.uploadFile(dbWalPath, "application/x-sqlite3.wal", "AccountingAppBackups", new GoogleDriveService.DriveServiceCallback() {
                @Override
                public void onSuccess(String message) {
                    Log.d("MainActivity", "WAL file backup successful: " + message);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("MainActivity", "WAL file backup failed", e);
                }
            });
        }

        // Upload -shm file if it exists
        java.io.File dbShmFile = new java.io.File(dbShmPath);
        if (dbShmFile.exists()) {
            googleDriveService.uploadFile(dbShmPath, "application/x-sqlite3-shm", "AccountingAppBackups", new GoogleDriveService.DriveServiceCallback() {
                @Override
                public void onSuccess(String message) {
                    Log.d("MainActivity", "SHM file backup successful: " + message);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("MainActivity", "SHM file backup failed", e);
                }
            });
        }
    }
}

