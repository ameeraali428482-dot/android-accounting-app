package com.example.androidapp.ui.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.ChatMessage;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ChatDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private GenericAdapter<ChatMessage> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String chatId;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        chatId = getIntent().getStringExtra("chat_id");

        initViews();
        setupRecyclerView();
        loadMessages();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<>(new ArrayList<>(), null) {
            @Override
            protected int getLayoutResId() {
                return R.layout.chat_message_row;
            }

            @Override
            protected void bindView(View itemView, ChatMessage message) {
                // ربط البيانات مع عناصر الواجهة في chat_message_row.xml
                TextView messageText = itemView.findViewById(R.id.messageText);
                TextView senderName = itemView.findViewById(R.id.senderName);
                TextView timestamp = itemView.findViewById(R.id.timestamp);
                
                messageText.setText(message.getMessage());
                senderName.setText(message.getSenderId());
                timestamp.setText(message.getTimestamp().toString());
            }
        };
        
        recyclerView.setAdapter(adapter);
    }

    private void loadMessages() {
        if (chatId != null && companyId != null) {
            database.chatMessageDao().getMessagesByChat(chatId, companyId)
                    .observe(this, messages -> {
                        if (messages != null) {
                            adapter.updateData(messages);
                            recyclerView.scrollToPosition(messages.size() - 1);
                        }
                    });
        }
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (messageText.isEmpty() || chatId == null || companyId == null) {
            return;
        }

        String userId = sessionManager.getUserDetails().get(SessionManager.KEY_USER_ID);
        ChatMessage message = new ChatMessage(
            UUID.randomUUID().toString(),
            chatId,
            userId,
            messageText,
            "TEXT",
            new Date(),
            false,
            companyId
        );

        database.chatMessageDao().insert(message);
        messageEditText.setText("");
    }
}
