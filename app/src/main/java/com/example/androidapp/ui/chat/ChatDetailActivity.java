package com.example.androidapp.ui.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
    private EditText input;
    private ImageButton send;
    private GenericAdapter<ChatMessage> adapter;
    private AppDatabase db;
    private SessionManager sm;
    private String chatId, companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);
        companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        chatId = getIntent().getStringExtra("chat_id");

        recyclerView = findViewById(R.id.recyclerView);
        input        = findViewById(R.id.input);
        send         = findViewById(R.id.send);

        setupRecycler();
        loadMessages();

        send.setOnClickListener(v -> sendMessage());
    }

    private void setupRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GenericAdapter<>(new ArrayList<>(), null) {
            @Override
            protected int getLayoutResId() {
                return R.layout.chat_message_row;
            }
            @Override
            protected void bindView(View itemView, ChatMessage m) {
                TextView tvText  = itemView.findViewById(R.id.messageText);
                TextView tvUser  = itemView.findViewById(R.id.senderName);
                TextView tvTime  = itemView.findViewById(R.id.timestamp);
                tvText.setText(m.getMessage());
                tvUser.setText(m.getSenderId());
                tvTime.setText(m.getTimestamp().toString());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadMessages() {
        if (chatId == null || companyId == null) return;
        db.chatMessageDao().getMessagesByChat(chatId, companyId)
                .observe(this, list -> {
                    adapter.updateData(list);
                    recyclerView.scrollToPosition(list.size() - 1);
                });
    }

    private void sendMessage() {
        String text = input.getText().toString().trim();
        if (text.isEmpty()) return;
        String userId = sm.getUserDetails().get(SessionManager.KEY_USER_ID);
        ChatMessage m = new ChatMessage(
                UUID.randomUUID().toString(),
                chatId,
                userId,
                text,
                "TEXT",
                new Date(),
                false,
                companyId
        );
        db.chatMessageDao().insert(m);
        input.setText("");
    }
}
