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
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.addButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ConnectionListActivity.this, ConnectionDetailActivity.class);
            startActivity(intent);
        });

        loadConnections();
    }

    private void loadConnections() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<Connection>() {
            @Override
            public void onItemClick(Connection item) {
                Intent intent = new Intent(ConnectionListActivity.this, ConnectionDetailActivity.class);
                intent.putExtra("connection_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.connection_list_row;
            }

            @Override
            protected void bindView(View itemView, Connection connection) {
                TextView connectionName = itemView.findViewById(R.id.connectionName);
                TextView connectionType = itemView.findViewById(R.id.connectionType);
                TextView connectionStatus = itemView.findViewById(R.id.connectionStatus);

                connectionName.setText(connection.getConnectionName());
                connectionType.setText(connection.getConnectionType());
                connectionStatus.setText(connection.getConnectionStatus());
            }
        };

        recyclerView.setAdapter(adapter);

        database.connectionDao().getAllConnections(companyId).observe(this, connections -> {
            if (connections != null) {
                adapter.updateData(connections);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConnections();
    }
}
