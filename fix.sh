#!/bin/bash

# --- Create new files ---

# Create NewChatActivity.java
cat > app/src/main/java/com/example/androidapp/ui/chat/NewChatActivity.java << 'EOF'
package com.example.androidapp.ui.chat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;

public class NewChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        setTitle("محادثة جديدة");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
EOF

# Create activity_new_chat.xml
cat > app/src/main/res/layout/activity_new_chat.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".ui.chat.NewChatActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="ابدأ محادثة جديدة"
        android:textSize="24sp"
        android:textStyle="bold"
        android:layout_gravity="center_horizontal"
        android:layout_marginBottom="16dp"/>

    <EditText
        android:id="@+id/et_recipient_id"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="أدخل معرف المستلم"
        android:inputType="text"
        android:layout_marginBottom="16dp"/>

    <Button
        android:id="@+id/btn_start_chat"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="بدء المحادثة"
        android:layout_gravity="center_horizontal"/>

</LinearLayout>
EOF

# --- Modify existing files ---

# Modify Order.java
cat > app/src/main/java/com/example/androidapp/data/entities/Order.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "orders",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                        parentColumns = "id",
                        childColumns = "companyId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Customer.class,
                        parentColumns = "id",
                        childColumns = "customerId",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "customerId")})
public class Order {
    @PrimaryKey
    private @NonNull String id;
    private @NonNull String companyId;
    private String customerId;
    private @NonNull Date orderDate;
    private double totalAmount;
    private String status; // e.g., "Processing", "Completed", "Cancelled"
    private String notes;

    public Order(@NonNull String id, @NonNull String companyId, String customerId, @NonNull Date orderDate, double totalAmount, String status, String notes) {
        this.id = id;
        this.companyId = companyId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.notes = notes;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public String getCustomerId() { return customerId; }
    @NonNull
    public Date getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setOrderDate(@NonNull Date orderDate) { this.orderDate = orderDate; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
}
EOF

# Modify SessionManager.java
cat > app/src/main/java/com/example/androidapp/utils/SessionManager.java << 'EOF'
package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    private static final String PREF_NAME = "AndroidAppPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_CURRENT_ORG_ID = "currentOrgId";
    public static final String KEY_COMPANY_ID = KEY_CURRENT_ORG_ID; // Alias for KEY_CURRENT_ORG_ID

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String userId, String currentOrgId) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_CURRENT_ORG_ID, currentOrgId);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_CURRENT_ORG_ID, pref.getString(KEY_CURRENT_ORG_ID, null));
        return user;
    }

    public String getCurrentUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getCurrentCompanyId() {
        return pref.getString(KEY_CURRENT_ORG_ID, null);
    }

    public String getCompanyId() {
        return getCurrentCompanyId(); // Alias for getCurrentCompanyId()
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}
EOF

# Modify JournalEntryAdapter.java
sed -i 's/tvDate.setText(journalEntry.getDate());/tvDate.setText(journalEntry.getEntryDate());/' app/src/main/java/com/example/androidapp/ui/journalentry/JournalEntryAdapter.java

# Modify JournalEntryDetailActivity.java
sed -i 's/etDate.setText(journalEntry.getDate());/etDate.setText(journalEntry.getEntryDate());/' app/src/main/java/com/example/androidapp/ui/journalentry/JournalEntryDetailActivity.java

