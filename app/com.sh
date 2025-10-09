#!/bin/bash

echo "🔍 مقارنة متقدمة بين المجلدين الرئيسيين"
echo "=============================================="

# الانتقال للمجلد الصحيح
cd /data/data/com.termux/files/home/android-accounting-app/app

# إنشاء ملف التقرير
REPORT_FILE="detailed_comparison.txt"
echo "التقرير التفصيلي للمقارنة - $(date)" > $REPORT_FILE
echo "==============================================" >> $REPORT_FILE

# دالة للمقارنة التفصيلية
compare_main_folders() {
    local main1="src/main"
    local main2="src/src/main"
    
    echo "📊 مقارنة: $main1 vs $main2" | tee -a $REPORT_FILE
    echo "----------------------------------------" | tee -a $REPORT_FILE
    
    # 1. المقارنة الأساسية
    echo "📈 1. المقارنة الأساسية:" | tee -a $REPORT_FILE
    echo "   $main1:" | tee -a $REPORT_FILE
    if [ -d "$main1" ]; then
        size1=$(du -sh "$main1" 2>/dev/null | cut -f1)
        file_count1=$(find "$main1" -type f 2>/dev/null | wc -l)
        java_count1=$(find "$main1" -name "*.java" 2>/dev/null | wc -l)
        xml_count1=$(find "$main1" -name "*.xml" 2>/dev/null | wc -l)
        echo "     الحجم: $size1" | tee -a $REPORT_FILE
        echo "     إجمالي الملفات: $file_count1" | tee -a $REPORT_FILE
        echo "     ملفات Java: $java_count1" | tee -a $REPORT_FILE
        echo "     ملفات XML: $xml_count1" | tee -a $REPORT_FILE
    else
        echo "     ❌ المجلد غير موجود" | tee -a $REPORT_FILE
    fi
    
    echo "   $main2:" | tee -a $REPORT_FILE
    if [ -d "$main2" ]; then
        size2=$(du -sh "$main2" 2>/dev/null | cut -f1)
        file_count2=$(find "$main2" -type f 2>/dev/null | wc -l)
        java_count2=$(find "$main2" -name "*.java" 2>/dev/null | wc -l)
        xml_count2=$(find "$main2" -name "*.xml" 2>/dev/null | wc -l)
        echo "     الحجم: $size2" | tee -a $REPORT_FILE
        echo "     إجمالي الملفات: $file_count2" | tee -a $REPORT_FILE
        echo "     ملفات Java: $java_count2" | tee -a $REPORT_FILE
        echo "     ملفات XML: $xml_count2" | tee -a $REPORT_FILE
    else
        echo "     ❌ المجلد غير موجود" | tee -a $REPORT_FILE
    fi
    
    # 2. مقارنة التواريخ
    echo "" | tee -a $REPORT_FILE
    echo "🕒 2. مقارنة التواريخ:" | tee -a $REPORT_FILE
    echo "   $main1 - أحدث الملفات:" | tee -a $REPORT_FILE
    find "$main1" -type f -exec ls -lt {} + 2>/dev/null | head -5 | while read line; do
        echo "     $line" | tee -a $REPORT_FILE
    done
    
    echo "   $main2 - أحدث الملفات:" | tee -a $REPORT_FILE
    find "$main2" -type f -exec ls -lt {} + 2>/dev/null | head -5 | while read line; do
        echo "     $line" | tee -a $REPORT_FILE
    done
    
    # 3. مقارنة الهيكل
    echo "" | tee -a $REPORT_FILE
    echo "🏗️ 3. مقارنة الهيكل:" | tee -a $REPORT_FILE
    
    compare_structure() {
        local dir=$1
        local name=$2
        echo "   📁 $name:" | tee -a $REPORT_FILE
        
        # عد المجلدات الرئيسية
        folders=("java" "res" "AndroidManifest.xml")
        for folder in "${folders[@]}"; do
            if [ -e "$dir/$folder" ]; then
                if [ -d "$dir/$folder" ]; then
                    count=$(find "$dir/$folder" -type f 2>/dev/null | wc -l)
                    echo "     ✅ $folder: $count ملف" | tee -a $REPORT_FILE
                else
                    echo "     ✅ $folder: ملف" | tee -a $REPORT_FILE
                fi
            else
                echo "     ❌ $folder: مفقود" | tee -a $REPORT_FILE
            fi
        done
    }
    
    compare_structure "$main1" "$main1"
    compare_structure "$main2" "$main2"
    
    # 4. اكتشاف الملفات المفقودة
    echo "" | tee -a $REPORT_FILE
    echo "🔍 4. اكتشاف الملفات المفقودة:" | tee -a $REPORT_FILE
    
    if [ -d "$main1" ] && [ -d "$main2" ]; then
        echo "   الملفات في $main1 فقط:" | tee -a $REPORT_FILE
        find "$main1" -type f -printf "%P\n" 2>/dev/null | while read file; do
            if [ ! -f "$main2/$file" ]; then
                echo "     ✅ $file" | tee -a $REPORT_FILE
            fi
        done | head -10
        
        echo "   الملفات في $main2 فقط:" | tee -a $REPORT_FILE
        find "$main2" -type f -printf "%P\n" 2>/dev/null | while read file; do
            if [ ! -f "$main1/$file" ]; then
                echo "     ✅ $file" | tee -a $REPORT_FILE
            fi
        done | head -10
    fi
    
    # 5. مقارنة الملفات الرئيسية
    echo "" | tee -a $REPORT_FILE
    echo "🔑 5. الملفات الرئيسية:" | tee -a $REPORT_FILE
    
    check_file() {
        local file=$1
        local desc=$2
        
        if [ -f "$main1/$file" ] && [ -f "$main2/$file" ]; then
            size1=$(stat -f%z "$main1/$file" 2>/dev/null || echo "N/A")
            size2=$(stat -f%z "$main2/$file" 2>/dev/null || echo "N/A")
            
            if [ "$size1" = "$size2" ]; then
                echo "     ✅ $desc: متطابق ($size1 بايت)" | tee -a $REPORT_FILE
            else
                echo "     ⚠️  $desc: مختلف ($main1: $size1 vs $main2: $size2)" | tee -a $REPORT_FILE
            fi
        elif [ -f "$main1/$file" ]; then
            echo "     📁 $desc: موجود في $main1 فقط" | tee -a $REPORT_FILE
        elif [ -f "$main2/$file" ]; then
            echo "     📁 $desc: موجود في $main2 فقط" | tee -a $REPORT_FILE
        else
            echo "     ❌ $desc: مفقود في كليهما" | tee -a $REPORT_FILE
        fi
    }
    
    important_files=(
        "AndroidManifest.xml"
        "java/com/example/androidapp/MainActivity.java"
        "java/com/example/androidapp/App.java"
        "java/com/example/androidapp/data/AppDatabase.java"
        "java/com/example/androidapp/data/dao/AccountDao.java"
        "res/layout/activity_main.xml"
    )
    
    for file_path in "${important_files[@]}"; do
        check_file "$file_path" "$file_path"
    done
    
    # 6. التوصية النهائية
    echo "" | tee -a $REPORT_FILE
    echo "🎯 6. التوصية النهائية:" | tee -a $REPORT_FILE
    echo "----------------------------------------" | tee -a $REPORT_FILE
    
    if [ $file_count1 -gt $file_count2 ]; then
        echo "✅ $main1 أفضل (ملفات أكثر: $file_count1 vs $file_count2)" | tee -a $REPORT_FILE
        return 1
    elif [ $file_count2 -gt $file_count1 ]; then
        echo "✅ $main2 أفضل (ملفات أكثر: $file_count2 vs $file_count1)" | tee -a $REPORT_FILE
        return 2
    else
        # إذا كانا متساويين، ننظر للحجم والتواريخ
        if [ "$size1" != "$size2" ]; then
            if [ "$(echo "$size1" | sed 's/M//')" -gt "$(echo "$size2" | sed 's/M//')" ]; then
                echo "✅ $main1 أفضل (حجم أكبر: $size1 vs $size2)" | tee -a $REPORT_FILE
                return 1
            else
                echo "✅ $main2 أفضل (حجم أكبر: $size2 vs $size1)" | tee -a $REPORT_FILE
                return 2
            fi
        else
            echo "⚖️  المجلدان متساويان تقريباً" | tee -a $REPORT_FILE
            echo "💡 نوصي بالاحتفاظ بـ $main1 (الأصل)" | tee -a $REPORT_FILE
            return 1
        fi
    fi
}

