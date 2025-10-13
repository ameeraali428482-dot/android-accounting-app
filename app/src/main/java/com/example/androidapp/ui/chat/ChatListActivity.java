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

        chatRecyclerView = findViewById(R.id.recyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GenericAdapter<Chat>(new ArrayList<>(), chat -> {
            Intent intent = new Intent(ChatListActivity.this, ChatDetailActivity.class);
            intent.putExtra("chat_id", chat.getId());
            intent.putExtra("other_user_id", 
                chat.getSenderId().equals(sessionManager.getCurrentUserId()) 
                    ? chat.getReceiverId() 
                    : chat.getSenderId()
            );
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.chat_list_row;
            }

            @Override
            protected void bindView(View itemView, Chat chat) {
                TextView chatMessagePreview = itemView.findViewById(R.id.chatMessage);
                TextView chatTimestampPreview = itemView.findViewById(R.id.chatTimestamp);

                if (chatMessagePreview != null) chatMessagePreview.setText(chat.getMessage());
                if (chatTimestampPreview != null) chatTimestampPreview.setText(chat.getCreatedAt().toString());
            }
        };
        chatRecyclerView.setAdapter(adapter);
        loadChats();
    }

    private void loadChats() {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                List<Chat> chats = database.chatDao().getAllChatsByUserId(userId, sessionManager.getCurrentCompanyId());
                runOnUiThread(() -> {
                    if (chats != null) {
                        adapter.setData(chats);
                    }
                });
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats();
    }
}
