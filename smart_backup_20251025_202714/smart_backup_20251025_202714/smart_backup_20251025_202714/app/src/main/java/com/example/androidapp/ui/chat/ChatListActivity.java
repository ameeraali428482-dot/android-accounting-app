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
    private AppDatabase db;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadChats();
    }

    private void loadChats() {
        String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) return;

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent i = new Intent(ChatListActivity.this, ChatDetailActivity.class);
            i.putExtra("chat_id", item.getId());
            startActivity(i);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.chat_list_row;
            }
            @Override
            protected void bindView(View itemView, Chat c) {
                TextView tvName = itemView.findViewById(R.id.tvChatName);
                TextView tvMsg  = itemView.findViewById(R.id.chatMessage);
                TextView tvTime = itemView.findViewById(R.id.chatTimestamp);
                tvName.setText(c.getChatName());
                tvMsg .setText(c.getLastMessage());
                tvTime.setText(c.getLastMessageTime());
            }
        };

        recyclerView.setAdapter(adapter);
        db.chatDao().getAllChats(companyId).observe(this, list -> adapter.updateData(list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats();
    }
}
