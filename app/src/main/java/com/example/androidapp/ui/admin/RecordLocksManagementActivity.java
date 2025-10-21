package com.example.androidapp.ui.admin;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.RecordLock;
import com.example.androidapp.utils.ConcurrentSessionManager;
import com.example.androidapp.utils.PermissionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * نشاط إدارة الأقفال النشطة
 * يعرض جميع الأقفال النشطة ويسمح بإلغائها أو مراقبتها
 */
public class RecordLocksManagementActivity extends AppCompatActivity {
    
    private static final String TAG = "RecordLocksManagement";
    
    private ConcurrentSessionManager sessionManager;
    private PermissionManager permissionManager;
    
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialTextView tvEmptyState;
    private ActiveLocksAdapter adapter;
    
    private List<RecordLock> activeLocks = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_locks_management);
        
        initializeComponents();
        setupUI();
        loadActiveLocks();
        
        // إعداد شريط الأدوات
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("إدارة الأقفال النشطة");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initializeComponents() {
        sessionManager = new ConcurrentSessionManager(this);
        permissionManager = new PermissionManager(this);
        
        // التحقق من الصلاحيات
        if (!permissionManager.hasAdminPermission()) {
            finish();
            return;
        }
        
        recyclerView = findViewById(R.id.recycler_view_locks);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        
        adapter = new ActiveLocksAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupUI() {
        swipeRefreshLayout.setOnRefreshListener(this::loadActiveLocks);
        swipeRefreshLayout.setColorSchemeColors(getColor(R.color.primary));
    }
    
    private void loadActiveLocks() {
        swipeRefreshLayout.setRefreshing(true);
        
        new Thread(() -> {
            try {
                List<RecordLock> locks = sessionManager.getAllActiveLocks();
                
                runOnUiThread(() -> {
                    activeLocks.clear();
                    activeLocks.addAll(locks);
                    adapter.notifyDataSetChanged();
                    
                    // إظهار حالة فارغة إذا لم توجد أقفال
                    if (activeLocks.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    
                    swipeRefreshLayout.setRefreshing(false);
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(this, "خطأ في تحميل الأقفال", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private void releaseLock(RecordLock lock) {
        new AlertDialog.Builder(this)
            .setTitle("إلغاء القفل")
            .setMessage("هل أنت متأكد من إلغاء هذا القفل؟\n" +
                       "المستخدم: " + lock.getUserId() + "\n" +
                       "نوع السجل: " + lock.getEntityType())
            .setPositiveButton("نعم", (dialog, which) -> {
                new Thread(() -> {
                    try {
                        boolean success = sessionManager.forcefullyReleaseLock(
                            lock.getRecordId(), lock.getEntityType());
                        
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(this, "تم إلغاء القفل بنجاح", 
                                             Toast.LENGTH_SHORT).show();
                                loadActiveLocks(); // تحديث القائمة
                            } else {
                                Toast.makeText(this, "فشل في إلغاء القفل", 
                                             Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                    } catch (Exception e) {
                        runOnUiThread(() -> 
                            Toast.makeText(this, "خطأ في إلغاء القفل", 
                                         Toast.LENGTH_SHORT).show());
                    }
                }).start();
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_locks_management, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                loadActiveLocks();
                return true;
            case R.id.action_release_all:
                releaseAllLocks();
                return true;
            case R.id.action_release_expired:
                releaseExpiredLocks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void releaseAllLocks() {
        new AlertDialog.Builder(this)
            .setTitle("إلغاء جميع الأقفال")
            .setMessage("تحذير: سيتم إلغاء جميع الأقفال النشطة!\nهذا قد يؤثر على المستخدمين النشطين.")
            .setPositiveButton("تأكيد", (dialog, which) -> {
                new Thread(() -> {
                    try {
                        int releasedCount = sessionManager.releaseAllLocks();
                        
                        runOnUiThread(() -> {
                            Toast.makeText(this, 
                                "تم إلغاء " + releasedCount + " قفل", 
                                Toast.LENGTH_SHORT).show();
                            loadActiveLocks();
                        });
                        
                    } catch (Exception e) {
                        runOnUiThread(() -> 
                            Toast.makeText(this, "خطأ في إلغاء الأقفال", 
                                         Toast.LENGTH_SHORT).show());
                    }
                }).start();
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    private void releaseExpiredLocks() {
        new Thread(() -> {
            try {
                int releasedCount = sessionManager.releaseExpiredLocks();
                
                runOnUiThread(() -> {
                    Toast.makeText(this, 
                        "تم إلغاء " + releasedCount + " قفل منتهي الصلاحية", 
                        Toast.LENGTH_SHORT).show();
                    loadActiveLocks();
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> 
                    Toast.makeText(this, "خطأ في إلغاء الأقفال المنتهية", 
                                 Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    
    /**
     * محول عرض الأقفال النشطة
     */
    private class ActiveLocksAdapter extends RecyclerView.Adapter<ActiveLocksAdapter.LockViewHolder> {
        
        @NonNull
        @Override
        public LockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_lock, parent, false);
            return new LockViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull LockViewHolder holder, int position) {
            RecordLock lock = activeLocks.get(position);
            holder.bind(lock);
        }
        
        @Override
        public int getItemCount() {
            return activeLocks.size();
        }
        
        class LockViewHolder extends RecyclerView.ViewHolder {
            
            MaterialCardView cardView;
            MaterialTextView tvRecordId;
            MaterialTextView tvEntityType;
            MaterialTextView tvUserId;
            MaterialTextView tvLockTime;
            MaterialTextView tvExpiryTime;
            MaterialTextView tvTimeRemaining;
            MaterialButton btnRelease;
            
            public LockViewHolder(@NonNull View itemView) {
                super(itemView);
                
                cardView = itemView.findViewById(R.id.card_view);
                tvRecordId = itemView.findViewById(R.id.tv_record_id);
                tvEntityType = itemView.findViewById(R.id.tv_entity_type);
                tvUserId = itemView.findViewById(R.id.tv_user_id);
                tvLockTime = itemView.findViewById(R.id.tv_lock_time);
                tvExpiryTime = itemView.findViewById(R.id.tv_expiry_time);
                tvTimeRemaining = itemView.findViewById(R.id.tv_time_remaining);
                btnRelease = itemView.findViewById(R.id.btn_release);
            }
            
            public void bind(RecordLock lock) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
                
                tvRecordId.setText("رقم السجل: " + lock.getRecordId());
                tvEntityType.setText("نوع البيانات: " + getEntityTypeName(lock.getEntityType()));
                tvUserId.setText("المستخدم: " + lock.getUserId());
                tvLockTime.setText("وقت القفل: " + sdf.format(new Date(lock.getLockTime())));
                tvExpiryTime.setText("انتهاء الصلاحية: " + sdf.format(new Date(lock.getExpiryTime())));
                
                // حساب الوقت المتبقي
                long currentTime = System.currentTimeMillis();
                long timeRemaining = lock.getExpiryTime() - currentTime;
                
                if (timeRemaining > 0) {
                    long minutes = timeRemaining / (1000 * 60);
                    tvTimeRemaining.setText("متبقي: " + minutes + " دقيقة");
                    tvTimeRemaining.setTextColor(Color.parseColor("#4CAF50"));
                } else {
                    tvTimeRemaining.setText("منتهي الصلاحية");
                    tvTimeRemaining.setTextColor(Color.parseColor("#F44336"));
                    cardView.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
                }
                
                // زر إلغاء القفل
                btnRelease.setOnClickListener(v -> releaseLock(lock));
            }
            
            private String getEntityTypeName(String entityType) {
                switch (entityType) {
                    case "Invoice": return "فاتورة";
                    case "Customer": return "عميل";
                    case "Item": return "صنف";
                    case "Account": return "حساب";
                    default: return entityType;
                }
            }
        }
    }
}