# Modify CompanySettingsActivity.java
sed -i 's/companySettingsDao = new CompanySettingsDao(App.getDatabaseHelper());/companySettingsDao = App.getDatabaseHelper().companySettingsDao();/' app/src/main/java/com/example/androidapp/ui/companysettings/CompanySettingsActivity.java
sed -i 's/String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);/String companyId = sessionManager.getCompanyId();/' app/src/main/java/com/example/androidapp/ui/companysettings/CompanySettingsActivity.java
sed -i 's/companyNameEditText.setText(settings.getCompanyName());/if (settings != null) { companyNameEditText.setText(settings.getCompanyName()); }/' app/src/main/java/com/example/androidapp/ui/companysettings/CompanySettingsActivity.java
sed -i 's/companyAddressEditText.setText(settings.getCompanyAddress());/if (settings != null) { companyAddressEditText.setText(settings.getCompanyAddress()); }/' app/src/main/java/com/example/androidapp/ui/companysettings/CompanySettingsActivity.java
sed -i 's/companyPhoneEditText.setText(settings.getCompanyPhone());/if (settings != null) { companyPhoneEditText.setText(settings.getCompanyPhone()); }/' app/src/main/java/com/example/androidapp/ui/companysettings/CompanySettingsActivity.java
sed -i 's/companyEmailEditText.setText(settings.getCompanyEmail());/if (settings != null) { companyEmailEditText.setText(settings.getCompanyEmail()); }/' app/src/main/java/com/example/androidapp/ui/companysettings/CompanySettingsActivity.java
sed -i 's/settings = new CompanySettings(UUID.randomUUID().toString(), companyId, companyName, companyAddress, companyPhone, companyEmail);/settings = new CompanySettings(UUID.randomUUID().toString(), companyId, companyName, companyAddress, companyPhone, companyEmail);/' app/src/main/java/com/example/androidapp/ui/companysettings/CompanySettingsActivity.java
sed -i 's/settings = new CompanySettings(companySettingsId, companyId, companyName, companyAddress, companyPhone, companyEmail);/settings = new CompanySettings(companySettingsId, companyId, companyName, companyAddress, companyPhone, companyEmail);/' app/src/main/java/com/example/androidapp/ui/companysettings/CompanySettingsActivity.java

# Modify CompanySettingsDao.java
sed -i '/CompanySettings getCompanySettingsById(String id);/a \
    @Query("SELECT * FROM company_settings WHERE companyId = :companyId LIMIT 1")\
    LiveData<CompanySettings> getSettingsByCompanyId(String companyId);' app/src/main/java/com/example/androidapp/data/dao/CompanySettingsDao.java

# Modify CompanySettings.java
cat > app/src/main/java/com/example/androidapp/data/entities/CompanySettings.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "company_settings",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "companyId")})
public class CompanySettings {
    @PrimaryKey
    private @NonNull String id;
    private @NonNull String companyId;
    private String companyName;
    private String companyAddress;
    private String companyPhone;
    private String companyEmail;

    public CompanySettings(@NonNull String id, @NonNull String companyId, String companyName, String companyAddress, String companyPhone, String companyEmail) {
        this.id = id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
    }

    // Getters
    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    // Setters
    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setCompanyId(@NonNull String companyId) {
        this.companyId = companyId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }
}
EOF

# Modify Chat.java
cat > app/src/main/java/com/example/androidapp/data/entities/Chat.java << 'EOF'
package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "chats",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "senderId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "receiverId",
                           onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "companyId"), @Index(value = "senderId"), @Index(value = "receiverId")})
public class Chat {
    @PrimaryKey
    private @NonNull String id;
    private String message;
    private @NonNull String companyId;
    private @NonNull String senderId;
    private @NonNull String receiverId;
    private @NonNull Date createdAt;
    private boolean isRead;
    private String messageType;

    public Chat(@NonNull String id, String message, @NonNull String companyId, @NonNull String senderId, @NonNull String receiverId, @NonNull Date createdAt, boolean isRead, String messageType) {
        this.id = id;
        this.message = message;
        this.companyId = companyId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.messageType = messageType;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    @NonNull
    public String getSenderId() { return senderId; }
    public void setSenderId(@NonNull String senderId) { this.senderId = senderId; }
    @NonNull
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(@NonNull String receiverId) { this.receiverId = receiverId; }
    @NonNull
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(@NonNull Date createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
}
EOF

# Modify ChatDetailActivity.java
cat > app/src/main/java/com/example/androidapp/ui/chat/ChatDetailActivity.java << 'EOF'
package com.example.androidapp.ui.chat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ChatDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend;
    private GenericAdapter<Chat> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private String otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);
        otherUserId = getIntent().getStringExtra("other_user_id");

