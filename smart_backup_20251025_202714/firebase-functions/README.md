# Firebase Functions للتحليل المالي بالذكاء الاصطناعي

هذا المشروع يحتوي على Firebase Functions التي توفر خدمات التحليل المالي بالذكاء الاصطناعي لتطبيق المحاسبة على Android.

## الميزات

### 1. تحليل التدفق النقدي (`cash_flow_prediction`)
- توقع التدفق النقدي المستقبلي بناءً على البيانات التاريخية
- حساب درجة الثقة في التوقعات
- تقديم توصيات لتحسين التدفق النقدي

### 2. تصنيف المصروفات (`expense_categorization`)
- تصنيف المصروفات تلقائياً إلى فئات مناسبة
- حساب درجة الثقة في التصنيف
- تقديم ملخص للمصروفات حسب الفئة

### 3. كشف الاحتيال (`fraud_detection`)
- تحليل المعاملات المالية لكشف الأنشطة المشبوهة
- حساب نقاط المخاطر للمعاملات
- تحديد عوامل الخطر وتقديم توصيات الوقاية

### 4. نقاط الصحة المالية (`financial_health_score`)
- حساب نقاط شاملة للصحة المالية للشركة
- تحليل المؤشرات المالية الرئيسية
- تقديم توصيات لتحسين الأداء المالي

### 5. توصيات الميزانية (`budget_recommendations`)
- مقارنة الميزانية المخططة مع الإنفاق الفعلي
- تحديد الانحرافات وتقديم توصيات للتحسين
- اقتراح ميزانيات محسّنة للفترات القادمة

## التثبيت والإعداد

### 1. تثبيت التبعيات
```bash
npm install
```

### 2. إعداد Firebase
1. قم بتحديث `PROJECT_ID` في ملف `index.js`
2. قم بتحديث معرف المشروع في `.firebaserc`
3. تأكد من تفعيل Firebase Functions في مشروعك

### 3. إعداد Google Cloud AI Platform
1. قم بإنشاء نموذج AI في Google Cloud AI Platform
2. احصل على `ENDPOINT_ID` وقم بتحديثه في `index.js`
3. تأكد من تفعيل الصلاحيات المناسبة لـ AI Platform

## النشر

### نشر محلي للاختبار
```bash
npm run serve
```

### النشر إلى Firebase
```bash
npm run deploy
```

## الاستخدام

### استدعاء الدالة من تطبيق Android

```javascript
// مثال على استدعاء دالة تحليل التدفق النقدي
const functions = firebase.functions();
const analyzeFinancialData = functions.httpsCallable('analyzeFinancialData');

analyzeFinancialData({
  companyId: 'company123',
  analysisType: 'cash_flow_prediction',
  financialData: {
    revenue: [10000, 12000, 11000],
    expenses: [8000, 9000, 8500],
    historicalCashFlow: [2000, 3000, 2500]
  }
}).then((result) => {
  console.log('نتيجة التحليل:', result.data);
}).catch((error) => {
  console.error('خطأ في التحليل:', error);
});
```

## هيكل البيانات

### طلب التحليل
```json
{
  "companyId": "string",
  "analysisType": "cash_flow_prediction|expense_categorization|fraud_detection|financial_health_score|budget_recommendations",
  "financialData": {
    // البيانات المالية حسب نوع التحليل
  }
}
```

### استجابة التحليل
```json
{
  "success": true,
  "analysisType": "string",
  "result": {
    // نتائج التحليل حسب النوع
  },
  "timestamp": "ISO 8601 string"
}
```

## الأمان

- جميع الدوال تتطلب مصادقة المستخدم
- يتم حفظ نتائج التحليل في Firestore مع معرف المستخدم
- يتم التحقق من صحة البيانات المدخلة

## المراقبة والسجلات

- استخدم `npm run logs` لعرض سجلات الدوال
- يتم حفظ جميع طلبات التحليل في Firestore للمراجعة
- يتم تسجيل الأخطاء مع تفاصيل كاملة

## التطوير المستقبلي

- إضافة المزيد من أنواع التحليل المالي
- تحسين دقة نماذج الذكاء الاصطناعي
- إضافة دعم للتحليل في الوقت الفعلي
- تطوير لوحة تحكم لمراقبة الأداء

## الدعم

للحصول على المساعدة أو الإبلاغ عن مشاكل، يرجى إنشاء issue في المستودع أو التواصل مع فريق التطوير.
