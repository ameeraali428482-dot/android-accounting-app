package com.example.androidapp.ui.ai;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.AIConversation;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * محول عرض محادثات الذكاء الاصطناعي
 * AI Chat Adapter for displaying conversations
 */
public class AIChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER_MESSAGE = 1;
    private static final int VIEW_TYPE_AI_MESSAGE = 2;
    
    private List<AIConversation> conversations;
    private Context context;
    private OnConversationActionListener actionListener;

    public interface OnConversationActionListener {
        void onRateConversation(String conversationId, int rating, String comment);
        void onDeleteConversation(String conversationId);
        void onBookmarkConversation(String conversationId);
        void onCopyText(String text);
        void onShareConversation(AIConversation conversation);
    }

    public AIChatAdapter(List<AIConversation> conversations, Context context) {
        this.conversations = conversations;
        this.context = context;
        
        // إذا كان السياق ينفذ الواجهة، استخدمه كمستمع
        if (context instanceof OnConversationActionListener) {
            this.actionListener = (OnConversationActionListener) context;
        }
    }

    public void setActionListener(OnConversationActionListener listener) {
        this.actionListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        // إنشاء عرض مزدوج: رسالة المستخدم ورد AI في نفس المحادثة
        return position % 2 == 0 ? VIEW_TYPE_USER_MESSAGE : VIEW_TYPE_AI_MESSAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        if (viewType == VIEW_TYPE_USER_MESSAGE) {
            View view = inflater.inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_ai_message, parent, false);
            return new AIMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AIConversation conversation = conversations.get(position / 2); // كل محادثة تحتوي على رسالة ورد
        
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(conversation);
        } else if (holder instanceof AIMessageViewHolder) {
            ((AIMessageViewHolder) holder).bind(conversation);
        }
    }

    @Override
    public int getItemCount() {
        return conversations.size() * 2; // كل محادثة = رسالة مستخدم + رد AI
    }

    /**
     * حامل عرض رسالة المستخدم
     */
    class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textTime;
        TextView textConversationType;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTime = itemView.findViewById(R.id.textTime);
            textConversationType = itemView.findViewById(R.id.textConversationType);
        }

        public void bind(AIConversation conversation) {
            textMessage.setText(conversation.getUserMessage());
            
            // عرض الوقت
            if (conversation.getCreatedAt() != null) {
                String timeText = DateUtils.getRelativeTimeSpanString(
                    conversation.getCreatedAt().getTime(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                ).toString();
                textTime.setText(timeText);
            }

            // عرض نوع المحادثة
            String typeDisplay = getConversationTypeDisplay(conversation.getConversationType());
            textConversationType.setText(typeDisplay);
            textConversationType.setVisibility(
                typeDisplay != null && !typeDisplay.isEmpty() ? View.VISIBLE : View.GONE
            );
        }
    }

    /**
     * حامل عرض رد الذكاء الاصطناعي
     */
    class AIMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textTime;
        TextView textConfidence;
        RatingBar ratingBar;
        Button buttonCopy;
        Button buttonShare;
        ImageButton buttonBookmark;
        ImageButton buttonDelete;
        LinearLayout layoutSuggestions;
        LinearLayout layoutActions;
        TextView textResponseTime;

        public AIMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTime = itemView.findViewById(R.id.textTime);
            textConfidence = itemView.findViewById(R.id.textConfidence);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            buttonCopy = itemView.findViewById(R.id.buttonCopy);
            buttonShare = itemView.findViewById(R.id.buttonShare);
            buttonBookmark = itemView.findViewById(R.id.buttonBookmark);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            layoutSuggestions = itemView.findViewById(R.id.layoutSuggestions);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            textResponseTime = itemView.findViewById(R.id.textResponseTime);
        }

        public void bind(AIConversation conversation) {
            textMessage.setText(conversation.getAiResponse());

            // عرض الوقت
            if (conversation.getCreatedAt() != null) {
                String timeText = DateUtils.getRelativeTimeSpanString(
                    conversation.getCreatedAt().getTime(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                ).toString();
                textTime.setText(timeText);
            }

            // عرض درجة الثقة
            if (conversation.getConfidenceScore() > 0) {
                textConfidence.setText(String.format(Locale.getDefault(), 
                    "درجة الثقة: %.1f%%", conversation.getConfidenceScore() * 100));
                textConfidence.setVisibility(View.VISIBLE);
            } else {
                textConfidence.setVisibility(View.GONE);
            }

            // عرض وقت الاستجابة
            if (conversation.getResponseTimeMs() > 0) {
                textResponseTime.setText(String.format(Locale.getDefault(),
                    "وقت الاستجابة: %d ميلي ثانية", conversation.getResponseTimeMs()));
                textResponseTime.setVisibility(View.VISIBLE);
            } else {
                textResponseTime.setVisibility(View.GONE);
            }

            // إعداد التقييم
            ratingBar.setRating(conversation.getFeedbackRating());
            ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                if (fromUser && actionListener != null) {
                    actionListener.onRateConversation(conversation.getId(), (int) rating, "");
                }
            });

            // عرض الاقتراحات
            displaySuggestions(conversation);

            // إعداد الأزرار
            setupActionButtons(conversation);
        }

        private void displaySuggestions(AIConversation conversation) {
            layoutSuggestions.removeAllViews();
            
            if (conversation.getSuggestions() != null && !conversation.getSuggestions().isEmpty()) {
                try {
                    JSONArray suggestions = new JSONArray(conversation.getSuggestions());
                    
                    for (int i = 0; i < suggestions.length() && i < 3; i++) { // أقصى 3 اقتراحات
                        String suggestion = suggestions.getString(i);
                        
                        Button suggestionButton = new Button(context);
                        suggestionButton.setText(suggestion);
                        suggestionButton.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        suggestionButton.setTextSize(12);
                        suggestionButton.setPadding(16, 8, 16, 8);
                        
                        // إضافة action للاقتراح
                        suggestionButton.setOnClickListener(v -> {
                            if (actionListener != null) {
                                actionListener.onCopyText(suggestion);
                            }
                        });
                        
                        layoutSuggestions.addView(suggestionButton);
                    }
                    
                    layoutSuggestions.setVisibility(View.VISIBLE);
                    
                } catch (JSONException e) {
                    layoutSuggestions.setVisibility(View.GONE);
                }
            } else {
                layoutSuggestions.setVisibility(View.GONE);
            }
        }

        private void setupActionButtons(AIConversation conversation) {
            // نسخ النص
            buttonCopy.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onCopyText(conversation.getAiResponse());
                }
            });

            // مشاركة المحادثة
            buttonShare.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onShareConversation(conversation);
                }
            });

            // إضافة/إزالة إشارة مرجعية
            buttonBookmark.setImageResource(
                conversation.isBookmarked() ? 
                R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border
            );
            buttonBookmark.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onBookmarkConversation(conversation.getId());
                }
            });

            // حذف المحادثة
            buttonDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteConversation(conversation.getId());
                }
            });
        }
    }

    /**
     * الحصول على عرض نوع المحادثة
     */
    private String getConversationTypeDisplay(String type) {
        if (type == null) return "";
        
        switch (type) {
            case "GENERAL_CHAT":
                return "محادثة عامة";
            case "ACCOUNTING_ANALYSIS":
                return "تحليل محاسبي";
            case "DATA_INSIGHTS":
                return "رؤى البيانات";
            case "FINANCIAL_ADVICE":
                return "استشارة مالية";
            case "FINANCIAL":
                return "تحليل مالي";
            case "TRENDS":
                return "تحليل الاتجاهات";
            case "PREDICTIONS":
                return "التوقعات";
            case "RECOMMENDATIONS":
                return "التوصيات";
            case "ANOMALIES":
                return "كشف الشذوذ";
            case "PERFORMANCE":
                return "تحليل الأداء";
            case "CASH_FLOW":
                return "التدفق النقدي";
            case "PROFITABILITY":
                return "تحليل الربحية";
            default:
                return type;
        }
    }

    /**
     * تحديث البيانات
     */
    public void updateConversations(List<AIConversation> newConversations) {
        this.conversations = newConversations;
        notifyDataSetChanged();
    }

    /**
     * إضافة محادثة جديدة
     */
    public void addConversation(AIConversation conversation) {
        conversations.add(conversation);
        notifyItemInserted((conversations.size() - 1) * 2);
        notifyItemInserted((conversations.size() - 1) * 2 + 1);
    }
}