        if (otherUserId == null || otherUserId.isEmpty()) {
            Toast.makeText(this, "خطأ في تحديد المستخدم", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadMessages();
        markMessagesAsRead();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        setTitle("محادثة مع المستخدم " + otherUserId);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom
        recyclerView.setLayoutManager(layoutManager);
        
        adapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.chat_message_row,
                (chat, itemView) -> {
                    TextView tvMessage = itemView.findViewById(R.id.tv_message);
                    TextView tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
                    LinearLayout messageContainer = itemView.findViewById(R.id.message_container);

                    tvMessage.setText(chat.getMessage());
                    tvTimestamp.setText(dateFormat.format(chat.getCreatedAt()));

                    // Align messages based on sender
                    if (chat.getSenderId().equals(sessionManager.getCurrentUserId())) {
                        // Sent message - align right
                        messageContainer.setBackgroundResource(R.drawable.sent_message_background);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageContainer.getLayoutParams();
                        params.gravity = android.view.Gravity.END;
                        messageContainer.setLayoutParams(params);
                    } else {
                        // Received message - align left
                        messageContainer.setBackgroundResource(R.drawable.received_message_background);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageContainer.getLayoutParams();
                        params.gravity = android.view.Gravity.START;
                        messageContainer.setLayoutParams(params);
                    }
                },
                chat -> {
                    // No click action for individual messages
                }
        );
        
        recyclerView.setAdapter(adapter);
    }

    private void loadMessages() {
        database.chatDao().getChatsBetweenUsers(
                sessionManager.getCurrentUserId(), 
                otherUserId, 
                sessionManager.getCurrentCompanyId()
        ).observe(this, chats -> {
            if (chats != null) {
                adapter.updateData(chats);
                if (!chats.isEmpty()) {
                    recyclerView.scrollToPosition(chats.size() - 1);
                }
            }
        });
    }

