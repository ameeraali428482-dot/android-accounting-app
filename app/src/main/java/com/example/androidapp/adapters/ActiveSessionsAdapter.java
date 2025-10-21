package com.example.androidapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.models.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * محول عرض الجلسات النشطة للمستخدمين
 */
public class ActiveSessionsAdapter extends RecyclerView.Adapter<ActiveSessionsAdapter.SessionViewHolder> {
    
    private List<UserSession> sessions;
    private OnSessionActionListener listener;
    
    public ActiveSessionsAdapter(List<UserSession> sessions) {
        this.sessions = sessions;
    }
    
    public interface OnSessionActionListener {
        void onTerminateSession(UserSession session);
        void onSendMessage(UserSession session);
        void onViewActivity(UserSession session);
    }
    
    public void setOnSessionActionListener(OnSessionActionListener listener) {
        this.listener = listener;
    }
    
    public void updateSessions(List<UserSession> newSessions) {
        this.sessions = newSessions;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_active_session, parent, false);
        return new SessionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        UserSession session = sessions.get(position);
        holder.bind(session);
    }
    
    @Override
    public int getItemCount() {
        return sessions != null ? sessions.size() : 0;
    }
    
    class SessionViewHolder extends RecyclerView.ViewHolder {
        
        MaterialCardView cardView;
        MaterialTextView tvUserId;
        MaterialTextView tvDeviceInfo;
        MaterialTextView tvLoginTime;
        MaterialTextView tvLastActivity;
        MaterialTextView tvSessionDuration;
        Chip chipOnlineStatus;
        MaterialButton btnTerminate;
        MaterialButton btnMessage;
        MaterialButton btnViewActivity;
        
        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.card_view);
            tvUserId = itemView.findViewById(R.id.tv_user_id);
            tvDeviceInfo = itemView.findViewById(R.id.tv_device_info);
            tvLoginTime = itemView.findViewById(R.id.tv_login_time);
            tvLastActivity = itemView.findViewById(R.id.tv_last_activity);
            tvSessionDuration = itemView.findViewById(R.id.tv_session_duration);
            chipOnlineStatus = itemView.findViewById(R.id.chip_online_status);
            btnTerminate = itemView.findViewById(R.id.btn_terminate);
            btnMessage = itemView.findViewById(R.id.btn_message);
            btnViewActivity = itemView.findViewById(R.id.btn_view_activity);
        }
        
        public void bind(UserSession session) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
            
            tvUserId.setText("المستخدم: " + session.getUserId());
            tvDeviceInfo.setText("الجهاز: " + session.getDeviceInfo());
            tvLoginTime.setText("وقت الدخول: " + sdf.format(new Date(session.getLoginTime())));
            tvLastActivity.setText("آخر نشاط: " + sdf.format(new Date(session.getLastActivityTime())));
            
            // مدة الجلسة
            long duration = session.getSessionDuration();
            long hours = duration / (1000 * 60 * 60);
            long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
            tvSessionDuration.setText("مدة الجلسة: " + hours + "س " + minutes + "د");
            
            // حالة الاتصال
            if (session.isOnline()) {
                chipOnlineStatus.setText("متصل");
                chipOnlineStatus.setChipBackgroundColorResource(R.color.green);
            } else {
                chipOnlineStatus.setText("غير نشط");
                chipOnlineStatus.setChipBackgroundColorResource(R.color.orange);
            }
            
            // تلوين البطاقة حسب الحالة
            if (!session.isActive()) {
                cardView.setCardBackgroundColor(Color.parseColor("#FFEBEE")); // أحمر فاتح
            } else if (!session.isOnline()) {
                cardView.setCardBackgroundColor(Color.parseColor("#FFF3E0")); // برتقالي فاتح
            } else {
                cardView.setCardBackgroundColor(Color.parseColor("#E8F5E8")); // أخضر فاتح
            }
            
            // أحداث الأزرار
            btnTerminate.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTerminateSession(session);
                }
            });
            
            btnMessage.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSendMessage(session);
                }
            });
            
            btnViewActivity.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewActivity(session);
                }
            });
        }
    }
}