package com.example.androidapp.utils;

import android.content.Context;
import android.content.Intent;
import com.example.androidapp.ui.ai.AIChatActivity;

/**
 * مساعد لتشغيل المحادثة مع الذكاء الاصطناعي
 * AI Chat Launcher utility class
 */
public class AIChatLauncher {

    /**
     * فتح شاشة المحادثة مع الذكاء الاصطناعي
     * Open AI chat screen
     */
    public static void openAIChat(Context context) {
        Intent intent = new Intent(context, AIChatActivity.class);
        context.startActivity(intent);
    }

    /**
     * فتح شاشة المحادثة مع نوع محادثة محدد
     * Open AI chat with specific conversation type
     */
    public static void openAIChatWithType(Context context, String conversationType) {
        Intent intent = new Intent(context, AIChatActivity.class);
        intent.putExtra("conversation_type", conversationType);
        context.startActivity(intent);
    }

    /**
     * فتح شاشة التحليل المتقدم (للمدراء فقط)
     * Open advanced analysis (for admins only)
     */
    public static void openAdvancedAnalysis(Context context) {
        Intent intent = new Intent(context, AIChatActivity.class);
        intent.putExtra("conversation_type", "ACCOUNTING_ANALYSIS");
        intent.putExtra("show_analysis_options", true);
        context.startActivity(intent);
    }

    // أنواع المحادثات المتاحة
    public static final String GENERAL_CHAT = "GENERAL_CHAT";
    public static final String ACCOUNTING_ANALYSIS = "ACCOUNTING_ANALYSIS";
    public static final String DATA_INSIGHTS = "DATA_INSIGHTS";
    public static final String FINANCIAL_ADVICE = "FINANCIAL_ADVICE";
}