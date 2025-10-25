package com.example.androidapp.ui.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.utils.Material3Helper;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Adapter عصري للعملاء مع تصميم Material Design 3
 * يدعم العرض الغني للمعلومات والتفاعل المحسن
 */
public class ModernCustomerAdapter extends RecyclerView.Adapter<ModernCustomerAdapter.CustomerViewHolder> {
    
    private List<Customer> customers;
    private OnCustomerClickListener listener;
    
    public interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
    }
    
    public ModernCustomerAdapter(List<Customer> customers, OnCustomerClickListener listener) {
        this.customers = customers;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_customer_modern, parent, false);
        return new CustomerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer);
    }
    
    @Override
    public int getItemCount() {
        return customers.size();
    }
    
    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        
        private MaterialCardView cardView;
        private ImageView avatarImageView;
        private TextView nameTextView;
        private TextView phoneTextView;
        private TextView emailTextView;
        private TextView addressTextView;
        private TextView lastContactTextView;
        private Chip statusChip;
        private Chip vipChip;
        private View divider;
        
        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            lastContactTextView = itemView.findViewById(R.id.lastContactTextView);
            statusChip = itemView.findViewById(R.id.statusChip);
            vipChip = itemView.findViewById(R.id.vipChip);
            divider = itemView.findViewById(R.id.divider);
            
            // Apply Material 3 styling
            Material3Helper.Components.setupMaterialCard(cardView, Material3Helper.CardStyle.FILLED);
            
            // Set click listener
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCustomerClick(customers.get(getAdapterPosition()));
                }
            });
        }
        
        public void bind(Customer customer) {
            // Customer Name
            nameTextView.setText(customer.getName() != null ? customer.getName() : "غير محدد");
            
            // Phone
            if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
                phoneTextView.setText(customer.getPhone());
                phoneTextView.setVisibility(View.VISIBLE);
            } else {
                phoneTextView.setVisibility(View.GONE);
            }
            
            // Email
            if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
                emailTextView.setText(customer.getEmail());
                emailTextView.setVisibility(View.VISIBLE);
            } else {
                emailTextView.setVisibility(View.GONE);
            }
            
            // Address
            if (customer.getAddress() != null && !customer.getAddress().isEmpty()) {
                addressTextView.setText(customer.getAddress());
                addressTextView.setVisibility(View.VISIBLE);
            } else {
                addressTextView.setVisibility(View.GONE);
            }
            
            // Last Contact
            if (customer.getLastContactDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("ar"));
                lastContactTextView.setText("آخر اتصال: " + dateFormat.format(customer.getLastContactDate()));
                lastContactTextView.setVisibility(View.VISIBLE);
            } else {
                lastContactTextView.setText("لم يتم الاتصال بعد");
                lastContactTextView.setVisibility(View.VISIBLE);
            }
            
            // Status Chip
            if (customer.isActive()) {
                statusChip.setText("نشط");
                statusChip.setChipBackgroundColorResource(R.color.success_container);
                statusChip.setTextColor(itemView.getContext().getColor(R.color.on_success_container));
            } else {
                statusChip.setText("غير نشط");
                statusChip.setChipBackgroundColorResource(R.color.error_container);
                statusChip.setTextColor(itemView.getContext().getColor(R.color.on_error_container));
            }
            statusChip.setVisibility(View.VISIBLE);
            
            // VIP Chip
            if (customer.isVip()) {
                vipChip.setText("VIP");
                vipChip.setChipBackgroundColorResource(R.color.tertiary_container);
                vipChip.setTextColor(itemView.getContext().getColor(R.color.on_tertiary_container));
                vipChip.setVisibility(View.VISIBLE);
            } else {
                vipChip.setVisibility(View.GONE);
            }
            
            // Avatar (placeholder for now)
            generateAvatar(customer.getName());
            
            // Handle empty states
            boolean hasContactInfo = (customer.getPhone() != null && !customer.getPhone().isEmpty()) ||
                                   (customer.getEmail() != null && !customer.getEmail().isEmpty());
            
            if (!hasContactInfo) {
                phoneTextView.setText("لا توجد معلومات اتصال");
                phoneTextView.setVisibility(View.VISIBLE);
                phoneTextView.setTextColor(itemView.getContext().getColor(R.color.on_surface_variant));
            }
        }
        
        private void generateAvatar(String name) {
            // إنشاء avatar بسيط باستخدام الحرف الأول من الاسم
            if (name != null && !name.isEmpty()) {
                String initial = name.substring(0, 1).toUpperCase();
                // يمكن تطوير هذا لإنشاء avatar ملون أو استخدام مكتبة خارجية
                
                // للآن، سنستخدم drawable افتراضي
                avatarImageView.setImageResource(R.drawable.ic_notification);
                avatarImageView.setVisibility(View.VISIBLE);
            } else {
                avatarImageView.setImageResource(R.drawable.ic_notification);
                avatarImageView.setVisibility(View.VISIBLE);
            }
        }
    }
    
    public void updateCustomers(List<Customer> newCustomers) {
        this.customers = newCustomers;
        notifyDataSetChanged();
    }
    
    public void addCustomer(Customer customer) {
        customers.add(customer);
        notifyItemInserted(customers.size() - 1);
    }
    
    public void removeCustomer(int position) {
        if (position >= 0 && position < customers.size()) {
            customers.remove(position);
            notifyItemRemoved(position);
        }
    }
    
    public void updateCustomer(int position, Customer customer) {
        if (position >= 0 && position < customers.size()) {
            customers.set(position, customer);
            notifyItemChanged(position);
        }
    }
}