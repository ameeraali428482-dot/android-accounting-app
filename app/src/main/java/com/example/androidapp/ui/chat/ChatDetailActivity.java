package com.example.androidapp.ui.chat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class ChatDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend;
    private GenericAdapter<Chat> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private int otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);
        otherUserId = getIntent().getIntExtra("other_user_id", -1);

        if (otherUserId == -1) {
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom
        recyclerView.setLayoutManager(layoutManager);
        
        adapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.chat_message_row,
                (chat, view) -> {
                    TextView tvMessage = view.findViewById(R.id.tv_message);
                    TextView tvTimestamp = view.findViewById(R.id.tv_timestamp);
                    LinearLayout messageContainer = view.findViewById(R.id.message_container);

                    tvMessage.setText(chat.getMessage());
                    tvTimestamp.setText(dateFormat.format(chat.getTimestamp()));

                    // Align messages based on sender
                    if (chat.getSenderId() == sessionManager.getCurrentUserId()) {
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
                sessionManager.getCurrentUserId(),
                otherUserId,
                sessionManager.getCurrentCompanyId(),
                messageText,
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