    private void markMessagesAsRead() {
        new Thread(() -> {
            database.chatDao().markChatsAsRead(
                    sessionManager.getCurrentUserId(),
                    otherUserId,
                    sessionManager.getCurrentCompanyId()
            );
        }).start();
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        Chat newChat = new Chat(
                UUID.randomUUID().toString(),
                messageText,
                sessionManager.getCurrentCompanyId(),
                sessionManager.getCurrentUserId(),
                otherUserId,
                new Date(),
                false,
                "TEXT"
        );

        new Thread(() -> {
            database.chatDao().insert(newChat);
            runOnUiThread(() -> {
                etMessage.setText("");
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
EOF

# Modify ChatListActivity.java
cat > app/src/main/java/com/example/androidapp/ui/chat/ChatListActivity.java << 'EOF'
package com.example.androidapp.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Chat> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadChats();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton fab = findViewById(R.id.fab);

        setTitle("المحادثات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewChatActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.chat_list_row,
                (chat, itemView) -> {
                    TextView tvSenderName = itemView.findViewById(R.id.tv_sender_name);
                    TextView tvLastMessage = itemView.findViewById(R.id.tv_last_message);
                    TextView tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
                    TextView tvUnreadCount = itemView.findViewById(R.id.tv_unread_count);

                    // Get sender name (this would need to be joined with User table in a real implementation)
                    tvSenderName.setText("المستخدم " + chat.getSenderId());
                    tvLastMessage.setText(chat.getMessage());
                    tvTimestamp.setText(dateFormat.format(chat.getCreatedAt()));
                    
                    if (!chat.isRead()) {
                        tvUnreadCount.setVisibility(android.view.View.VISIBLE);
                        tvUnreadCount.setText("1");
                    } else {
                        tvUnreadCount.setVisibility(android.view.View.GONE);
                    }
                },
                chat -> {
                    Intent intent = new Intent(this, ChatDetailActivity.class);
                    intent.putExtra("other_user_id", chat.getSenderId().equals(sessionManager.getCurrentUserId()) ? 
                            chat.getReceiverId() : chat.getSenderId());
                    startActivity(intent);
                }
        );
        
        recyclerView.setAdapter(adapter);
    }

    private void loadChats() {
        database.chatDao().getAllChats(sessionManager.getCurrentCompanyId())
                .observe(this, chats -> {
                    if (chats != null) {
                        // Group chats by conversation (sender-receiver pair)
                        Map<String, Chat> latestChats = new HashMap<>();
                        String currentUserId = sessionManager.getCurrentUserId();
                        
                        for (Chat chat : chats) {
                            String conversationKey;
                            if (chat.getSenderId().equals(currentUserId)) {
                                conversationKey = currentUserId + "_" + chat.getReceiverId();
                            } else {
                                conversationKey = chat.getSenderId() + "_" + currentUserId;
                            }
                            
                            if (!latestChats.containsKey(conversationKey) || 
                                chat.getCreatedAt().after(latestChats.get(conversationKey).getCreatedAt())) {
                                latestChats.put(conversationKey, chat);
                            }
                        }
                        
                        adapter.updateData(new ArrayList<>(latestChats.values()));
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
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_refresh) {
            loadChats();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats();
    }
}
EOF

# Modify ConnectionDao.java
sed -i '/Connection getConnectionById(String id);/a \
    @Query("SELECT * FROM connections WHERE companyId = :companyId")\
    LiveData<List<Connection>> getConnectionsByCompanyId(String companyId);' app/src/main/java/com/example/androidapp/data/dao/ConnectionDao.java

# Modify ConnectionListActivity.java
sed -i 's/connectionDao = new ConnectionDao(App.getDatabaseHelper());/connectionDao = App.getDatabaseHelper().connectionDao();/' app/src/main/java/com/example/androidapp/ui/connection/ConnectionListActivity.java
sed -i 's/String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);/String companyId = sessionManager.getCompanyId();/' app/src/main/java/com/example/androidapp/ui/connection/ConnectionListActivity.java
sed -i 's/List<Connection> connections = connectionDao.getConnectionsByCompanyId(companyId);/connectionDao.getConnectionsByCompanyId(companyId).observe(this, connections -> {\n            if (connections != null) {\n                adapter.updateData(connections);\n            }\n        });/' app/src/main/java/com/example/androidapp/ui/connection/ConnectionListActivity.java
sed -i 's/adapter = new GenericAdapter<Connection>(connections) {/adapter = new GenericAdapter<Connection>(new ArrayList<>()) {/' app/src/main/java/com/example/androidapp/ui/connection/ConnectionListActivity.java

# Modify AppDatabase.java
sed -i 's/version = 4/version = 5/' app/src/main/java/com/example/androidapp/data/AppDatabase.java
sed -i 's/.fallbackToDestructiveMigration()//' app/src/main/java/com/example/androidapp/data/AppDatabase.java

# Modify App.java
sed -i 's/public static AppDatabase getDatabase()/public static AppDatabase getDatabaseHelper()/' app/src/main/java/com/example/androidapp/App.java

# Modify OrderListActivity.java
cat > app/src/main/java/com/example/androidapp/ui/order/OrderListActivity.java << 'EOF'
package com.example.androidapp.ui.order;

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
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Order> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadOrders();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton fab = findViewById(R.id.fab);

        setTitle("إدارة الطلبيات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.order_list_row,
                (order, itemView) -> {
                    TextView tvOrderId = itemView.findViewById(R.id.tv_order_id);
                    TextView tvOrderDate = itemView.findViewById(R.id.tv_order_date);
                    TextView tvTotalAmount = itemView.findViewById(R.id.tv_order_total_amount);
                    TextView tvStatus = itemView.findViewById(R.id.tv_order_status);
                    TextView tvNotes = itemView.findViewById(R.id.tv_order_notes);

                    tvOrderId.setText("طلبية #" + order.getId());
                    tvOrderDate.setText("التاريخ: " + dateFormat.format(order.getOrderDate()));
                    tvTotalAmount.setText("المبلغ: " + currencyFormat.format(order.getTotalAmount()));
                    tvStatus.setText(order.getStatus());
                    
                    if (order.getNotes() != null && !order.getNotes().isEmpty()) {
                        tvNotes.setText(order.getNotes());
                    } else {
                        tvNotes.setText("لا توجد ملاحظات");
                    }

                    int statusBackground;
                    if (order.getStatus() != null) {
                        switch (order.getStatus()) {
                            case "Completed":
                                statusBackground = R.drawable.status_active_background;
                                break;
                            case "Processing":
                                statusBackground = R.drawable.status_draft_background;
                                break;
                            case "Cancelled":
                                statusBackground = R.drawable.status_inactive_background;
                                break;
                            default:
                                statusBackground = R.drawable.status_pending_background;
                                break;
                        }
                        tvStatus.setBackgroundResource(statusBackground);
                    }
                },
                order -> {
                    Intent intent = new Intent(this, OrderDetailActivity.class);
                    intent.putExtra("order_id", order.getId());
                    startActivity(intent);
                }
        );
        
        recyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        database.orderDao().getAllOrders(sessionManager.getCurrentCompanyId()).observe(this, orders -> {
            if (orders != null) {
                adapter.updateData(orders);
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
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_refresh) {
            loadOrders();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}
EOF

echo "All fixes applied successfully!"

