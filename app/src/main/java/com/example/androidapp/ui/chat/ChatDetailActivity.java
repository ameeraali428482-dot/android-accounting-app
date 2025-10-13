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
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ChatDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText etMessageInput;
    private Button btnSendMessage;
    private TextView tvChatTitle;
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
        tvChatTitle = findViewById(R.id.tvChatTitle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatId = getIntent().getStringExtra("chat_id");

        if (chatId != null) {
            loadChatDetails();
        }

        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private void loadChatDetails() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        
        database.chatDao().getChatById(chatId, companyId).observe(this, chat -> {
            if (chat != null) {
                tvChatTitle.setText(chat.getChatName());
            }
        });
    }

    private void sendMessage() {
        String messageText = etMessageInput.getText().toString().trim();

        if (messageText.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال نص الرسالة", Toast.LENGTH_SHORT).show();
            return;
        }

        // هنا يمكنك إضافة منطق إرسال الرسالة
        etMessageInput.setText("");
        Toast.makeText(this, "تم إرسال الرسالة", Toast.LENGTH_SHORT).show();
    }
}
