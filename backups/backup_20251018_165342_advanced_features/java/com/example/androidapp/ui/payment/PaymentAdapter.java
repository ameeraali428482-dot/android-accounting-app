package com.example.androidapp.ui.payment;

import java.util.Date;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.Payment;
import java.util.ArrayList;
import java.util.List;






public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {
    private Context context;
    private List<Payment> payments;
    private OnPaymentClickListener listener;

    public interface OnPaymentClickListener {
        void onPaymentClick(Payment payment);
    }

    public PaymentAdapter(Context context, OnPaymentClickListener listener) {
        this.context = context;
        this.payments = new ArrayList<>();
        this.listener = listener;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_list_row, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Payment payment = payments.get(position);
        holder.bind(payment);
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView textPaymentDate, textPayerName, textAmount, textPaymentMethod, textReferenceNumber;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPaymentClick(payments.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Payment payment) {
            textPaymentDate.setText(payment.getPaymentDate());
            textPayerName.setText(payment.getPayerId()); // You might want to resolve this to actual name
            textAmount.setText(String.format("%.2f", payment.getAmount()));
            textPaymentMethod.setText(payment.getPaymentMethod());
            textReferenceNumber.setText(payment.getReferenceNumber());
        }
    }
}
