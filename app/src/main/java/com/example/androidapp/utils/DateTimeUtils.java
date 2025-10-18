package com.example.androidapp.utils;

import android.text.format.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility class for date and time operations
 */
public class DateTimeUtils {
    
    private static final String TAG = "DateTimeUtils";
    
    // Common date formats
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String FORMAT_DISPLAY_DATE = "dd/MM/yyyy";
    public static final String FORMAT_DISPLAY_DATE_TIME = "dd/MM/yyyy HH:mm";
    public static final String FORMAT_DISPLAY_TIME = "HH:mm";
    public static final String FORMAT_ARABIC_DATE = "dd MMMM yyyy";
    public static final String FORMAT_ARABIC_DATE_TIME = "dd MMMM yyyy - HH:mm";
    
    // Arabic locale for formatting
    private static final Locale ARABIC_LOCALE = new Locale("ar");
    private static final Locale ENGLISH_LOCALE = Locale.ENGLISH;
    
    /**
     * Get current timestamp as string
     */
    public static String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    /**
     * Get current date/time formatted
     */
    public static String getCurrentDateTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, ENGLISH_LOCALE);
        return sdf.format(new Date());
    }
    
    /**
     * Format timestamp to readable date
     */
    public static String formatDate(String timestamp) {
        return formatDate(timestamp, FORMAT_DISPLAY_DATE);
    }
    
    /**
     * Format timestamp to readable date with custom format
     */
    public static String formatDate(String timestamp, String format) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "";
        }
        
        try {
            long time = Long.parseLong(timestamp);
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat(format, ARABIC_LOCALE);
            return sdf.format(date);
        } catch (NumberFormatException e) {
            // Try parsing as date string
            return parseAndFormat(timestamp, format);
        }
    }
    
    /**
     * Format timestamp to readable date and time
     */
    public static String formatDateTime(String timestamp) {
        return formatDate(timestamp, FORMAT_DISPLAY_DATE_TIME);
    }
    
    /**
     * Format timestamp to Arabic date format
     */
    public static String formatArabicDate(String timestamp) {
        return formatDate(timestamp, FORMAT_ARABIC_DATE);
    }
    
    /**
     * Format timestamp to Arabic date and time format
     */
    public static String formatArabicDateTime(String timestamp) {
        return formatDate(timestamp, FORMAT_ARABIC_DATE_TIME);
    }
    
    /**
     * Parse date string and format to another format
     */
    private static String parseAndFormat(String dateString, String outputFormat) {
        String[] inputFormats = {
            FORMAT_DATE_TIME,
            FORMAT_DATE,
            FORMAT_DISPLAY_DATE,
            FORMAT_DISPLAY_DATE_TIME,
            "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss",
            "dd-MM-yyyy",
            "dd-MM-yyyy HH:mm:ss"
        };
        
        for (String inputFormat : inputFormats) {
            try {
                SimpleDateFormat inputSdf = new SimpleDateFormat(inputFormat, ENGLISH_LOCALE);
                Date date = inputSdf.parse(dateString);
                SimpleDateFormat outputSdf = new SimpleDateFormat(outputFormat, ARABIC_LOCALE);
                return outputSdf.format(date);
            } catch (ParseException e) {
                // Try next format
            }
        }
        
        return dateString; // Return original if parsing fails
    }
    
    /**
     * Get relative time (منذ ساعة، منذ يوم، إلخ)
     */
    public static String getRelativeTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "";
        }
        
        try {
            long time = Long.parseLong(timestamp);
            long now = System.currentTimeMillis();
            long diff = now - time;
            
            if (diff < 0) {
                return "في المستقبل";
            }
            
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long weeks = days / 7;
            long months = days / 30;
            long years = days / 365;
            
            if (years > 0) {
                return "منذ " + years + " " + (years == 1 ? "سنة" : "سنوات");
            } else if (months > 0) {
                return "منذ " + months + " " + (months == 1 ? "شهر" : "أشهر");
            } else if (weeks > 0) {
                return "منذ " + weeks + " " + (weeks == 1 ? "أسبوع" : "أسابيع");
            } else if (days > 0) {
                return "منذ " + days + " " + (days == 1 ? "يوم" : "أيام");
            } else if (hours > 0) {
                return "منذ " + hours + " " + (hours == 1 ? "ساعة" : "ساعات");
            } else if (minutes > 0) {
                return "منذ " + minutes + " " + (minutes == 1 ? "دقيقة" : "دقائق");
            } else {
                return "الآن";
            }
            
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
    
    /**
     * Check if date is today
     */
    public static boolean isToday(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return false;
        }
        
        try {
            long time = Long.parseLong(timestamp);
            Calendar today = Calendar.getInstance();
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(time);
            
            return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                   today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if date is yesterday
     */
    public static boolean isYesterday(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return false;
        }
        
        try {
            long time = Long.parseLong(timestamp);
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);
            
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(time);
            
            return yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                   yesterday.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Get formatted date with smart display (Today, Yesterday, or date)
     */
    public static String getSmartDateDisplay(String timestamp) {
        if (isToday(timestamp)) {
            return "اليوم";
        } else if (isYesterday(timestamp)) {
            return "أمس";
        } else {
            return formatArabicDate(timestamp);
        }
    }
    
    /**
     * Convert date string to timestamp
     */
    public static String dateToTimestamp(String dateString, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, ENGLISH_LOCALE);
            Date date = sdf.parse(dateString);
            return String.valueOf(date.getTime());
        } catch (ParseException e) {
            return getCurrentTimestamp();
        }
    }
    
    /**
     * Get start of day timestamp
     */
    public static String getStartOfDay(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return String.valueOf(calendar.getTimeInMillis());
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
    
    /**
     * Get end of day timestamp
     */
    public static String getEndOfDay(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return String.valueOf(calendar.getTimeInMillis());
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
    
    /**
     * Add days to timestamp
     */
    public static String addDays(String timestamp, int days) {
        try {
            long time = Long.parseLong(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.DAY_OF_YEAR, days);
            return String.valueOf(calendar.getTimeInMillis());
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
    
    /**
     * Add hours to timestamp
     */
    public static String addHours(String timestamp, int hours) {
        try {
            long time = Long.parseLong(timestamp);
            return String.valueOf(time + (hours * 60 * 60 * 1000));
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
    
    /**
     * Get week start (Sunday)
     */
    public static String getWeekStart(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            return getStartOfDay(String.valueOf(calendar.getTimeInMillis()));
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
    
    /**
     * Get week end (Saturday)
     */
    public static String getWeekEnd(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
            return getEndOfDay(String.valueOf(calendar.getTimeInMillis()));
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
    
    /**
     * Get month start
     */
    public static String getMonthStart(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return getStartOfDay(String.valueOf(calendar.getTimeInMillis()));
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
    
    /**
     * Get month end
     */
    public static String getMonthEnd(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            return getEndOfDay(String.valueOf(calendar.getTimeInMillis()));
        } catch (NumberFormatException e) {
            return timestamp;
        }
    }
    
    /**
     * Compare two timestamps
     * @return -1 if first is earlier, 0 if equal, 1 if first is later
     */
    public static int compareDates(String timestamp1, String timestamp2) {
        try {
            long time1 = Long.parseLong(timestamp1);
            long time2 = Long.parseLong(timestamp2);
            return Long.compare(time1, time2);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Get duration between two timestamps
     */
    public static String getDuration(String startTimestamp, String endTimestamp) {
        try {
            long start = Long.parseLong(startTimestamp);
            long end = Long.parseLong(endTimestamp);
            long diff = Math.abs(end - start);
            
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            
            if (days > 0) {
                return days + " أيام و " + (hours % 24) + " ساعات";
            } else if (hours > 0) {
                return hours + " ساعات و " + (minutes % 60) + " دقائق";
            } else if (minutes > 0) {
                return minutes + " دقائق";
            } else {
                return seconds + " ثواني";
            }
            
        } catch (NumberFormatException e) {
            return "غير محدد";
        }
    }
    
    /**
     * Get Arabic day name
     */
    public static String getArabicDayName(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            switch (dayOfWeek) {
                case Calendar.SUNDAY: return "الأحد";
                case Calendar.MONDAY: return "الإثنين";
                case Calendar.TUESDAY: return "الثلاثاء";
                case Calendar.WEDNESDAY: return "الأربعاء";
                case Calendar.THURSDAY: return "الخميس";
                case Calendar.FRIDAY: return "الجمعة";
                case Calendar.SATURDAY: return "السبت";
                default: return "";
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }
    
    /**
     * Get Arabic month name
     */
    public static String getArabicMonthName(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            
            int month = calendar.get(Calendar.MONTH);
            switch (month) {
                case Calendar.JANUARY: return "يناير";
                case Calendar.FEBRUARY: return "فبراير";
                case Calendar.MARCH: return "مارس";
                case Calendar.APRIL: return "أبريل";
                case Calendar.MAY: return "مايو";
                case Calendar.JUNE: return "يونيو";
                case Calendar.JULY: return "يوليو";
                case Calendar.AUGUST: return "أغسطس";
                case Calendar.SEPTEMBER: return "سبتمبر";
                case Calendar.OCTOBER: return "أكتوبر";
                case Calendar.NOVEMBER: return "نوفمبر";
                case Calendar.DECEMBER: return "ديسمبر";
                default: return "";
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }
    
    /**
     * Check if timestamp is in future
     */
    public static boolean isFuture(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            return time > System.currentTimeMillis();
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if timestamp is in past
     */
    public static boolean isPast(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            return time < System.currentTimeMillis();
        } catch (NumberFormatException e) {
            return false;
        }
    }
}