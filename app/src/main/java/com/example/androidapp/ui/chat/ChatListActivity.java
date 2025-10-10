import java.util.Date;
package com.example.androidapp.ui.chat;                                                         
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;                                                                                                                                                                 import androidx.appcompat.app.AppCompatActivity;
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

        fab.setOnClickListener(v -> {                                                                       Intent intent = new Intent(this, NewChatActivity.class);
            startActivity(intent);
        });
    }                                                                                                                                                                                               private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));                                                                                                                                   adapter = new GenericAdapter<>(
                new ArrayList<>(),                                                                              R.layout.chat_list_row,                                                                         (chat, itemView) -> {
                    TextView tvSenderName = chat.findViewById(R.id.tv_sender_name);                             TextView tvLastMessage = chat.findViewById(R.id.tv_last_message);                           TextView tvTimestamp = chat.findViewById(R.id.tv_timestamp);
                    TextView tvUnreadCount = chat.findViewById(R.id.tv_unread_count);                                                                                                                           // Get sender name (this would need to be joined with User table in a real implementation)                                                                                                      tvSenderName.setText("المستخدم " + itemView.getSenderId());                                         tvLastMessage.setText(itemView.getMessage());
                    tvTimestamp.setText(dateFormat.format(itemView.getCreatedAt()));                                                                                                                                    if (!itemView.isRead()) {                                                                               tvUnreadCount.setVisibility(android.view.View.VISIBLE);
                        tvUnreadCount.setText("1");                                                                 } else {                                                                                            tvUnreadCount.setVisibility(android.view.View.GONE);                                        }
                },
                chat -> {
                    Intent intent = new Intent(this, ChatDetailActivity.class);                                     intent.putExtra("other_user_id", itemView.getSenderId().equals(sessionManager.getCurrentUserId()) ?
                            itemView.getReceiverId() : itemView.getSenderId());
                    startActivity(intent);
                }                                                                                       );

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
                                ((Chat)holder.itemView.getTag()).getCreatedAt().after(latestChats.get(conversationKey).getCreatedAt())) {
                                latestChats.put(conversationKey, chat);
                            }
                        }

                        adapter.updateData(new ArrayList<>(latestChats.values()));
                    }                                                                                           });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                                                     getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();                                                                                       return true;
        } else if (itemId == R.id.action_refresh) {                                                         loadChats();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
                                                                                                    @Override
    protected void onResume() {                                                                         super.onResume();
        loadChats();
    }
}
