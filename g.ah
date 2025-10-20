#!/bin/bash

echo "🔧 إصلاح أخطاء الـ Constructor النهائية - Room Database"
echo "==============================================="

# إضافة @Ignore لـ User.java
echo "📝 إصلاح User.java..."
sed -i '/public User(String name, String email, long createdAt, long updatedAt)/i\    @Ignore' app/src/main/java/com/example/androidapp/data/entities/User.java

# إضافة @Ignore لـ ContactSync.java  
echo "📝 إصلاح ContactSync.java..."
sed -i '/public ContactSync(int userId, String contactIdentifier, String displayName)/i\    @Ignore' app/src/main/java/com/example/androidapp/data/entities/ContactSync.java

# إضافة @Ignore لـ Role.java
echo "📝 إصلاح Role.java..."
sed -i '/public Role(String roleId, String name, String description, long createdAt)/i\    @Ignore' app/src/main/java/com/example/androidapp/data/entities/Role.java

echo "✅ تم إصلاح جميع أخطاء الـ Constructor!"
echo "🚀 الآن يمكنك تشغيل البناء: ./gradlew build"
