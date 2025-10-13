package com.example.androidapp.ui.chat;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
            Toast.makeText(this, "خطأ في تحميل المحادثة", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        loadMessages();
        markMessagesAsRead();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessageInput);
        btnSend = findViewById(R.id.btnSendMessage);

        setTitle("محادثة مع " + otherUserId);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new GenericAdapter<Chat>(new ArrayList<>(), chat -> {
            // Handle chat click if needed
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.chat_message_row;
            }

            @Override
            protected void bindView(View itemView, Chat chat) {
                TextView tvMessageText = itemView.findViewById(R.id.tvMessageText);
                TextView tvTimestampText = itemView.findViewById(R.id.tvTimestampText);
                LinearLayout messageContainerLayout = itemView.findViewById(R.id.messageContainerLayout);

                if (tvMessageText != null) tvMessageText.setText(chat.getMessage());
                if (tvTimestampText != null) tvTimestampText.setText(dateFormat.format(chat.getCreatedAt()));
                
                // Align message based on sender
                if (messageContainerLayout != null) {
                    if (chat.getSenderId().equals(sessionManager.getCurrentUserId())) {
                        // My message - align right
                    } else {
                        // Other user's message - align left
                    }
                }
            }
        };
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
            runOnUiThread(() -> etMessage.setText(""));
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
