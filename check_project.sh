#!/bin/bash
set -e
echo "1) XML syntax check..."
find app/src/main/res -name "*.xml" -print0 | xargs -0 -n1 xmlstarlet val -e 2> xml_errors.txt || true
if [[ -s xml_errors.txt ]]; then
  echo "XML ERRORS:"
  head -n 50 xml_errors.txt
else
  echo "✓ XML syntax OK"
fi

echo
echo "2) Missing @string references..."
grep -oP '(?<=name=").*?(?=")' app/src/main/res/values/strings.xml | sort -u > /tmp/strings_keys.txt
grep -R --line-number --exclude-dir=build -n "@string/" app/src/main/res > /tmp/string_refs.txt || true
grep -o "@string/[^)\"' >]*" /tmp/string_refs.txt | sed 's/@string\///' | sort -u > /tmp/string_refs_only.txt || true
comm -23 /tmp/string_refs_only.txt /tmp/strings_keys.txt > /tmp/missing_string_keys.txt || true
if [[ -s /tmp/missing_string_keys.txt ]]; then
  echo "Missing @string keys:"
  cat /tmp/missing_string_keys.txt
else
  echo "✓ no missing @string keys"
fi

echo
echo "3) Java compile check (javac via Gradle)..."
./gradlew :app:compileDebugJavaWithJavac --no-daemon --stacktrace 2>&1 | tee javac_log.txt || true
grep -n "error:" javac_log.txt || true

echo
echo "4) Try Gradle resource task to show AAPT errors (if any):"
./gradlew :app:processDebugResources --no-daemon --stacktrace --info 2>&1 | tee gradle_process_resources.log || true
grep -nE "AAPT|error:|Exception|Caused by" gradle_process_resources.log || true

echo
echo "=== Finished checks. See generated logs: xml_errors.txt, javac_log.txt, gradle_process_resources.log ==="
