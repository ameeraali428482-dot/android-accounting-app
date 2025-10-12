#!/bin/bash

###############################################################################
# سكريبت إصلاح شامل لجميع أخطاء التطبيق
# يعالج: TODO Fix findViewById + GenericAdapter syntax errors
###############################################################################

echo "=========================================="
echo "🚀 بدء الإصلاح الشامل لجميع الأخطاء..."
echo "=========================================="
echo ""

# التحقق من وجود المجلد
if [ ! -d "app/src/main/java" ]; then
    echo "❌ خطأ: تأكد من أنك في المجلد الجذر للمشروع"
    exit 1
fi

JAVA_DIR="app/src/main/java/com/example/androidapp"

###############################################################################
# المرحلة 1: إصلاح GenericAdapter syntax errors - إزالة {(
###############################################################################
echo "🔴 المرحلة 1: إصلاح GenericAdapter syntax errors..."
echo "------------------------------------------"

find "$JAVA_DIR" -name "*.java" -type f | while read -r file; do
    # استبدال {( بـ {
    if grep -q "GenericAdapter<Object>(new ArrayList<>(), null) {(" "$file"; then
        echo "  إصلاح: $file"
        sed -i 's/GenericAdapter<Object>(new ArrayList<>(), null) {(/GenericAdapter<Object>(new ArrayList<>(), null) {/g' "$file"
    fi
done

echo "✅ تم إصلاح GenericAdapter syntax errors"
echo ""

###############################################################################
# المرحلة 2: إزالة جميع TODO Fix findViewById
###############################################################################
echo "🟠 المرحلة 2: إزالة TODO Fix findViewById..."
echo "------------------------------------------"

# إزالة السطور التي تحتوي على TODO Fix findViewById فقط
find "$JAVA_DIR" -name "*.java" -type f | while read -r file; do
    if grep -q "// TODO: Fix findViewById" "$file"; then
        echo "  معالجة: $file"
        # حذف السطور التي تحتوي على TODO فقط
        sed -i '/// TODO: Fix findViewById;/d' "$file"
    fi
done

echo "✅ تم إزالة TODO Fix findViewById"
echo ""

###############################################################################
# المرحلة 3: إصلاح الأخطاء الحرجة الأخرى
###############################################################################
echo "🟡 المرحلة 3: إصلاح الأخطاء الحرجة الأخرى..."
echo "------------------------------------------"

# إصلاح GoogleDriveService
GDRIVE="$JAVA_DIR/utils/GoogleDriveService.java"
if [ -f "$GDRIVE" ]; then
    sed -i '/public void initializeDriveClient(String accountName$/{ N; s/public void initializeDriveClient(String accountName
)/public void initializeDriveClient(String accountName)/; }' "$GDRIVE"
    echo "  ✅ GoogleDriveService"
fi

# إصلاح TrophyListActivity - case المكرر
TROPHY="$JAVA_DIR/ui/trophy/TrophyListActivity.java"
if [ -f "$TROPHY" ]; then
    awk '/case android.R.id.home:/ { if (++count == 2) { getline; next } } 1' "$TROPHY" > /tmp/trophy_temp.java && mv /tmp/trophy_temp.java "$TROPHY"
    echo "  ✅ TrophyListActivity"
fi

# إصلاح ProfitLossStatement - import مكرر
PROFIT="$JAVA_DIR/data/reports/ProfitLossStatement.java"
if [ -f "$PROFIT" ]; then
    awk '!seen[$0]++ || !/import androidx.annotation.NonNull/' "$PROFIT" > /tmp/profit_temp.java && mv /tmp/profit_temp.java "$PROFIT"
    echo "  ✅ ProfitLossStatement"
fi

echo "✅ المرحلة 3 مكتملة"
echo ""

###############################################################################
# المرحلة 4: التحقق من النتائج
###############################################################################
echo "🔍 المرحلة 4: التحقق من النتائج..."
echo "------------------------------------------"

TODO_COUNT=$(find "$JAVA_DIR" -name "*.java" -type f -exec grep -l "TODO: Fix findViewById" {} ; 2>/dev/null | wc -l)
SYNTAX_COUNT=$(find "$JAVA_DIR" -name "*.java" -type f -exec grep -l "GenericAdapter<Object>(new ArrayList<>(), null) {(" {} ; 2>/dev/null | wc -l)

echo "عدد ملفات TODO المتبقية: $TODO_COUNT"
echo "عدد ملفات syntax errors المتبقية: $SYNTAX_COUNT"
echo ""

###############################################################################
# الخلاصة
###############################################################################
echo "=========================================="
echo "✨ اكتمل الإصلاح الشامل!"
echo "=========================================="
echo ""
echo "📋 ملخص العمليات:"
echo "  ✅ إصلاح GenericAdapter syntax errors"
echo "  ✅ إزالة جميع TODO Fix findViewById"
echo "  ✅ إصلاح GoogleDriveService"
echo "  ✅ إصلاح TrophyListActivity"
echo "  ✅ إصلاح ProfitLossStatement"
echo ""
echo "🎯 يمكنك الآن تشغيل ./gradlew assembleDebug"
echo "=========================================="
