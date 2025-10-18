package com.example.accountingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountingapp.advanced.BackupManager;

import java.util.ArrayList;
import java.util.List;

public class BackupAdapter extends RecyclerView.Adapter<BackupAdapter.BackupViewHolder> {
    
    public interface OnBackupActionListener {
        void onBackupAction(BackupManager.BackupInfo backup, String action);
    }
    
    private Context context;
    private List<BackupManager.BackupInfo> backups;
    private OnBackupActionListener listener;
    
    public BackupAdapter(Context context, OnBackupActionListener listener) {
        this.context = context;
        this.listener = listener;
        this.backups = new ArrayList<>();
    }
    
    public void setBackups(List<BackupManager.BackupInfo> backups) {
        this.backups = backups;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public BackupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_backup, parent, false);
        return new BackupViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BackupViewHolder holder, int position) {
        BackupManager.BackupInfo backup = backups.get(position);
        
        holder.textFileName.setText(backup.fileName);
        holder.textDate.setText(backup.getFormattedDate());
        holder.textSize.setText(backup.getFormattedSize());
        
        holder.btnRestore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBackupAction(backup, "restore");
            }
        });
        
        holder.btnExport.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBackupAction(backup, "export");
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBackupAction(backup, "delete");
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return backups.size();
    }
    
    static class BackupViewHolder extends RecyclerView.ViewHolder {
        TextView textFileName, textDate, textSize;
        Button btnRestore, btnExport, btnDelete;
        
        BackupViewHolder(@NonNull View itemView) {
            super(itemView);
            
            textFileName = itemView.findViewById(R.id.textFileName);
            textDate = itemView.findViewById(R.id.textDate);
            textSize = itemView.findViewById(R.id.textSize);
            btnRestore = itemView.findViewById(R.id.btnRestore);
            btnExport = itemView.findViewById(R.id.btnExport);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
