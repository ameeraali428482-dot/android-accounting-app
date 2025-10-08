package com.example.androidapp.ui.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.ConnectionDao;
import com.example.androidapp.models.Connection;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

import java.util.List;

public class ConnectionListActivity extends AppCompatActivity {

    private RecyclerView connectionRecyclerView;
    private ConnectionDao connectionDao;
    private SessionManager sessionManager;
    private GenericAdapter<Connection> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_list);

        connectionRecyclerView = findViewById(R.id.connection_recycler_view);
        connectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        connectionDao = new ConnectionDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        findViewById(R.id.add_connection_button).setOnClickListener(v -> {
            Intent intent = new Intent(ConnectionListActivity.this, ConnectionDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConnections();
    }

    private void loadConnections() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            // Handle error: no company ID found
            return;
        }

        List<Connection> connections = connectionDao.getConnectionsByCompanyId(companyId);

        adapter = new GenericAdapter<Connection>(connections) {
            @Override
            protected int getLayoutResId() {
                return R.layout.connection_list_row;
            }

            @Override
            protected void bindView(View itemView, Connection connection) {
                TextView connectionName = itemView.findViewById(R.id.connection_name);
                TextView connectionType = itemView.findViewById(R.id.connection_type);
                TextView connectionStatus = itemView.findViewById(R.id.connection_status);

                connectionName.setText(connection.getName());
                connectionType.setText("النوع: " + connection.getType());
                connectionStatus.setText("الحالة: " + connection.getStatus());

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(ConnectionListActivity.this, ConnectionDetailActivity.class);
                    intent.putExtra("connection_id", connection.getId());
                    startActivity(intent);
                });
            }
        };
        connectionRecyclerView.setAdapter(adapter);
    }
}
