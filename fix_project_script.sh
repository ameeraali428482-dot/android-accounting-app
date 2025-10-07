# الصق الكود الذي أرسلته لك هنا، بدءًا من #!/bin/bash وحتى EOF الأخير.
#!/bin/bash

# هذا السكريبت يقوم بتصحيح أخطاء package و import statements وإضافة الموارد الناقصة في مشروع Android.
# يرجى التأكد من تشغيل هذا السكريبت في الجذر الرئيسي لمشروعك (حيث يوجد مجلد 'android-accounting-app').

PROJECT_ROOT="android-accounting-app"

if [ ! -d "$PROJECT_ROOT" ]; then
    echo "خطأ: لم يتم العثور على مجلد المشروع '$PROJECT_ROOT'. يرجى التأكد من تشغيل السكريبت في الجذر الصحيح للمشروع."
    exit 1
fi

echo "بدء تصحيح أخطاء المشروع..."

# 1. تصحيح package statements في جميع ملفات الكيانات (Entities)
echo "تصحيح package statements لملفات الكيانات..."
find "$PROJECT_ROOT/app/src/main/java/com/example/androidapp/data/entities" -name "*.java" -exec sed -i '1s/^package .*;$/package com.example.androidapp.data.entities;/' {} \;

# 2. تصحيح package statements في جميع ملفات DAO
echo "تصحيح package statements لملفات DAO..."
find "$PROJECT_ROOT/app/src/main/java/com/example/androidapp/data/dao" -name "*.java" -exec sed -i '1s/^package .*;$/package com.example.androidapp.data.dao;/' {} \;

# 3. تصحيح جمل import في PermissionManager.java
echo "تصحيح import statements في PermissionManager.java..."
sed -i 's/import com.example.androidapp.models./import com.example.androidapp.data.entities./g' "$PROJECT_ROOT/app/src/main/java/com/example/androidapp/utils/PermissionManager.java"

# 4. تصحيح جمل import في جميع ملفات DAO (للتأكد من استخدام data.entities)
echo "تصحيح import statements في ملفات DAO..."
find "$PROJECT_ROOT/app/src/main/java/com/example/androidapp/data/dao" -name "*.java" -exec sed -i 's/import com.example.androidapp.models./import com.example.androidapp.data.entities./g' {} \;

# 5. إضافة أيقونة الإشعارات ic_notification.xml الناقصة
echo "إضافة ملف ic_notification.xml..."
DRAWABLE_DIR="$PROJECT_ROOT/app/src/main/res/drawable"
mkdir -p "$DRAWABLE_DIR"
cat <<EOF > "$DRAWABLE_DIR/ic_notification.xml"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24.0"
    android:viewportHeight="24.0">
    <path
        android:fillColor="#FF000000"
        android:pathData="M12,22c1.1,0 2,-0.9 2,-2h-4c0,1.1 0.9,2 2,2zM18,16v-5c0,-3.07 -1.63,-5.64 -4.5,-6.32L13.5,4c0,-0.83 -0.67,-1.5 -1.5,-1.5S10.5,3.17 10.5,4l0,0.68C7.63,5.36 6,7.93 6,11v5l-2,2v1h16v-1l-2,-2z"/>
</vector>
