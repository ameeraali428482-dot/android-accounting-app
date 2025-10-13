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

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Chat> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadChats();
    }

    private void loadChats() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<Chat>() {
            @Override
            public void onItemClick(Chat item) {
                Intent intent = new Intent(ChatListActivity.this, ChatDetailActivity.class);
                intent.putExtra("chat_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.chat_list_row;
            }

            @Override
            protected void bindView(View itemView, Chat chat) {
                TextView tvChatName = itemView.findViewById(R.id.tvChatName);
                TextView chatMessage = itemView.findViewById(R.id.chatMessage);
                TextView chatTimestamp = itemView.findViewById(R.id.chatTimestamp);

                tvChatName.setText(chat.getChatName());
                chatMessage.setText(chat.getLastMessage());
                chatTimestamp.setText(chat.getLastMessageTime());
            }
        };

        recyclerView.setAdapter(adapter);

        database.chatDao().getAllChats(companyId).observe(this, chats -> {
            if (chats != null) {
                adapter.updateData(chats);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats();
    }
}
