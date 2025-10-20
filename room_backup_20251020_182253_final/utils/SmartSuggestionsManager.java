package com.example.androidapp.utils;

public class SmartSuggestionsManager {
    
    public enum SuggestionType {
        PRODUCT,
        CUSTOMER, 
        SUPPLIER,
        CATEGORY,
        ACCOUNT,
        GENERAL
    }
    
    // باقي الكلاس كما هو موجود مسبقاً
    public void generateSuggestions(SuggestionType type, String context) {
        // implementation
    }
    
    public void saveSuggestionHistory(SuggestionType type, String query) {
        // implementation
    }
}
