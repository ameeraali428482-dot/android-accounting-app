#!/bin/bash

echo "🔧 بدء إصلاح جميع الملفات..."

# 1. إصلاح CompanySettingsActivity.java
cat > app/src/main/java/com/example/androidapp/ui/companysettings/CompanySettingsActivity.java << 'COMPANY_SETTINGS'
package com.example.androidapp.ui.companysettings;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.CompanySettings;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class CompanySettingsActivity extends AppCompatActivity {

    private EditText companyNameEditText, companyAddressEditText, companyPhoneEditText, companyEmailEditText;
    private Button saveButton;
    private SessionManager sessionManager;
    private String companyId;
    private String companySettingsId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadCompanySettings();
    }

    private void initViews() {
        companyNameEditText = findViewById(R.id.company_name_edit_text);
        companyAddressEditText = findViewById(R.id.company_address_edit_text);
        companyPhoneEditText = findViewById(R.id.company_phone_edit_text);
        companyEmailEditText = findViewById(R.id.company_email_edit_text);
        saveButton = findViewById(R.id.save_company_settings_button);

        saveButton.setOnClickListener(v -> saveCompanySettings());
    }

    private void loadCompanySettings() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            CompanySettings settings = AppDatabase.getDatabase(this).companySettingsDao().getCompanySettingsByCompanyId(companyId);
            runOnUiThread(() -> {
                if (settings != null) {
                    populateSettingsFields(settings);
                    companySettingsId = settings.getId();
                }
            });
        });
    }

    private void populateSettingsFields(CompanySettings settings) {
        if (settings != null) {
            companyNameEditText.setText(settings.getCompanyName());
            companyAddressEditText.setText(settings.getCompanyAddress());
            companyPhoneEditText.setText(settings.getCompanyPhone());
            companyEmailEditText.setText(settings.getCompanyEmail());
        }
    }

    private void saveCompanySettings() {
        String name = companyNameEditText.getText().toString().trim();
        String address = companyAddressEditText.getText().toString().trim();
        String phone = companyPhoneEditText.getText().toString().trim();
        String email = companyEmailEditText.getText().toString().trim();

        if (name.isEmpty()) {
            companyNameEditText.setError("اسم الشركة مطلوب");
            return;
        }

        CompanySettings settings = new CompanySettings(
            companySettingsId != null ? companySettingsId : UUID.randomUUID().toString(),
            companyId,
            name,
            address,
            phone,
            email
        );

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (companySettingsId == null) {
                AppDatabase.getDatabase(this).companySettingsDao().insert(settings);
            } else {
                AppDatabase.getDatabase(this).companySettingsDao().update(settings);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ إعدادات الشركة بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
COMPANY_SETTINGS

# 2. إصلاح GenericAdapter.java
cat > app/src/main/java/com/example/androidapp/ui/common/GenericAdapter.java << 'GENERIC_ADAPTER'
package com.example.androidapp.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter.GenericViewHolder> {

    private List<T> dataList;
    private OnItemClickListener<T> clickListener;

    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }

    public GenericAdapter(List<T> dataList, OnItemClickListener<T> clickListener) {
        this.dataList = dataList != null ? dataList : new ArrayList<>();
        this.clickListener = clickListener;
    }

    public void setData(List<T> newData) {
        this.dataList = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void updateData(List<T> newData) {
        this.dataList.clear();
        if (newData != null) {
            this.dataList.addAll(newData);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false);
        return new GenericViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        T item = dataList.get(position);
        bindView(holder.itemView, item);
        
        if (clickListener != null) {
            holder.itemView.setOnClickListener(v -> clickListener.onItemClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    protected abstract int getLayoutResId();
    protected abstract void bindView(View itemView, T item);

    public static class GenericViewHolder extends RecyclerView.ViewHolder {
        public GenericViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
GENERIC_ADAPTER

# 3. إصلاح ConnectionListActivity.java
cat > app/src/main/java/com/example/androidapp/ui/connection/ConnectionListActivity.java << 'CONNECTION_LIST'
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
CONNECTION_LIST

# 4. إصلاح ConnectionDetailActivity.java
cat > app/src/main/java/com/example/androidapp/ui/connection/ConnectionDetailActivity.java << 'CONNECTION_DETAIL'
package com.example.androidapp.ui.connection;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Connection;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class ConnectionDetailActivity extends AppCompatActivity {

    private EditText nameEditText, typeEditText, statusEditText;
    private Button saveButton, deleteButton;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String connectionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        nameEditText = findViewById(R.id.connection_name_edit_text);
        typeEditText = findViewById(R.id.connection_type_edit_text);
        statusEditText = findViewById(R.id.connection_status_edit_text);
        saveButton = findViewById(R.id.save_connection_button);
        deleteButton = findViewById(R.id.delete_connection_button);

        if (getIntent().hasExtra("connection_id")) {
            connectionId = getIntent().getStringExtra("connection_id");
            loadConnectionData(connectionId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> saveConnection());
        deleteButton.setOnClickListener(v -> deleteConnection());
    }

    private void loadConnectionData(String id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Connection connection = database.connectionDao().getConnectionById(id);
            runOnUiThread(() -> {
                if (connection != null) {
                    nameEditText.setText(connection.getName());
                    typeEditText.setText(connection.getType());
                    statusEditText.setText(connection.getStatus());
                } else {
                    Toast.makeText(this, "الاتصال غير موجود", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void saveConnection() {
        String name = nameEditText.getText().toString().trim();
        String type = typeEditText.getText().toString().trim();
        String status = statusEditText.getText().toString().trim();
        String companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || type.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        Connection connection;
        if (connectionId == null) {
            connection = new Connection(UUID.randomUUID().toString(), companyId, name, type, status);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                database.connectionDao().insert(connection);
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم إضافة الاتصال بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        } else {
            connection = new Connection(connectionId, companyId, name, type, status);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                database.connectionDao().update(connection);
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم تحديث الاتصال بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }

    private void deleteConnection() {
        if (connectionId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Connection connection = database.connectionDao().getConnectionById(connectionId);
                if (connection != null) {
                    database.connectionDao().delete(connection);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم حذف الاتصال بنجاح", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }
}
CONNECTION_DETAIL

# 5. إصلاح ChatListActivity.java
cat > app/src/main/java/com/example/androidapp/ui/chat/ChatListActivity.java << 'CHAT_LIST'
package com.example.androidapp.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private GenericAdapter<Chat> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GenericAdapter<Chat>(
            new ArrayList<>(),
            chat -> {
                Intent intent = new Intent(ChatListActivity.this, ChatDetailActivity.class);
                intent.putExtra("chat_id", chat.getId());
                intent.putExtra("other_user_id", 
                    chat.getSenderId().equals(sessionManager.getCurrentUserId()) ? 
                    chat.getReceiverId() : chat.getSenderId());
                startActivity(intent);
            }
        ) {
            @Override
            protected int getLayoutResId() {
                return R.layout.chat_list_row;
            }

            @Override
            protected void bindView(View itemView, Chat chat) {
                TextView chatMessage = itemView.findViewById(R.id.chat_message);
                TextView chatTimestamp = itemView.findViewById(R.id.chat_timestamp);
                
                if (chatMessage != null) chatMessage.setText(chat.getMessage());
                if (chatTimestamp != null) chatTimestamp.setText(chat.getCreatedAt().toString());
            }
        };
        
        chatRecyclerView.setAdapter(adapter);
        loadChats();
    }

    private void loadChats() {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            database.chatDao().getChatsByUserId(userId)
                .observe(this, chats -> {
                    if (chats != null) {
                        adapter.setData(chats);
                    }
                });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats();
    }
}
CHAT_LIST

# 6. إصلاح NotificationListActivity.java
cat > app/src/main/java/com/example/androidapp/ui/notification/NotificationListActivity.java << 'NOTIFICATION_LIST'
package com.example.androidapp.ui.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Notification;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotificationListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Notification> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadNotifications();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        setTitle("الإشعارات");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<Notification>(
            new ArrayList<>(),
            notification -> {
                Intent intent = new Intent(NotificationListActivity.this, NotificationDetailActivity.class);
                intent.putExtra("notification_id", notification.getId());
                startActivity(intent);
            }
        ) {
            @Override
            protected int getLayoutResId() {
                return R.layout.notification_list_row;
            }

            @Override
            protected void bindView(View itemView, Notification notification) {
                TextView tvTitle = itemView.findViewById(R.id.tv_notification_title);
                TextView tvMessage = itemView.findViewById(R.id.tv_notification_message);
                TextView tvTimestamp = itemView.findViewById(R.id.tv_notification_timestamp);
                TextView tvType = itemView.findViewById(R.id.tv_notification_type);

                if (tvTitle != null) tvTitle.setText(notification.getTitle());
                if (tvMessage != null) tvMessage.setText(notification.getMessage());
                if (tvTimestamp != null) tvTimestamp.setText(dateFormat.format(notification.getCreatedAt()));
                if (tvType != null) tvType.setText(notification.getType());

                if (!notification.isRead()) {
                    itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                } else {
                    itemView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
            }
        };
        
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        database.notificationDao().getAllNotifications()
                .observe(this, notifications -> {
                    if (notifications != null) {
                        adapter.setData(notifications);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}
NOTIFICATION_LIST

# 7. إصلاح PaymentViewModel.java
cat > app/src/main/java/com/example/androidapp/ui/payment/viewmodel/PaymentViewModel.java << 'PAYMENT_VIEWMODEL'
package com.example.androidapp.ui.payment.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.entities.Payment;
import java.util.List;

public class PaymentViewModel extends AndroidViewModel {
    private PaymentDao paymentDao;

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        paymentDao = AppDatabase.getDatabase(application).paymentDao();
    }

    public LiveData<List<Payment>> getAllPayments(String companyId) {
        return paymentDao.getAllPayments(companyId);
    }

    public LiveData<Payment> getPaymentById(String paymentId, String companyId) {
        return paymentDao.getPaymentById(paymentId, companyId);
    }

    public void insert(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.insert(payment);
        });
    }

    public void update(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.update(payment);
        });
    }

    public void delete(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.delete(payment);
        });
    }
}
PAYMENT_VIEWMODEL

echo "✅ تم إنشاء جميع الإصلاحات بنجاح!"
echo "📁 الملفات التي تم إصلاحها:"
echo "1. CompanySettingsActivity.java"
echo "2. GenericAdapter.java" 
echo "3. ConnectionListActivity.java"
echo "4. ConnectionDetailActivity.java"
echo "5. ChatListActivity.java"
echo "6. NotificationListActivity.java"
echo "7. PaymentViewModel.java"
echo ""
echo "🚀 يمكنك الآن تشغيل: ./gradlew build"