# المقارنة الرئيسية
echo "📍 المجلد الحالي: $(pwd)" | tee -a $REPORT_FILE

if [ -d "src/main" ] && [ -d "src/src/main" ]; then
    compare_main_folders
    result=$?
    
    echo "" | tee -a $REPORT_FILE
    echo "==============================================" | tee -a $REPORT_FILE
    echo "🚀 الإجراء الموصى به:" | tee -a $REPORT_FILE
    
    case $result in
        1)
            echo "✅ الاحتفاظ بـ: src/main" | tee -a $REPORT_FILE
            echo "🗑️  حذف: src/src/main" | tee -a $REPORT_FILE
            echo "💡 الأمر: rm -rf src/src/" | tee -a $REPORT_FILE
            ACTION="rm_src_src"
            ;;
        2)
            echo "✅ الاحتفاظ بـ: src/src/main" | tee -a $REPORT_FILE
            echo "🗑️  حذف: src/main" | tee -a $REPORT_FILE
            echo "💡 الأمر: mv src/main src/main_backup && mv src/src/main src/main" | tee -a $REPORT_FILE
            ACTION="replace_main"
            ;;
    esac
    
elif [ -d "src/main" ]; then
    echo "✅ المجلد src/src/main غير موجود، استخدم src/main" | tee -a $REPORT_FILE
    ACTION="keep_main"
elif [ -d "src/src/main" ]; then
    echo "✅ المجلد src/main غير موجود، استخدم src/src/main" | tee -a $REPORT_FILE
    ACTION="rename_src_main"
else
    echo "❌ لا يوجد مجلدات main للمقارنة" | tee -a $REPORT_FILE
    ACTION="none"
fi

echo "" | tee -a $REPORT_FILE
echo "📋 التقرير الكامل في: $(pwd)/$REPORT_FILE"

# التنفيذ
echo ""
echo "❓ هل تريد تنفيذ الإجراء الموصى به؟ (y/n)"
read -r response
if [ "$response" = "y" ] || [ "$response" = "Y" ]; then
    echo "🚀 جاري التنفيذ..."
    case $ACTION in
        "rm_src_src")
            echo "🗑️ حذف src/src/..."
            rm -rf src/src/
            echo "✅ تم الحذف"
            ;;
        "replace_main")
            echo "🔄 استبدال src/main بـ src/src/main..."
            mv src/main src/main_backup
            mv src/src/main src/main
            echo "✅ تم الاستبدال"
            ;;
        "rename_src_main")
            echo "✏️ إعادة تسمية src/src/main إلى src/main..."
            mv src/src/main src/main
            echo "✅ تمت إعادة التسمية"
            ;;
        *)
            echo "⚠️ لا إجراء مطلوب"
            ;;
    esac
    
    echo ""
    echo "🔍 الهيكل النهائي:"
    find src/main -type f -name "*.java" | head -5
else
    echo "⚠️ تم إلغاء التنفيذ"
fi 
