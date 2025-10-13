package com.example.androidapp.ui.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Connection;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.util.ArrayList;

public class ConnectionListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Connection> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupRecyclerView();
        loadConnections();

        Button addConnectionButton = findViewById(R.id.addConnectionButton);
        if (addConnectionButton != null) {
            addConnectionButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, ConnectionDetailActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupRecyclerView() {
        adapter = new GenericAdapter<Connection>(new ArrayList<>(), connection -> {
            Intent intent = new Intent(ConnectionListActivity.this, ConnectionDetailActivity.class);
            intent.putExtra("connection_id", connection.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.connection_list_row;
            }

            @Override
            protected void bindView(View itemView, Connection connection) {
                TextView connectionNameDisplay = itemView.findViewById(R.id.connectionNameDisplay);
                TextView connectionTypeDisplay = itemView.findViewById(R.id.connectionTypeDisplay);
                TextView connectionStatusDisplay = itemView.findViewById(R.id.connectionStatusDisplay);

                if (connectionNameDisplay != null) connectionNameDisplay.setText(connection.getName());
                if (connectionTypeDisplay != null) connectionTypeDisplay.setText("النوع: " + connection.getType());
                if (connectionStatusDisplay != null) connectionStatusDisplay.setText("الحالة: " + connection.getStatus());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadConnections() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId != null) {
            database.connectionDao().getAllConnections(companyId).observe(this, connections -> {
                if (connections != null) {
                    adapter.updateData(connections);
                }
            });
        }
    }
}
