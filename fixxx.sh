#!/bin/bash

# ==============================================================================
#  السكريبت الشامل لتصحيح مشروع Android المحاسبي
# ==============================================================================
#
#  هذا السكريبت يقوم بتنفيذ سلسلة من التصحيحات لمعالجة الأخطاء الشائعة
#  التي تم تحديدها في سجلات البناء، بما في ذلك:
#    1. تعديل ملف app/build.gradle لتصحيح التبعيات.
#    2. تصحيح package و import statements في ملفات DAO والكيانات.
#    3. إنشاء ملفات الموارد المفقودة (ic_notification.xml).
#    4. تعديل ملفات التخطيط لإضافة الموارد المفقودة (Guidelines).
#
#  **تعليمات الاستخدام:**
#    1. تأكد من أنك في مجلد الجذر الرئيسي لمشروعك (حيث يوجد مجلد 'android-accounting-app').
#    2. قم بإنشاء ملف جديد باسم `fix_project_script.sh` والصق هذا المحتوى فيه.
#    3. قم بمنح أذونات التنفيذ: chmod +x fix_project_script.sh
#    4. قم بتشغيل السكريبت: ./fix_project_script.sh
#
# ==============================================================================

PROJECT_ROOT="android-accounting-app"

# التحقق من وجود مجلد المشروع
if [ ! -d "$PROJECT_ROOT" ]; then
    echo "[خطأ] لم يتم العثور على مجلد المشروع '$PROJECT_ROOT'. يرجى التأكد من تشغيل السكريبت في الجذر الصحيح للمشروع."
    exit 1
fi

echo "[INFO] بدء عملية تصحيح المشروع في '$PROJECT_ROOT'..."

# --- الخطوة 1: تصحيح ملف app/build.gradle --- 
echo "[INFO] الخطوة 1/4: تصحيح ملف app/build.gradle..."

BUILD_GRADLE_PATH="$PROJECT_ROOT/app/build.gradle"

if [ -f "$BUILD_GRADLE_PATH" ]; then
    # عمل نسخة احتياطية
    cp "$BUILD_GRADLE_PATH" "${BUILD_GRADLE_PATH}.bak"
    echo "[INFO] تم إنشاء نسخة احتياطية: ${BUILD_GRADLE_PATH}.bak"

    # إزالة التبعيات المكررة
    sed -i '/annotationProcessor "androidx.room:room-compiler:2.6.1"/d' "$BUILD_GRADLE_PATH"
    sed -i '/implementation .com.github.bumptech.glide:glide:4.16.0/d' "$BUILD_GRADLE_PATH"
    sed -i '/annotationProcessor .com.github.bumptech.glide:compiler:4.16.0/d' "$BUILD_GRADLE_PATH"

    # إضافة التبعيات الصحيحة والمفقودة
    # التحقق من عدم وجود القسم قبل إضافته لتجنب التكرار
    if ! grep -q "// --- Corrected Dependencies ---" "$BUILD_GRADLE_PATH"; then
        sed -i '/dependencies {/a \
    // --- Corrected Dependencies ---\
    implementation \u0027androidx.room:room-ktx:2.6.1\u0027\
    implementation \u0027org.apache.commons:commons-compress:1.24.1\u0027\
    implementation \u0027com.google.cloud:google-cloud-aiplatform:3.27.0\u0027\
    implementation \u0027com.github.bumptech.glide:glide:4.16.0\u0027\
    annotationProcessor \u0027com.github.bumptech.glide:compiler:4.16.0\u0027\
    implementation \u0027androidx.room:room-runtime:2.6.1\u0027\
    annotationProcessor \u0027androidx.room:room-compiler:2.6.1\u0027
' "$BUILD_GRADLE_PATH"
        echo "[SUCCESS] تم تعديل التبعيات في app/build.gradle."
    else
        echo "[WARN] يبدو أن التبعيات المصححة موجودة بالفعل. تم التخطي."
    fi
else
    echo "[ERROR] ملف app/build.gradle غير موجود! لا يمكن المتابعة."
    exit 1
fi

# --- الخطوة 2: تصحيح package و import statements --- 
echo "[INFO] الخطوة 2/4: تصحيح package و import statements..."

# تصحيح package statements في جميع ملفات الكيانات (Entities)
find "$PROJECT_ROOT/app/src/main/java/com/example/androidapp/data/entities" -name "*.java" -exec sed -i '1s/^package .*;$/package com.example.androidapp.data.entities;/' {} \;
echo "[INFO] تم تصحيح package statements لملفات الكيانات."

