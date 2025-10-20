package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class for currency formatting and operations
 */
public class CurrencyUtils {
    
    private static final String TAG = "CurrencyUtils";
    
    // Default currency settings
    private static final String DEFAULT_CURRENCY_CODE = "SAR";
    private static final String DEFAULT_CURRENCY_SYMBOL = "ر.س";
    private static final int DEFAULT_DECIMAL_PLACES = 2;
    
    // Preferences keys
    private static final String PREF_CURRENCY_CODE = "currency_code";
    private static final String PREF_CURRENCY_SYMBOL = "currency_symbol";
    private static final String PREF_DECIMAL_PLACES = "decimal_places";
    private static final String PREF_USE_THOUSANDS_SEPARATOR = "use_thousands_separator";
    private static final String PREF_CURRENCY_POSITION = "currency_position"; // before, after
    
    // Supported currencies
    private static final Map<String, CurrencyInfo> SUPPORTED_CURRENCIES = new HashMap<>();
    
    static {
        // Middle East & North Africa
        SUPPORTED_CURRENCIES.put("SAR", new CurrencyInfo("SAR", "ر.س", "ريال سعودي", 2));
        SUPPORTED_CURRENCIES.put("AED", new CurrencyInfo("AED", "د.إ", "درهم إماراتي", 2));
        SUPPORTED_CURRENCIES.put("KWD", new CurrencyInfo("KWD", "د.ك", "دينار كويتي", 3));
        SUPPORTED_CURRENCIES.put("QAR", new CurrencyInfo("QAR", "ر.ق", "ريال قطري", 2));
        SUPPORTED_CURRENCIES.put("BHD", new CurrencyInfo("BHD", "د.ب", "دينار بحريني", 3));
        SUPPORTED_CURRENCIES.put("OMR", new CurrencyInfo("OMR", "ر.ع", "ريال عماني", 3));
        SUPPORTED_CURRENCIES.put("JOD", new CurrencyInfo("JOD", "د.أ", "دينار أردني", 3));
        SUPPORTED_CURRENCIES.put("LBP", new CurrencyInfo("LBP", "ل.ل", "ليرة لبنانية", 2));
        SUPPORTED_CURRENCIES.put("EGP", new CurrencyInfo("EGP", "ج.م", "جنيه مصري", 2));
        SUPPORTED_CURRENCIES.put("MAD", new CurrencyInfo("MAD", "د.م", "درهم مغربي", 2));
        SUPPORTED_CURRENCIES.put("TND", new CurrencyInfo("TND", "د.ت", "دينار تونسي", 3));
        SUPPORTED_CURRENCIES.put("DZD", new CurrencyInfo("DZD", "د.ج", "دينار جزائري", 2));
        
        // International
        SUPPORTED_CURRENCIES.put("USD", new CurrencyInfo("USD", "$", "دولار أمريكي", 2));
        SUPPORTED_CURRENCIES.put("EUR", new CurrencyInfo("EUR", "€", "يورو", 2));
        SUPPORTED_CURRENCIES.put("GBP", new CurrencyInfo("GBP", "£", "جنيه إسترليني", 2));
        SUPPORTED_CURRENCIES.put("JPY", new CurrencyInfo("JPY", "¥", "ين ياباني", 0));
        SUPPORTED_CURRENCIES.put("CNY", new CurrencyInfo("CNY", "¥", "يوان صيني", 2));
        SUPPORTED_CURRENCIES.put("INR", new CurrencyInfo("INR", "₹", "روبية هندية", 2));
        SUPPORTED_CURRENCIES.put("PKR", new CurrencyInfo("PKR", "₨", "روبية باكستانية", 2));
        SUPPORTED_CURRENCIES.put("TRY", new CurrencyInfo("TRY", "₺", "ليرة تركية", 2));
    }
    
    private static Context appContext;
    private static SharedPreferences preferences;
    
