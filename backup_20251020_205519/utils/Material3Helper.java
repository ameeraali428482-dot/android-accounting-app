package com.example.androidapp.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.androidapp.R;

/**
 * مساعد Material Design 3 - يوفر تطبيق متسق لمبادئ Material Design 3
 * يضمن المظهر العصري والمتناسق عبر جميع أجزاء التطبيق
 */
public class Material3Helper {
    
    public static class Colors {
        public static final String PRIMARY = "#6750A4";
        public static final String ON_PRIMARY = "#FFFFFF";
        public static final String PRIMARY_CONTAINER = "#EADDFF";
        public static final String ON_PRIMARY_CONTAINER = "#21005D";
        
        public static final String SECONDARY = "#625B71";
        public static final String ON_SECONDARY = "#FFFFFF";
        public static final String SECONDARY_CONTAINER = "#E8DEF8";
        public static final String ON_SECONDARY_CONTAINER = "#1D192B";
        
        public static final String TERTIARY = "#7D5260";
        public static final String ON_TERTIARY = "#FFFFFF";
        public static final String TERTIARY_CONTAINER = "#FFD8E4";
        public static final String ON_TERTIARY_CONTAINER = "#31111D";
        
        public static final String ERROR = "#BA1A1A";
        public static final String ON_ERROR = "#FFFFFF";
        public static final String ERROR_CONTAINER = "#FFDAD6";
        public static final String ON_ERROR_CONTAINER = "#410002";
        
        public static final String SURFACE = "#FFFBFE";
        public static final String ON_SURFACE = "#1C1B1F";
        public static final String SURFACE_VARIANT = "#E7E0EC";
        public static final String ON_SURFACE_VARIANT = "#49454F";
        
        public static final String OUTLINE = "#79747E";
        public static final String OUTLINE_VARIANT = "#CAC4D0";
        public static final String SHADOW = "#000000";
        public static final String SURFACE_TINT = PRIMARY;
    }
    
    /**
     * تطبيق تصميم Material 3 على المكونات الأساسية
     */
    public static class Components {
        
        /**
         * إعداد MaterialButton مع تصميم Material 3
         */
        public static void setupMaterialButton(MaterialButton button, ButtonStyle style) {
            Context context = button.getContext();
            
            switch (style) {
                case FILLED:
                    button.setStyle(com.google.android.material.R.style.Widget_Material3_Button);
                    break;
                case OUTLINED:
                    button.setStyle(com.google.android.material.R.style.Widget_Material3_Button_OutlinedButton);
                    break;
                case TEXT:
                    button.setStyle(com.google.android.material.R.style.Widget_Material3_Button_TextButton);
                    break;
                case TONAL:
                    button.setStyle(com.google.android.material.R.style.Widget_Material3_Button_TonalButton);
                    break;
            }
            
            // تطبيق الخطوط والأبعاد
            button.setLetterSpacing(0.1f);
            button.setMinHeight(40);
            button.setCornerRadius(20);
            button.setElevation(0);
        }
        
        /**
         * إعداد TextInputLayout مع تصميم Material 3
         */
        public static void setupTextInputLayout(TextInputLayout textInputLayout, InputStyle style) {
            Context context = textInputLayout.getContext();
            
            switch (style) {
                case FILLED:
                    textInputLayout.setStyle(com.google.android.material.R.style.Widget_Material3_TextInputLayout_FilledBox);
                    break;
                case OUTLINED:
                    textInputLayout.setStyle(com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
                    break;
            }
            
            // تطبيق الألوان
            textInputLayout.setBoxCornerRadii(12, 12, 12, 12);
            textInputLayout.setHintAnimationEnabled(true);
        }
        
        /**
         * إعداد MaterialCardView مع تصميم Material 3
         */
        public static void setupMaterialCard(MaterialCardView cardView, CardStyle style) {
            Context context = cardView.getContext();
            
            cardView.setCardElevation(0);
            cardView.setRadius(12);
            
            switch (style) {
                case ELEVATED:
                    cardView.setCardElevation(6);
                    break;
                case FILLED:
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.background_light));
                    break;
                case OUTLINED:
                    cardView.setStrokeWidth(1);
                    cardView.setStrokeColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                    break;
            }
        }
        
        /**
         * إنشاء Chip مع تصميم Material 3
         */
        public static Chip createMaterial3Chip(Context context, String text, ChipType type) {
            Chip chip = new Chip(context);
            chip.setText(text);
            
            switch (type) {
                case ASSIST:
                    chip.setChipIcon(ContextCompat.getDrawable(context, R.drawable.ic_add));
                    break;
                case FILTER:
                    chip.setCheckable(true);
                    break;
                case INPUT:
                    chip.setCloseIconVisible(true);
                    break;
                case SUGGESTION:
                    chip.setClickable(true);
                    break;
            }
            
            chip.setChipCornerRadius(8);
            chip.setTextSize(14);
            return chip;
        }
        
