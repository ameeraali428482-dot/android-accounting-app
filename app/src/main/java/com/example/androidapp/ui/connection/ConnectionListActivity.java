package com.example.androidapp.ui.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Connection;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ConnectionListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Connection> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private FloatingActionButton fabAddConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddConnection = findViewById(R.id.fabAddConnection);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddConnection.setOnClickListener(v -> {
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
                TextView tvConnectionName = itemView.findViewById(R.id.tvConnectionName);
                TextView tvConnectionType = itemView.findViewById(R.id.tvConnectionType);
                TextView tvConnectionStatus = itemView.findViewById(R.id.tvConnectionStatus);

                tvConnectionName.setText(connection.getConnectionName());
                tvConnectionType.setText(connection.getConnectionType());
                tvConnectionStatus.setText(connection.getStatus());
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