# تصحيح package statements في جميع ملفات DAO
find "$PROJECT_ROOT/app/src/main/java/com/example/androidapp/data/dao" -name "*.java" -exec sed -i '1s/^package .*;$/package com.example.androidapp.data.dao;/' {} \;
echo "[INFO] تم تصحيح package statements لملفات DAO."

# --- الخطوة 3: إنشاء الموارد المفقودة --- 
echo "[INFO] الخطوة 3/4: إنشاء الموارد المفقودة..."

DRAWABLE_DIR="$PROJECT_ROOT/app/src/main/res/drawable"
IC_NOTIFICATION_PATH="$DRAWABLE_DIR/ic_notification.xml"

# إنشاء مجلد drawable إذا لم يكن موجودًا
mkdir -p "$DRAWABLE_DIR"

# إنشاء ic_notification.xml إذا لم يكن موجودًا
if [ ! -f "$IC_NOTIFICATION_PATH" ]; then
    cat > "$IC_NOTIFICATION_PATH" << EOF
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24.0"
    android:viewportHeight="24.0">
    <path
        android:fillColor="#FF000000"
        android:pathData="M12,22c1.1,0 2,-0.9 2,-2h-4c0,1.1 0.9,2 2,2zM18,16v-5c0,-3.07 -1.63,-5.64 -4.5,-6.32L13.5,4c0,-0.83 -0.67,-1.5 -1.5,-1.5S10.5,3.17 10.5,4l0,0.68C7.63,5.36 6,7.93 6,11v5l-2,2v1h16v-1l-2,-2z"/>
</vector>
EOF
    echo "[SUCCESS] تم إنشاء ملف 'ic_notification.xml'."
else
    echo "[INFO] ملف 'ic_notification.xml' موجود بالفعل. تم التخطي."
fi

# --- الخطوة 4: تعديل ملفات التخطيط --- 
echo "[INFO] الخطوة 4/4: تعديل ملفات التخطيط..."

LAYOUT_DIR="$PROJECT_ROOT/app/src/main/res/layout"
ACTIVITY_MAIN_PATH="$LAYOUT_DIR/activity_main.xml"

if [ -f "$ACTIVITY_MAIN_PATH" ]; then
    # التحقق من عدم وجود Guideline قبل إضافتها
    if ! grep -q "android:id=\"@+id/guideline_top\"" "$ACTIVITY_MAIN_PATH"; then
        # عمل نسخة احتياطية
        cp "$ACTIVITY_MAIN_PATH" "${ACTIVITY_MAIN_PATH}.bak"
        # إضافة Guideline
        sed -i '/<androidx.constraintlayout.widget.ConstraintLayout/a \
        <androidx.constraintlayout.widget.Guideline\
            android:id="@+id/guideline_top"\
            android:layout_width="wrap_content"\
            android:layout_height="wrap_content"\
            android:orientation="horizontal"\
            app:layout_constraintGuide_percent="0.05" />\
\
        <androidx.constraintlayout.widget.Guideline\
            android:id="@+id/guideline_bottom"\
            android:layout_width="wrap_content"\
            android:layout_height="wrap_content"\
            android:orientation="horizontal"\
            app:layout_constraintGuide_percent="0.95" />' "$ACTIVITY_MAIN_PATH"
        echo "[SUCCESS] تم إضافة Guidelines إلى 'activity_main.xml'."
    else
        echo "[INFO] يبدو أن Guidelines موجودة بالفعل في 'activity_main.xml'. تم التخطي."
    fi
else
    echo "[WARN] ملف 'activity_main.xml' غير موجود. تم تخطي هذه الخطوة."
fi

echo "
[COMPLETED] اكتمل تشغيل السكريبت.

**الخطوات التالية الحاسمة:**
1.  **التحقق اليدوي:** افتح المشروع في Android Studio وقم بمراجعة الملفات التي تم تعديلها.
2.  **رفع التغييرات إلى Git:** قم برفع جميع التغييرات إلى مستودع Git الخاص بك.
3.  **إعادة البناء في CodeMagic:** قم بتشغيل بناء جديد في CodeMagic.
4.  **إرسال سجل البناء الجديد:** أرسل لي سجل البناء الكامل من CodeMagic لتحليل أي أخطاء متبقية.
"
