package com.example.accountingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.accountingapp.advanced.BackupManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BackupAdapter extends RecyclerView.Adapter<BackupAdapter.BackupViewHolder> {
    
    private List<BackupManager.BackupInfo> backups;
    private OnBackupClickListener listener;
    private SimpleDateFormat dateFormat;
    
    public interface OnBackupClickListener {
        void onBackupClick(BackupManager.BackupInfo backup);
    }
    
    public BackupAdapter(OnBackupClickListener listener) {
        this.listener = listener;
        this.backups = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public BackupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_backup, parent, false);
        return new BackupViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BackupViewHolder holder, int position) {
        BackupManager.BackupInfo backup = backups.get(position);
        holder.bind(backup);
    }
    
    @Override
    public int getItemCount() {
        return backups.size();
    }
    
    public void updateBackups(List<BackupManager.BackupInfo> newBackups) {
        this.backups.clear();
        this.backups.addAll(newBackups);
        notifyDataSetChanged();
    }
    
    class BackupViewHolder extends RecyclerView.ViewHolder {
        private TextView fileNameText;
        private TextView usernameText;
        private TextView dateText;
        private TextView sizeText;
        
        public BackupViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameText = itemView.findViewById(R.id.fileNameText);
            usernameText = itemView.findViewById(R.id.usernameText);
            dateText = itemView.findViewById(R.id.dateText);
            sizeText = itemView.findViewById(R.id.sizeText);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBackupClick(backups.get(position));
                }
            });
        }
        
        public void bind(BackupManager.BackupInfo backup) {
            fileNameText.setText(backup.fileName);
            usernameText.setText("المستخدم: " + backup.username);
            dateText.setText(dateFormat.format(backup.createdDate));
            sizeText.setText(backup.getFormattedSize());
        }
    }
}
