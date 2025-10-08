package com.example.androidapp.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter.GenericViewHolder> {

    private List<T> dataList;
    private int layoutResId;
    private OnItemBindListener<T> bindListener;
    private OnItemClickListener<T> clickListener;

    public interface OnItemBindListener<T> {
        void onBind(View view, T item);
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }

    public GenericAdapter(List<T> dataList, int layoutResId, OnItemBindListener<T> bindListener, OnItemClickListener<T> clickListener) {
        this.dataList = dataList != null ? dataList : new ArrayList<>();
        this.layoutResId = layoutResId;
        this.bindListener = bindListener;
        this.clickListener = clickListener;
    }

    public void updateData(List<T> newData) {
        this.dataList.clear();
        this.dataList.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new GenericViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericViewHolder holder, int position) {
        T item = dataList.get(position);
        if (bindListener != null) {
            bindListener.onBind(holder.itemView, item);
        }
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class GenericViewHolder extends RecyclerView.ViewHolder {
        public GenericViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
