cat > fix_resources.sh <<'EOF'
#!/bin/bash
echo "🔧 بدء إصلاح موارد المشروع..."

BASE_PATH="/data/data/com.termux/files/home/android-accounting-app/app/src/main/res"

# إنشاء المجلدات المطلوبة
mkdir -p "$BASE_PATH/font" "$BASE_PATH/drawable" "$BASE_PATH/values" "$BASE_PATH/layout"

echo "📝 إصلاح ملفات الخطوط..."
cat > "$BASE_PATH/font/cairo_bold.xml" <<'FONTA'
<font-family xmlns:app="http://schemas.android.com/apk/res-auto"
    app:fontProviderAuthority="com.google.android.gms.fonts"
    app:fontProviderPackage="com.google.android.gms"
    app:fontProviderQuery="Cairo"
    app:fontProviderCerts="@array/com_google_android_gms_fonts_certs"/>
FONTA

cat > "$BASE_PATH/font/cairo_regular.xml" <<'FONTB'
<font-family xmlns:app="http://schemas.android.com/apk/res-auto"
    app:fontProviderAuthority="com.google.android.gms.fonts"
    app:fontProviderPackage="com.google.android.gms"
    app:fontProviderQuery="Cairo"
    app:fontProviderCerts="@array/com_google_android_gms_fonts_certs"/>
FONTB

echo "🎨 إنشاء أيقونات ناقصة..."
for i in ic_arrow_back_24 ic_edit_24 ic_share_24 ic_print_24 ic_add_24; do
cat > "$BASE_PATH/drawable/$i.xml" <<'ICON'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#000000" android:pathData="M12,2L2,12h10v10h2V12h10L12,2z"/>
</vector>
ICON
done

echo "🌈 إضافة ألوان ناقصة..."
cat > "$BASE_PATH/values/colors.xml" <<'COLORS'
<resources>
    <color name="material_surface">#FFFFFF</color>
    <color name="material_primary">#6200EE</color>
    <color name="material_surface_variant">#E0E0E0</color>
</resources>
COLORS

echo "📐 تصحيح orientation في layouts..."
LAYOUT_FILE="$BASE_PATH/layout/activity_account_statement_reconciliation.xml"
if [ -f "$LAYOUT_FILE" ]; then
    sed -i 's/android:orientation="column"/android:orientation="vertical"/g' "$LAYOUT_FILE"
    echo "✅ تم تصحيح orientation في $LAYOUT_FILE"
else
    echo "⚠️ لم يتم العثور على ملف $LAYOUT_FILE لتصحيحه."
fi

echo "🧹 تنظيف المشروع..."
cd /data/data/com.termux/files/home/android-accounting-app || exit
./gradlew clean

echo "⚙️ بناء التطبيق..."
./gradlew assembleDebug

echo "✅ تم الإصلاح والبناء بنجاح!"
EOF

chmod +x fix_resources.sh
echo "📄 تم إنشاء fix_resources.sh — شغّله الآن بـ: ./fix_resources.sh"