        /**
         * إعداد FloatingActionButton مع تصميم Material 3
         */
        public static void setupFAB(FloatingActionButton fab, FABStyle style) {
            Context context = fab.getContext();
            
            switch (style) {
                case NORMAL:
                    fab.setSize(FloatingActionButton.SIZE_NORMAL);
                    break;
                case MINI:
                    fab.setSize(FloatingActionButton.SIZE_MINI);
                    break;
                case LARGE:
                    fab.setCustomSize(96);
                    break;
            }
            
            fab.setElevation(6);
            fab.setCompatElevation(6);
        }
    }
    
    /**
     * إنشاء مكونات مخصصة بتصميم Material 3
     */
    public static class CustomComponents {
        
        /**
         * إنشاء EditText مع دعم الإدخال الصوتي
         */
        public static LinearLayout createVoiceInputField(Context context, String hint, 
                SmartSuggestionsManager.SuggestionType suggestionType) {
            LinearLayout container = new LinearLayout(context);
            container.setOrientation(LinearLayout.HORIZONTAL);
            
            // TextInputLayout
            TextInputLayout textInputLayout = new TextInputLayout(context);
            textInputLayout.setHint(hint);
            Components.setupTextInputLayout(textInputLayout, InputStyle.OUTLINED);
            
            // EditText
            EditText editText = new EditText(context);
            editText.setHint(hint);
            textInputLayout.addView(editText);
            
            // Voice Input Button
            ImageButton voiceButton = new ImageButton(context);
            voiceButton.setImageResource(R.drawable.ic_send); // استخدام أيقونة مؤقتة
            voiceButton.setBackground(ContextCompat.getDrawable(context, R.drawable.send_button_background));
            voiceButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            voiceButton.setPadding(12, 12, 12, 12);
            
            // إضافة المكونات للحاوية
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                48, 48);
            buttonParams.setMargins(8, 0, 0, 0);
            
            container.addView(textInputLayout, textParams);
            container.addView(voiceButton, buttonParams);
            
            // ربط الاقتراحات الذكية
            SmartSuggestionsManager suggestionsManager = new SmartSuggestionsManager(context);
            suggestionsManager.attachSmartSuggestions(editText, suggestionType);
            
            // ربط الإدخال الصوتي
            VoiceInputManager voiceManager = new VoiceInputManager((android.app.Activity) context);
            voiceButton.setOnClickListener(v -> {
                voiceManager.startVoiceInput(editText, new VoiceInputManager.VoiceInputCallback() {
                    @Override
                    public void onVoiceInputResult(String result) {
                        editText.setText(result);
                    }
                    
                    @Override
                    public void onVoiceInputError(String error) {
                        // Handle error
                    }
                    
                    @Override
                    public void onVoiceInputStarted() {
                        voiceButton.setAlpha(0.5f);
                    }
                    
                    @Override
                    public void onVoiceInputStopped() {
                        voiceButton.setAlpha(1.0f);
                    }
                });
            });
            
            return container;
        }
        
        /**
         * إنشاء Card إحصائية مع تصميم Material 3
         */
        public static MaterialCardView createStatsCard(Context context, String title, 
                String value, String subtitle, int iconRes) {
            MaterialCardView card = new MaterialCardView(context);
            Components.setupMaterialCard(card, CardStyle.FILLED);
            
            // Layout داخلي
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(16, 16, 16, 16);
            
            // العنوان
            TextView titleView = new TextView(context);
            titleView.setText(title);
            titleView.setTextSize(14);
            titleView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            
            // القيمة
            TextView valueView = new TextView(context);
            valueView.setText(value);
            valueView.setTextSize(24);
            valueView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            valueView.setTypeface(valueView.getTypeface(), android.graphics.Typeface.BOLD);
            
            // العنوان الفرعي
            TextView subtitleView = new TextView(context);
            subtitleView.setText(subtitle);
            subtitleView.setTextSize(12);
            subtitleView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            
            layout.addView(titleView);
            layout.addView(valueView);
            layout.addView(subtitleView);
            card.addView(layout);
            
            return card;
        }
        
        /**
         * إنشاء شريط تنقل سفلي مع تصميم Material 3
         */
        public static ChipGroup createBottomNavigation(Context context, String[] items, int[] icons) {
            ChipGroup chipGroup = new ChipGroup(context);
            chipGroup.setSingleSelection(true);
            chipGroup.setSelectionRequired(true);
            
            for (int i = 0; i < items.length; i++) {
                Chip chip = createMaterial3Chip(context, items[i], ChipType.FILTER);
                if (icons != null && i < icons.length) {
                    chip.setChipIcon(ContextCompat.getDrawable(context, icons[i]));
                }
                chipGroup.addView(chip);
            }
            
            return chipGroup;
        }
    }
    
    /**
     * إعدادات الألوان والثيمات
     */
    public static class ThemeHelper {
        
        public static void applyMaterial3Theme(Context context, View rootView) {
            // تطبيق ألوان Material 3 على العناصر
            rootView.setBackgroundColor(android.graphics.Color.parseColor(Colors.SURFACE));
        }
        
        public static ColorStateList getPrimaryColorStateList(Context context) {
            return ColorStateList.valueOf(android.graphics.Color.parseColor(Colors.PRIMARY));
        }
        
        public static ColorStateList getSecondaryColorStateList(Context context) {
            return ColorStateList.valueOf(android.graphics.Color.parseColor(Colors.SECONDARY));
        }
    }
    
    // Enums for styling
    public enum ButtonStyle {
        FILLED, OUTLINED, TEXT, TONAL
    }
    
    public enum InputStyle {
        FILLED, OUTLINED
    }
    
    public enum CardStyle {
        ELEVATED, FILLED, OUTLINED
    }
    
    public enum ChipType {
        ASSIST, FILTER, INPUT, SUGGESTION
    }
    
    public enum FABStyle {
        NORMAL, MINI, LARGE
    }
}