    /**
     * Initialize with application context
     */
    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
        preferences = appContext.getSharedPreferences("currency_preferences", Context.MODE_PRIVATE);
    }
    
    /**
     * Format amount with default currency settings
     */
    public static String formatAmount(double amount) {
        if (preferences == null) {
            return formatAmountSimple(amount, DEFAULT_CURRENCY_SYMBOL, DEFAULT_DECIMAL_PLACES);
        }
        
        String currencyCode = preferences.getString(PREF_CURRENCY_CODE, DEFAULT_CURRENCY_CODE);
        String currencySymbol = preferences.getString(PREF_CURRENCY_SYMBOL, DEFAULT_CURRENCY_SYMBOL);
        int decimalPlaces = preferences.getInt(PREF_DECIMAL_PLACES, DEFAULT_DECIMAL_PLACES);
        boolean useThousandsSeparator = preferences.getBoolean(PREF_USE_THOUSANDS_SEPARATOR, true);
        String currencyPosition = preferences.getString(PREF_CURRENCY_POSITION, "after");
        
        return formatAmount(amount, currencySymbol, decimalPlaces, useThousandsSeparator, currencyPosition);
    }
    
    /**
     * Format amount with specific currency code
     */
    public static String formatAmount(double amount, String currencyCode) {
        CurrencyInfo currencyInfo = SUPPORTED_CURRENCIES.get(currencyCode);
        if (currencyInfo == null) {
            return formatAmountSimple(amount, currencyCode, DEFAULT_DECIMAL_PLACES);
        }
        
        return formatAmount(amount, currencyInfo.symbol, currencyInfo.decimalPlaces, true, "after");
    }
    
    /**
     * Format amount with full customization
     */
    public static String formatAmount(double amount, String currencySymbol, int decimalPlaces, 
                                    boolean useThousandsSeparator, String currencyPosition) {
        try {
            // Create decimal format with Arabic locale for RTL support
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("ar"));
            symbols.setDecimalSeparator('.');
            symbols.setGroupingSeparator(',');
            
            // Build pattern
            StringBuilder pattern = new StringBuilder();
            if (useThousandsSeparator) {
                pattern.append("#,##0");
            } else {
                pattern.append("0");
            }
            
            if (decimalPlaces > 0) {
                pattern.append(".");
                for (int i = 0; i < decimalPlaces; i++) {
                    pattern.append("0");
                }
            }
            
            DecimalFormat formatter = new DecimalFormat(pattern.toString(), symbols);
            String formattedAmount = formatter.format(amount);
            
            // Add currency symbol
            if ("before".equals(currencyPosition)) {
                return currencySymbol + " " + formattedAmount;
            } else {
                return formattedAmount + " " + currencySymbol;
            }
            
        } catch (Exception e) {
            return formatAmountSimple(amount, currencySymbol, decimalPlaces);
        }
    }
    
    /**
     * Simple amount formatting fallback
     */
    private static String formatAmountSimple(double amount, String currencySymbol, int decimalPlaces) {
        String format = "%." + decimalPlaces + "f";
        return String.format(format, amount) + " " + currencySymbol;
    }
    
    /**
     * Parse amount from formatted string
     */
    public static double parseAmount(String formattedAmount) {
        if (formattedAmount == null || formattedAmount.trim().isEmpty()) {
            return 0.0;
        }
        
        try {
            // Remove currency symbols and spaces
            String cleanAmount = formattedAmount.replaceAll("[^0-9.,-]", "");
            
            // Handle comma as decimal separator (European style)
            if (cleanAmount.contains(",") && cleanAmount.contains(".")) {
                // Assume comma is thousands separator
                cleanAmount = cleanAmount.replace(",", "");
            } else if (cleanAmount.contains(",") && !cleanAmount.contains(".")) {
                // Check if comma is decimal separator
                int commaIndex = cleanAmount.lastIndexOf(",");
                int digitAfterComma = cleanAmount.length() - commaIndex - 1;
                if (digitAfterComma <= 3) {
                    // Likely decimal separator
                    cleanAmount = cleanAmount.replace(",", ".");
                } else {
                    // Likely thousands separator
                    cleanAmount = cleanAmount.replace(",", "");
                }
            }
            
            return Double.parseDouble(cleanAmount);
            
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Format amount for display in different contexts
     */
    public static String formatForDisplay(double amount, DisplayContext context) {
        switch (context) {
            case COMPACT:
                return formatCompact(amount);
            case ACCOUNTING:
                return formatAccounting(amount);
            case INVOICE:
                return formatInvoice(amount);
            case SUMMARY:
                return formatSummary(amount);
            default:
                return formatAmount(amount);
        }
    }
    
    /**
     * Compact formatting (1.2K, 1.5M, etc.)
     */
    private static String formatCompact(double amount) {
        String currencySymbol = getCurrentCurrencySymbol();
        
        if (Math.abs(amount) >= 1_000_000_000) {
            return String.format("%.1fB %s", amount / 1_000_000_000, currencySymbol);
        } else if (Math.abs(amount) >= 1_000_000) {
            return String.format("%.1fM %s", amount / 1_000_000, currencySymbol);
        } else if (Math.abs(amount) >= 1_000) {
            return String.format("%.1fK %s", amount / 1_000, currencySymbol);
        } else {
            return formatAmount(amount);
        }
    }
    
    /**
     * Accounting format (negative in parentheses)
     */
    private static String formatAccounting(double amount) {
        if (amount < 0) {
            return "(" + formatAmount(Math.abs(amount)) + ")";
        } else {
            return formatAmount(amount);
        }
    }
    
    /**
     * Invoice formatting (more formal)
     */
    private static String formatInvoice(double amount) {
        return formatAmount(amount, getCurrentCurrencySymbol(), getCurrentDecimalPlaces(), true, "after");
    }
    
    /**
     * Summary formatting (bold/emphasized)
     */
    private static String formatSummary(double amount) {
        return "المجموع: " + formatAmount(amount);
    }
    
    /**
     * Get current currency symbol
     */
    public static String getCurrentCurrencySymbol() {
        if (preferences == null) {
            return DEFAULT_CURRENCY_SYMBOL;
        }
        return preferences.getString(PREF_CURRENCY_SYMBOL, DEFAULT_CURRENCY_SYMBOL);
    }
    
    /**
     * Get current currency code
     */
    public static String getCurrentCurrencyCode() {
        if (preferences == null) {
            return DEFAULT_CURRENCY_CODE;
        }
        return preferences.getString(PREF_CURRENCY_CODE, DEFAULT_CURRENCY_CODE);
    }
    
    /**
     * Get current decimal places
     */
    public static int getCurrentDecimalPlaces() {
        if (preferences == null) {
            return DEFAULT_DECIMAL_PLACES;
        }
        return preferences.getInt(PREF_DECIMAL_PLACES, DEFAULT_DECIMAL_PLACES);
    }
    
    /**
     * Set currency settings
     */
    public static void setCurrency(String currencyCode) {
        if (preferences == null) return;
        
        CurrencyInfo currencyInfo = SUPPORTED_CURRENCIES.get(currencyCode);
        if (currencyInfo != null) {
            preferences.edit()
                    .putString(PREF_CURRENCY_CODE, currencyCode)
                    .putString(PREF_CURRENCY_SYMBOL, currencyInfo.symbol)
                    .putInt(PREF_DECIMAL_PLACES, currencyInfo.decimalPlaces)
                    .apply();
        }
    }
    
    /**
     * Set custom currency symbol
     */
    public static void setCurrencySymbol(String symbol) {
        if (preferences == null) return;
        preferences.edit().putString(PREF_CURRENCY_SYMBOL, symbol).apply();
    }
    
    /**
     * Set decimal places
     */
    public static void setDecimalPlaces(int decimalPlaces) {
        if (preferences == null) return;
        preferences.edit().putInt(PREF_DECIMAL_PLACES, Math.max(0, Math.min(6, decimalPlaces))).apply();
    }
    
    /**
     * Set thousands separator usage
     */
    public static void setUseThousandsSeparator(boolean use) {
        if (preferences == null) return;
        preferences.edit().putBoolean(PREF_USE_THOUSANDS_SEPARATOR, use).apply();
    }
    
    /**
     * Set currency position
     */
    public static void setCurrencyPosition(String position) {
        if (preferences == null) return;
        if ("before".equals(position) || "after".equals(position)) {
            preferences.edit().putString(PREF_CURRENCY_POSITION, position).apply();
        }
    }
    
    /**
     * Get supported currencies
     */
    public static Map<String, CurrencyInfo> getSupportedCurrencies() {
        return new HashMap<>(SUPPORTED_CURRENCIES);
    }
    
    /**
     * Get currency info by code
     */
    public static CurrencyInfo getCurrencyInfo(String currencyCode) {
        return SUPPORTED_CURRENCIES.get(currencyCode);
    }
    
    /**
     * Convert amount between currencies (requires exchange rates)
     */
    public static double convertCurrency(double amount, String fromCurrency, String toCurrency, double exchangeRate) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        return amount * exchangeRate;
    }
    
    /**
     * Calculate percentage
     */
    public static double calculatePercentage(double amount, double percentage) {
        return amount * (percentage / 100.0);
    }
    
    /**
     * Calculate tax amount
     */
    public static double calculateTax(double amount, double taxRate) {
        return calculatePercentage(amount, taxRate);
    }
    
    /**
     * Calculate discount amount
     */
    public static double calculateDiscount(double amount, double discountRate) {
        return calculatePercentage(amount, discountRate);
    }
    
    /**
     * Calculate amount after tax
     */
    public static double addTax(double amount, double taxRate) {
        return amount + calculateTax(amount, taxRate);
    }
    
    /**
     * Calculate amount after discount
     */
    public static double applyDiscount(double amount, double discountRate) {
        return amount - calculateDiscount(amount, discountRate);
    }
    
    /**
     * Round amount to currency precision
     */
    public static double roundToCurrencyPrecision(double amount) {
        int decimalPlaces = getCurrentDecimalPlaces();
        double multiplier = Math.pow(10, decimalPlaces);
        return Math.round(amount * multiplier) / multiplier;
    }
    
    /**
     * Check if amount is valid
     */
    public static boolean isValidAmount(String amountString) {
        try {
            double amount = parseAmount(amountString);
            return amount >= 0 && !Double.isNaN(amount) && !Double.isInfinite(amount);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Format percentage
     */
    public static String formatPercentage(double percentage) {
        return String.format("%.2f%%", percentage);
    }
    
    /**
     * Currency information class
     */
    public static class CurrencyInfo {
        public final String code;
        public final String symbol;
        public final String nameArabic;
        public final int decimalPlaces;
        
        public CurrencyInfo(String code, String symbol, String nameArabic, int decimalPlaces) {
            this.code = code;
            this.symbol = symbol;
            this.nameArabic = nameArabic;
            this.decimalPlaces = decimalPlaces;
        }
        
        @Override
        public String toString() {
            return nameArabic + " (" + symbol + ")";
        }
    }
    
    /**
     * Display context enumeration
     */
    public enum DisplayContext {
        COMPACT,     // Compact format (K, M, B)
        ACCOUNTING,  // Accounting format (negative in parentheses)
        INVOICE,     // Formal invoice format
        SUMMARY,     // Summary format with label
        STANDARD     // Standard format
    }
}