from flask import Flask, render_template_string, send_file
import os

app = Flask(__name__)
LOG_FILE = os.path.expanduser("~/android-accounting-app/build_full_log.txt")
ERROR_FILE = os.path.expanduser("~/android-accounting-app/build_errors.txt")

HTML = """
<!doctype html>
<html lang="ar">
  <head>
    <meta charset="utf-8">
    <title>Codemagic Logs Viewer</title>
    <style>
      body { background:#111; color:#ddd; font-family:monospace; padding:18px; }
      pre { background:#1e1e1e; padding:12px; border-radius:6px; overflow:auto; max-height:60vh; }
      button { margin:6px; padding:8px 10px; border-radius:6px; border:none; background:#0a84ff; color:#fff; }
      h2 { margin:6px 0; }
    </style>
  </head>
  <body>
    <h2>📕 أخطاء البناء</h2>
    <pre id="errors">{{ errors }}</pre>
    <button onclick="copyText('errors')">📋 نسخ الأخطاء</button>
    <button onclick="location.href='/download_errors'">⬇ تحميل الأخطاء</button>

    <h2>📗 اللوج الكامل</h2>
    <pre id="log">{{ log }}</pre>
    <button onclick="copyText('log')">📋 نسخ اللوج</button>
    <button onclick="location.href='/download_log'">⬇ تحميل اللوج</button>

    <script>
      function copyText(id){
        const txt = document.getElementById(id).innerText;
        navigator.clipboard.writeText(txt).then(()=>alert('✅ تم النسخ'));
      }
      setInterval(()=>location.reload(),10000);
    </script>
  </body>
</html>
"""

@app.route('/')
def index():
    log = open(LOG_FILE, 'r', encoding='utf-8').read() if os.path.exists(LOG_FILE) else 'لا يوجد لوج.'
    errors = open(ERROR_FILE, 'r', encoding='utf-8').read() if os.path.exists(ERROR_FILE) else 'لا توجد أخطاء.'
    return render_template_string(HTML, log=log, errors=errors)

@app.route('/download_log')
def download_log():
    return send_file(LOG_FILE, as_attachment=True)

@app.route('/download_errors')
def download_errors():
    return send_file(ERROR_FILE, as_attachment=True)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
