package com.example.androidapp.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter.GenericViewHolder> {
    private List<T> items;
    private OnItemClickListener<T> listener;

    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }

    public GenericAdapter(List<T> items, OnItemClickListener<T> listener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    protected abstract int getLayoutResId();
    protected abstract void bindView(View itemView, T item);

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false);
        return new GenericViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        T item = items.get(position);
        bindView(holder.itemView, item);
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<T> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setData(List<T> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class GenericViewHolder extends RecyclerView.ViewHolder {
        GenericViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
