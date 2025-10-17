package com.example.androidapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.ui.auth.LoginActivity;
import com.example.androidapp.ui.item.ItemListActivity;
import com.example.androidapp.ui.customer.CustomerListActivity;
import com.example.androidapp.ui.supplier.SupplierListActivity;
import com.example.androidapp.ui.invoice.InvoiceListActivity;
import com.example.androidapp.ui.reports.ReportsActivity;
import com.example.androidapp.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private TextView welcomeTextView;
    private Button logoutButton;
    private Button itemsButton;
    private Button customersButton;
    private Button suppliersButton;
    private Button invoicesButton;
    private Button reportsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(getApplicationContext());

        welcomeTextView = findViewById(R.id.welcomeTextView);
        logoutButton = findViewById(R.id.logoutButton);
        itemsButton = findViewById(R.id.itemsButton);
        customersButton = findViewById(R.id.customersButton);
        suppliersButton = findViewById(R.id.suppliersButton);
        invoicesButton = findViewById(R.id.invoicesButton);
        reportsButton = findViewById(R.id.reportsButton);

        if (!sessionManager.isLoggedIn()) {
            logoutUser();
        } else {
            HashMap<String, String> userDetails = sessionManager.getUserDetails();
            String username = userDetails.get(SessionManager.KEY_USER_ID);
            welcomeTextView.setText("مرحباً بك، " + (username != null ? username : "مستخدم") + "!");
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        itemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ItemListActivity.class));
            }
        });

        customersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CustomerListActivity.class));
            }
        });

        suppliersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SupplierListActivity.class));
            }
        });

        invoicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InvoiceListActivity.class));
            }
        });

        reportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReportsActivity.class));
            }
        });
    }

    private void logoutUser() {
        mAuth.signOut();
        sessionManager.logoutUser();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
