#!/bin/bash

# إصلاح الأخطاء في ملفات الجافا
# تأكد من أنك في المجلد الجذر للمشروع

echo "🔧 بدء إصلاح الأخطاء..."

# 1. إصلاح GenericAdapter syntax error في BaseListActivity.java
echo "1️⃣ إصلاح GenericAdapter في BaseListActivity.java..."
sed -i 's/adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {(/adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {/g' app/src/main/java/com/example/androidapp/ui/common/BaseListActivity.java

# 2. إصلاح GenericAdapter في AdminUserListActivity.java
echo "2️⃣ إصلاح GenericAdapter في AdminUserListActivity.java..."
sed -i 's/adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {(/adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {/g' app/src/main/java/com/example/androidapp/ui/admin/AdminUserListActivity.java

# 3. إصلاح كسر السطر في GoogleDriveService.java
echo "3️⃣ إصلاح كسر السطر في GoogleDriveService.java..."
sed -i '/public void initializeDriveClient(String accountName/{ N; s/public void initializeDriveClient(String accountName
)/public void initializeDriveClient(String accountName)/; }' app/src/main/java/com/example/androidapp/utils/GoogleDriveService.java

# 4. إزالة case المكرر في TrophyListActivity.java
echo "4️⃣ إزالة case المكرر في TrophyListActivity.java..."
awk '/case android.R.id.home:/ { if (++count == 2) { getline; next } } 1' app/src/main/java/com/example/androidapp/ui/trophy/TrophyListActivity.java > /tmp/trophy_temp.java && mv /tmp/trophy_temp.java app/src/main/java/com/example/androidapp/ui/trophy/TrophyListActivity.java

# 5. إزالة import المكرر في ProfitLossStatement.java
echo "5️⃣ إزالة import المكرر في ProfitLossStatement.java..."
awk '!seen[$0]++ || !/import androidx.annotation.NonNull/' app/src/main/java/com/example/androidapp/data/reports/ProfitLossStatement.java > /tmp/profit_temp.java && mv /tmp/profit_temp.java app/src/main/java/com/example/androidapp/data/reports/ProfitLossStatement.java

echo "✅ تم إصلاح جميع الأخطاء الحرجة!"
echo ""
echo "⚠️  ملاحظة: لا تزال هناك TODO Fix findViewById في عدة ملفات"
echo "   يمكنك تشغيل المشروع الآن، لكن Views غير المربوطة قد تسبب NullPointerException"
