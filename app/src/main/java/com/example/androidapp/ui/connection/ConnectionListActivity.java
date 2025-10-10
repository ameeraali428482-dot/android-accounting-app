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
import java.util.ArrayList;
import java.util.List;

public class ConnectionListActivity extends AppCompatActivity {

    private RecyclerView connectionRecyclerView;
    private GenericAdapter<Connection> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        connectionRecyclerView = findViewById(R.id.connection_recycler_view);
        connectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GenericAdapter<Connection>(
            new ArrayList<>(),
            connection -> {
                Intent intent = new Intent(ConnectionListActivity.this, ConnectionDetailActivity.class);
                intent.putExtra("connection_id", connection.getId());
                startActivity(intent);
            }
        ) {
            @Override
            protected int getLayoutResId() {
                return R.layout.connection_list_row;
            }

            @Override
            protected void bindView(View itemView, Connection connection) {
                TextView connectionName = itemView.findViewById(R.id.connection_name);
                TextView connectionType = itemView.findViewById(R.id.connection_type);
                TextView connectionStatus = itemView.findViewById(R.id.connection_status);

                if (connectionName != null) connectionName.setText(connection.getName());
                if (connectionType != null) connectionType.setText("النوع: " + connection.getType());
                if (connectionStatus != null) connectionStatus.setText("الحالة: " + connection.getStatus());
            }
        };
        
        connectionRecyclerView.setAdapter(adapter);

        View addButton = findViewById(R.id.add_connection_button);
        if (addButton != null) {
            addButton.setOnClickListener(v -> {
                Intent intent = new Intent(ConnectionListActivity.this, ConnectionDetailActivity.class);
                startActivity(intent);
            });
        }

        loadConnections();
    }

    private void loadConnections() {
        String companyId = sessionManager.getCompanyId();
        if (companyId != null) {
            database.connectionDao().getConnectionsByCompanyId(companyId)
                .observe(this, connections -> {
                    if (connections != null) {
                        adapter.setData(connections);
                    }
                });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConnections();
    }
}
