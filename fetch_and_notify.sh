#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

TOKEN="C958vBKSmxCoQCpOOufGzR6RhgAShMNGAmM4S8a7_OY"
APP_ID="68e413d9bdaaa403f9aeb481"
LOG_FILE="/data/data/com.termux/files/home/android-accounting-app/build_full_log.txt"
ERROR_FILE="/data/data/com.termux/files/home/android-accounting-app/build_errors.txt"
TG_BOT="8310914786:AAH8NPaIoGKbXtFS_-vpcZ_hzkVH847JDEI"
TG_CHAT="1254508624"

# جلب آخر Build ID
resp=$(curl -s -H "x-auth-token: $TOKEN" "https://api.codemagic.io/apps/$APP_ID/builds")
LATEST=$(echo "$resp" | jq -r '.builds[0].id // .[0].id // empty')

if [ -z "$LATEST" ]; then
  echo "❌ لم أتمكن من إيجاد Build ID تلقائياً."
  exit 1
fi

echo "✅ سيتم جلب اللوج للبناء: $LATEST"

# جلب اللوج
curl -s -H "x-auth-token: $TOKEN" "https://api.codemagic.io/apps/$APP_ID/builds/$LATEST/logs" -o "$LOG_FILE"

# استخراج الأخطاء
grep -iE "FAILURE|FATAL|Exception|error" "$LOG_FILE" > "$ERROR_FILE" || true

# إرسال إلى Telegram
if [ -n "$TG_BOT" ] && [ -n "$TG_CHAT" ]; then
  summary=$(head -n 25 "$ERROR_FILE" | sed 's/"/\"/g' | sed ':a;N;$!ba;s/\n/\n/g')
  [ -z "$summary" ] && summary="(لا توجد أخطاء مكتشفة)"
  text="🔧 Codemagic Build ($LATEST) Summary:\n\n$summary"
  curl -s -X POST "https://api.telegram.org/bot$TG_BOT/sendMessage"   -d chat_id="$TG_CHAT"   -d parse_mode="Markdown"   -d text="$text" >/dev/null || echo "⚠️ فشل إرسال التليجرام."
fi

echo "📜 تم حفظ اللوج في: $LOG_FILE"
echo "📜 تم حفظ الأخطاء في: $ERROR_FILE"
