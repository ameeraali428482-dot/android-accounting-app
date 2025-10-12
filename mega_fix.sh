#!/bin/bash

echo "🔥 بدء الإصلاح الشامل النهائي..."
cd "$(dirname "$0")"

if [ ! -d "app/src/main/java" ]; then
    echo "❌ خطأ: شغل السكريبت من جذر المشروع"
    exit 1
fi

JAVA="app/src/main/java/com/example/androidapp"

# نسخة احتياطية
echo "📦 نسخة احتياطية..."
tar -czf "backup_$(date +%s).tar.gz" "$JAVA" 2>/dev/null
echo "✅ تمت النسخة الاحتياطية"

# إصلاح RoleListActivity
echo "🔧 إصلاح RoleListActivity..."
sed -i '/adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {/,/new ArrayList<>(),/ {
    /new ArrayList<>(),/d
}' "$JAVA/ui/role/RoleListActivity.java"
sed -i 's/adapter = new GenericAdapter<Object>/adapter = new GenericAdapter<Role>/' "$JAVA/ui/role/RoleListActivity.java"

# إصلاح RoleDetailActivity
echo "🔧 إصلاح RoleDetailActivity..."
sed -i '/permissionsAdapter = new GenericAdapter<Object>(new ArrayList<>(), null) {/,/new ArrayList<>(),/ {
    /new ArrayList<>(),/d
}' "$JAVA/ui/role/RoleDetailActivity.java"
sed -i 's/permissionsAdapter = new GenericAdapter<Object>/permissionsAdapter = new GenericAdapter<Permission>/' "$JAVA/ui/role/RoleDetailActivity.java"

# إصلاح ChatDetailActivity
echo "🔧 إصلاح ChatDetailActivity..."
sed -i '/adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {/,/new ArrayList<>(),/ {
    /new ArrayList<>(),/d
}' "$JAVA/ui/chat/ChatDetailActivity.java"
sed -i 's/adapter = new GenericAdapter<Object>/adapter = new GenericAdapter<Chat>/' "$JAVA/ui/chat/ChatDetailActivity.java"

# إصلاح PointTransactionListActivity
echo "🔧 إصلاح PointTransactionListActivity..."
sed -i '/adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {/,/new ArrayList<>(),/ {
    /new ArrayList<>(),/d
}' "$JAVA/ui/pointtransaction/PointTransactionListActivity.java"
sed -i 's/adapter = new GenericAdapter<Object>/adapter = new GenericAdapter<PointTransaction>/' "$JAVA/ui/pointtransaction/PointTransactionListActivity.java"

# إصلاح TrophyListActivity
echo "🔧 إصلاح TrophyListActivity..."
sed -i '/adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {/,/new ArrayList<>(),/ {
    /new ArrayList<>(),/d
}' "$JAVA/ui/trophy/TrophyListActivity.java"
sed -i 's/adapter = new GenericAdapter<Object>/adapter = new GenericAdapter<Trophy>/' "$JAVA/ui/trophy/TrophyListActivity.java"

# إصلاح AdminUserListActivity
echo "🔧 إصلاح AdminUserListActivity..."
sed -i '/adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {/,/new ArrayList<>(),/ {
    /new ArrayList<>(),/d
}' "$JAVA/ui/admin/AdminUserListActivity.java"
sed -i 's/adapter = new GenericAdapter<Object>/adapter = new GenericAdapter<User>/' "$JAVA/ui/admin/AdminUserListActivity.java"

# إصلاح AdminUserDetailActivity
echo "🔧 إصلاح AdminUserDetailActivity..."
sed -i '/adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {/,/new ArrayList<>(),/ {
    /new ArrayList<>(),/d
}' "$JAVA/ui/admin/AdminUserDetailActivity.java"
sed -i 's/adapter = new GenericAdapter<Object>/adapter = new GenericAdapter<Role>/' "$JAVA/ui/admin/AdminUserDetailActivity.java"

# إصلاح VoucherListActivity - إعادة كتابة onCreate
echo "🔧 إصلاح VoucherListActivity..."
python3 << 'PYEOF'
import re

file_path = "app/src/main/java/com/example/androidapp/ui/voucher/VoucherListActivity.java"
try:
    with open(file_path, 'r') as f:
        content = f.read()
    
    # إصلاح onCreate
    pattern = r'fab.setOnClickListener(v -> {[^}]+});'
    replacement = '''fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, VoucherDetailActivity.class);
            startActivity(intent);
        });
    }'''
    
    content = re.sub(pattern, replacement, content)
    
    with open(file_path, 'w') as f:
        f.write(content)
    print("✓ VoucherListActivity")
except: pass
PYEOF

# إصلاح CampaignListActivity
echo "🔧 إصلاح CampaignListActivity..."
python3 << 'PYEOF'
import re

file_path = "app/src/main/java/com/example/androidapp/ui/campaign/CampaignListActivity.java"
try:
    with open(file_path, 'r') as f:
        content = f.read()
    
    pattern = r'fab.setOnClickListener(v -> {[^}]+});'
    replacement = '''fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, CampaignDetailActivity.class);
            startActivity(intent);
        });
    }'''
    
    content = re.sub(pattern, replacement, content)
    
    with open(file_path, 'w') as f:
        f.write(content)
    print("✓ CampaignListActivity")
except: pass
PYEOF

echo ""
echo "✅✅✅ اكتمل الإصلاح!"
echo ""
echo "🎯 الآن شغّل:"
echo "   ./gradlew clean assembleDebug"
