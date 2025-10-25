package com.example.androidapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.accountingapp.advanced.ActivityLogManager;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ActivityViewHolder> {
    
    private List<ActivityLogManager.ActivityEntry> activities;
    
    public ActivityLogAdapter() {
        this.activities = new ArrayList<>();
    }
    
    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_log, parent, false);
        return new ActivityViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityLogManager.ActivityEntry activity = activities.get(position);
        holder.bind(activity);
    }
    
    @Override
    public int getItemCount() {
        return activities.size();
    }
    
    public void updateActivities(List<ActivityLogManager.ActivityEntry> newActivities) {
        this.activities.clear();
        this.activities.addAll(newActivities);
        notifyDataSetChanged();
    }
    
    class ActivityViewHolder extends RecyclerView.ViewHolder {
        private TextView typeText;
        private TextView descriptionText;
        private TextView usernameText;
        private TextView dateText;
        private TextView priorityText;
        
        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.typeText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            usernameText = itemView.findViewById(R.id.usernameText);
            dateText = itemView.findViewById(R.id.dateText);
            priorityText = itemView.findViewById(R.id.priorityText);
        }
        
        public void bind(ActivityLogManager.ActivityEntry activity) {
            typeText.setText(activity.type);
            descriptionText.setText(activity.description);
            usernameText.setText(activity.username);
            dateText.setText(activity.getFormattedDate());
            priorityText.setText(activity.getPriorityText());
            
            // تلوين حسب الأولوية
            int priorityColor = getPriorityColor(activity.priority);
            priorityText.setTextColor(priorityColor);
            typeText.setTextColor(priorityColor);
            
            // خلفية مختلفة للأنشطة الحرجة
            if (activity.priority == ActivityLogManager.PRIORITY_CRITICAL) {
                itemView.setBackgroundColor(Color.parseColor("#FFEBEE"));
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        
        private int getPriorityColor(int priority) {
            switch (priority) {
                case ActivityLogManager.PRIORITY_CRITICAL:
                    return Color.parseColor("#D32F2F");
                case ActivityLogManager.PRIORITY_HIGH:
                    return Color.parseColor("#F57C00");
                case ActivityLogManager.PRIORITY_MEDIUM:
                    return Color.parseColor("#1976D2");
                case ActivityLogManager.PRIORITY_LOW:
                default:
                    return Color.parseColor("#616161");
            }
        }
    }
}
