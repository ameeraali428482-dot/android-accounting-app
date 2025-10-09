#!/bin/bash

echo "🔍 بدء المقارنة المتقدمة بين المجلدين المكررين"
echo "=============================================="

# البحث التلقائي عن مجلد app
if [ -d "app" ]; then
    echo "📁 تم العثور على مجلد app، الانتقال إليه..."
    cd app
elif [ -d "src" ] && [ -d "src/src" ]; then
    echo "📁 أنت بالفعل في المجلد الصحيح"
else
    echo "❌ لم يتم العثور على مجلد app أو المجلدات المطلوبة"
    echo "🔍 البحث عن الهيكل..."
    find . -name "src" -type d 2>/dev/null | head -10
    exit 1
fi

echo "📍 المجلد الحالي: $(pwd)"

# إنشاء ملف التقرير
REPORT_FILE="folder_comparison_final.txt"
echo "التقرير النهائي للمقارنة - $(date)" > $REPORT_FILE
echo "==============================================" >> $REPORT_FILE

# دالة للمقارنة الشاملة
compare_folders() {
    local dir1=$1
    local dir2=$2
    local name1=$3
    local name2=$4
    
    echo "📊 مقارنة: $name1 vs $name2" | tee -a $REPORT_FILE
    echo "----------------------------------------" | tee -a $REPORT_FILE
    
    # 1. المقارنة الكمية
    echo "📈 1. المقارنة الكمية:" | tee -a $REPORT_FILE
    
    count_files() {
        local dir=$1
        local pattern=$2
        local desc=$3
        local count=$(find "$dir" -name "$pattern" 2>/dev/null | wc -l)
        echo "   $desc: $count ملف" | tee -a $REPORT_FILE
        echo $count
    }
    
    echo "   في $name1:" | tee -a $REPORT_FILE
    java_count1=$(count_files "$dir1" "*.java" "Java")
    xml_count1=$(count_files "$dir1" "*.xml" "XML")
    
    echo "   في $name2:" | tee -a $REPORT_FILE
    java_count2=$(count_files "$dir2" "*.java" "Java")
    xml_count2=$(count_files "$dir2" "*.xml" "XML")
    
    # 2. مقارنة الأحجام
    echo "📦 2. مقارنة الأحجام:" | tee -a $REPORT_FILE
    size1=$(du -sh "$dir1" 2>/dev/null | cut -f1)
    size2=$(du -sh "$dir2" 2>/dev/null | cut -f1)
    echo "   $name1: $size1" | tee -a $REPORT_FILE
    echo "   $name2: $size2" | tee -a $REPORT_FILE
    
    # 3. مقارنة التواريخ
    echo "🕒 3. أحدث الملفات:" | tee -a $REPORT_FILE
    echo "   $name1:" | tee -a $REPORT_FILE
    find "$dir1" -name "*.java" -exec ls -lt {} + 2>/dev/null | head -3 | while read line; do
        echo "     $line" | tee -a $REPORT_FILE
    done
    
    echo "   $name2:" | tee -a $REPORT_FILE
    find "$dir2" -name "*.java" -exec ls -lt {} + 2>/dev/null | head -3 | while read line; do
        echo "     $line" | tee -a $REPORT_FILE
    done
    
    # 4. مقارنة الملفات المهمة
    echo "🔑 4. الملفات الرئيسية:" | tee -a $REPORT_FILE
    check_critical_files() {
        local dir=$1
        local name=$2
        echo "   في $name:" | tee -a $REPORT_FILE
        
        critical_files=(
            "main/AndroidManifest.xml"
            "main/java/com/example/androidapp/MainActivity.java"
            "main/java/com/example/androidapp/App.java"
            "main/java/com/example/androidapp/data/AppDatabase.java"
        )
        
        for file in "${critical_files[@]}"; do
            if [ -f "$dir/$file" ]; then
                echo "     ✅ $file" | tee -a $REPORT_FILE
            else
                echo "     ❌ $file" | tee -a $REPORT_FILE
            fi
        done
    }
    
    check_critical_files "$dir1" "$name1"
    check_critical_files "$dir2" "$name2"
    
    # 5. التوصية
    echo "💡 5. التوصية:" | tee -a $REPORT_FILE
    
    total1=$((java_count1 + xml_count1))
    total2=$((java_count2 + xml_count2))
    
    if [ $total1 -gt $total2 ]; then
        echo "   ✅ $name1 أفضل (ملفات أكثر: $total1 vs $total2)" | tee -a $REPORT_FILE
        return 1
    elif [ $total2 -gt $total1 ]; then
        echo "   ✅ $name2 أفضل (ملفات أكثر: $total2 vs $total1)" | tee -a $REPORT_FILE
        return 2
    else
        echo "   ⚖️  المجلدان متساويان في عدد الملفات" | tee -a $REPORT_FILE
        return 0
    fi
}

# المقارنة الرئيسية
if [ -d "src" ] && [ -d "src/src" ]; then
    compare_folders "src" "src/src" "src/" "src/src/"
    result=$?
    
    echo "" | tee -a $REPORT_FILE
    echo "🎯 القرار النهائي:" | tee -a $REPORT_FILE
    echo "==============================================" | tee -a $REPORT_FILE
    
    case $result in
        1)
            echo "✅ الاحتفاظ بـ: src/" | tee -a $REPORT_FILE
            echo "🗑️  حذف: src/src/" | tee -a $REPORT_FILE
            ACTION="rm_src_src"
            ;;
        2)
            echo "✅ الاحتفاظ بـ: src/src/" | tee -a $REPORT_FILE
            echo "🗑️  حذف: src/" | tee -a $REPORT_FILE
            ACTION="replace_src"
            ;;
        0)
            echo "✅ المجلدان متطابقان، حذف src/src/" | tee -a $REPORT_FILE
            ACTION="rm_src_src"
            ;;
    esac
    
elif [ -d "src" ]; then
    echo "✅ المجلد src/src/ غير موجود، استخدم src/" | tee -a $REPORT_FILE
    ACTION="keep_src"
elif [ -d "src/src" ]; then
    echo "✅ المجلد src/ غير موجود، استخدم src/src/" | tee -a $REPORT_FILE
    ACTION="rename_src_src"
else
    echo "❌ لا يوجد مجلدات للمقارنة" | tee -a $REPORT_FILE
    ACTION="none"
fi

echo "" | tee -a $REPORT_FILE
echo "📋 التقرير الكامل في: $(pwd)/$REPORT_FILE"

# التنفيذ
echo ""
echo "❓ هل تريد تنفيذ الإجراء؟ (y/n)"
read -r response
if [ "$response" = "y" ] || [ "$response" = "Y" ]; then
    echo "🚀 جاري التنفيذ..."
    case $ACTION in
        "rm_src_src")
            echo "🗑️ حذف src/src/..."
            rm -rf src/src/
            echo "✅ تم الحذف"
            ;;
        "replace_src")
            echo "🔄 استبدال src/ بـ src/src/..."
            mv src src_backup
            mv src/src src
            echo "✅ تم الاستبدال"
            ;;
        "rename_src_src")
            echo "✏️ إعادة تسمية src/src/ إلى src/..."
            mv src/src src
            echo "✅ تمت إعادة التسمية"
            ;;
        *)
            echo "⚠️ لا إجراء مطلوب"
            ;;
    esac
    
    echo ""
    echo "🔍 الهيكل النهائي:"
    tree -L 2
else
    echo "⚠️ تم إلغاء التنفيذ"
fi
