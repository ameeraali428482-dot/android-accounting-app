package com.example.accountingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountingapp.advanced.ActivityLogManager;

import java.util.ArrayList;
import java.util.List;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ActivityLogViewHolder> {
    
    private Context context;
    private List<ActivityLogManager.ActivityEntry> activities;
    
    public ActivityLogAdapter(Context context) {
        this.context = context;
        this.activities = new ArrayList<>();
    }
    
    public void setActivities(List<ActivityLogManager.ActivityEntry> activities) {
        this.activities = activities;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ActivityLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity_log, parent, false);
        return new ActivityLogViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ActivityLogViewHolder holder, int position) {
        ActivityLogManager.ActivityEntry activity = activities.get(position);
        
        holder.textActivityType.setText(activity.getTypeDisplayName());
        holder.textDescription.setText(activity.description);
        holder.textUsername.setText(activity.username);
        holder.textDate.setText(activity.getFormattedDate());
        
        if (!activity.details.isEmpty()) {
            holder.textDetails.setText(activity.details);
            holder.textDetails.setVisibility(View.VISIBLE);
        } else {
            holder.textDetails.setVisibility(View.GONE);
        }
        
        // تلوين حسب نوع النشاط
        int colorResId = getColorForActivityType(activity.type);
        holder.textActivityType.setTextColor(context.getResources().getColor(colorResId));
    }
    
    private int getColorForActivityType(ActivityLogManager.ActivityType type) {
        switch (type) {
            case LOGIN:
            case LOGOUT:
                return android.R.color.holo_blue_dark;
            case CREATE_INVOICE:
            case CREATE_ACCOUNT:
                return android.R.color.holo_green_dark;
            case UPDATE_INVOICE:
            case UPDATE_ACCOUNT:
                return android.R.color.holo_orange_dark;
            case DELETE_INVOICE:
            case DELETE_ACCOUNT:
                return android.R.color.holo_red_dark;
            case FINANCIAL_TRANSACTION:
                return android.R.color.holo_purple;
            case SYSTEM_ERROR:
                return android.R.color.holo_red_light;
            default:
                return android.R.color.black;
        }
    }
    
    @Override
    public int getItemCount() {
        return activities.size();
    }
    
    static class ActivityLogViewHolder extends RecyclerView.ViewHolder {
        TextView textActivityType, textDescription, textUsername, textDate, textDetails;
        
        ActivityLogViewHolder(@NonNull View itemView) {
            super(itemView);
            
            textActivityType = itemView.findViewById(R.id.textActivityType);
            textDescription = itemView.findViewById(R.id.textDescription);
            textUsername = itemView.findViewById(R.id.textUsername);
            textDate = itemView.findViewById(R.id.textDate);
            textDetails = itemView.findViewById(R.id.textDetails);
        }
    }
}
