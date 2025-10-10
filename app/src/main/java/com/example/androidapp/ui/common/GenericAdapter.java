package com.example.androidapp.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter.GenericViewHolder> {

    private List<T> dataList;
    private OnItemClickListener<T> clickListener;

    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }

    public GenericAdapter(List<T> dataList, OnItemClickListener<T> clickListener) {
        this.dataList = dataList != null ? dataList : new ArrayList<>();
        this.clickListener = clickListener;
    }

    public void setData(List<T> newData) {
        this.dataList = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void updateData(List<T> newData) {
        this.dataList.clear();
        if (newData != null) {
            this.dataList.addAll(newData);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false);
        return new GenericViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        T item = dataList.get(position);
        bindView(holder.itemView, item);
        
        if (clickListener != null) {
            holder.itemView.setOnClickListener(v -> clickListener.onItemClick(item));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    protected abstract int getLayoutResId();
    protected abstract void bindView(View itemView, T item);

    public static class GenericViewHolder extends RecyclerView.ViewHolder {
        public GenericViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
