package com.example.androidapp.ui.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.ChatMessage;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ChatDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etMessageInput;
    private Button btnSendMessage;
    private GenericAdapter<ChatMessage> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatId = getIntent().getStringExtra("chat_id");

        btnSendMessage.setOnClickListener(v -> sendMessage());

        loadMessages();
    }

    private void loadMessages() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<ChatMessage>() {
            @Override
            public void onItemClick(ChatMessage item) {
                // لا حاجة لعمل شيء عند النقر على الرسالة
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.chat_message_item;
            }

            @Override
            protected void bindView(View itemView, ChatMessage message) {
                TextView tvMessageText = itemView.findViewById(R.id.tvMessageText);
                TextView tvTimestampText = itemView.findViewById(R.id.tvTimestampText);

                tvMessageText.setText(message.getMessageText());
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                tvTimestampText.setText(sdf.format(message.getTimestamp()));
            }
        };

        recyclerView.setAdapter(adapter);

        if (chatId != null) {
            database.chatMessageDao().getMessagesByChatId(chatId, companyId).observe(this, messages -> {
                if (messages != null) {
                    adapter.updateData(messages);
                }
            });
        }
    }

    private void sendMessage() {
        String messageText = etMessageInput.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        String userId = sessionManager.getUserDetails().get(SessionManager.KEY_USER_ID);

        if (messageText.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال نص الرسالة", Toast.LENGTH_SHORT).show();
            return;
        }

        ChatMessage chatMessage = new ChatMessage(
            UUID.randomUUID().toString(),
            companyId,
            chatId,
            userId,
            messageText,
            new Date(),
            false
        );

        Executors.newSingleThreadExecutor().execute(() -> {
            database.chatMessageDao().insert(chatMessage);
            runOnUiThread(() -> {
                etMessageInput.setText("");
                Toast.makeText(this, "تم إرسال الرسالة", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
