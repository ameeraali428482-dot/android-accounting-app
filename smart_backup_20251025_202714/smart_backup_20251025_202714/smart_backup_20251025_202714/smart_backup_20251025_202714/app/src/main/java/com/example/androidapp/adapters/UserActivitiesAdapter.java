package com.example.androidapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.models.UserActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * محول عرض أنشطة المستخدمين
 */
public class UserActivitiesAdapter extends RecyclerView.Adapter<UserActivitiesAdapter.ActivityViewHolder> {
    
    private List<UserActivity> activities;
    private OnActivityClickListener listener;
    
    public UserActivitiesAdapter(List<UserActivity> activities) {
        this.activities = activities;
    }
    
    public interface OnActivityClickListener {
        void onActivityClick(UserActivity activity);
    }
    
    public void setOnActivityClickListener(OnActivityClickListener listener) {
        this.listener = listener;
    }
    
    public void updateActivities(List<UserActivity> newActivities) {
        this.activities = newActivities;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_user_activity, parent, false);
        return new ActivityViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        UserActivity activity = activities.get(position);
        holder.bind(activity);
    }
    
    @Override
    public int getItemCount() {
        return activities != null ? activities.size() : 0;
    }
    
    class ActivityViewHolder extends RecyclerView.ViewHolder {
        
        MaterialCardView cardView;
        MaterialTextView tvUserId;
        MaterialTextView tvActivityType;
        MaterialTextView tvDescription;
        MaterialTextView tvTimestamp;
        MaterialTextView tvEntityInfo;
        Chip chipActivityLevel;
        
        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.card_view);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvActivityType = itemView.findViewById(R.id.tv_activity_type);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvEntityInfo = itemView.findViewById(R.id.tv_entity_info);
            chipActivityLevel = itemView.findViewById(R.id.chip_activity_level);
            
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onActivityClick(activities.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(UserActivity activity) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault());
            
            tvUserId.setText("المستخدم: " + activity.getUserId());
            tvActivityType.setText(getActivityTypeName(activity.getActivityType()));
            tvDescription.setText(activity.getDescription());
            tvTimestamp.setText(sdf.format(new Date(activity.getTimestamp())));
            
            if (activity.getEntityType() != null && activity.getEntityId() != null) {
                tvEntityInfo.setText(getEntityTypeName(activity.getEntityType()) + ": " + activity.getEntityId());
                tvEntityInfo.setVisibility(View.VISIBLE);
            } else {
                tvEntityInfo.setVisibility(View.GONE);
            }
            
            // تحديد مستوى النشاط وتلوينه
            setupActivityLevel(activity);
        }
        
        private void setupActivityLevel(UserActivity activity) {
            String level = activity.getActivityLevel();
            
            switch (level) {
                case "HIGH":
                    chipActivityLevel.setText("عالي");
                    chipActivityLevel.setChipBackgroundColorResource(R.color.red);
                    cardView.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
                    break;
                case "MEDIUM":
                    chipActivityLevel.setText("متوسط");
                    chipActivityLevel.setChipBackgroundColorResource(R.color.orange);
                    cardView.setCardBackgroundColor(Color.parseColor("#FFF3E0"));
                    break;
                case "LOW":
                    chipActivityLevel.setText("منخفض");
                    chipActivityLevel.setChipBackgroundColorResource(R.color.green);
                    cardView.setCardBackgroundColor(Color.parseColor("#E8F5E8"));
                    break;
                default:
                    chipActivityLevel.setText("عادي");
                    chipActivityLevel.setChipBackgroundColorResource(R.color.blue);
                    break;
            }
        }
        
        private String getActivityTypeName(String activityType) {
            switch (activityType) {
                case "LOGIN": return "تسجيل دخول";
                case "LOGOUT": return "تسجيل خروج";
                case "CREATE": return "إنشاء";
                case "UPDATE": return "تعديل";
                case "DELETE": return "حذف";
                case "VIEW": return "عرض";
                case "EXPORT": return "تصدير";
                case "IMPORT": return "استيراد";
                case "LOCK": return "قفل";
                case "UNLOCK": return "إلغاء قفل";
                case "SEARCH": return "بحث";
                case "REPORT": return "تقرير";
                default: return activityType;
            }
        }
        
        private String getEntityTypeName(String entityType) {
            switch (entityType) {
                case "Invoice": return "فاتورة";
                case "Customer": return "عميل";
                case "Item": return "صنف";
                case "Account": return "حساب";
                case "Employee": return "موظف";
                case "Report": return "تقرير";
                default: return entityType;
            }
        }
    }
}