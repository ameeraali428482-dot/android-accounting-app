package com.example.androidapp.ui.reports;

public class ReportTableAdapter {
    
    public interface OnColumnHeaderClickListener {
        void onColumnHeaderClick(int columnIndex, String columnName);
    }
    
    // باقي الكلاس كما هو موجود مسبقاً
    private OnColumnHeaderClickListener columnHeaderClickListener;
    
    public void setOnColumnHeaderClickListener(OnColumnHeaderClickListener listener) {
        this.columnHeaderClickListener = listener;
    }
}
