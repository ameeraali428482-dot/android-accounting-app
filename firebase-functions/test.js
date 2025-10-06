const { categorizeExpenseItem, calculateFraudScore, calculateLiquidityRatio } = require('./index');

// Test expense categorization
console.log('اختبار تصنيف المصروفات:');
console.log(categorizeExpenseItem({ description: 'راتب موظف', amount: 5000 }));
console.log(categorizeExpenseItem({ description: 'إيجار المكتب', amount: 2000 }));
console.log(categorizeExpenseItem({ description: 'حملة تسويقية', amount: 1500 }));

// Test fraud detection
console.log('\nاختبار كشف الاحتيال:');
console.log('نقاط الاحتيال للمعاملة العادية:', calculateFraudScore({
  amount: 500,
  timestamp: new Date('2023-12-01T14:30:00Z'),
  isDuplicate: false,
  isUnusualLocation: false
}));

console.log('نقاط الاحتيال للمعاملة المشبوهة:', calculateFraudScore({
  amount: 15000,
  timestamp: new Date('2023-12-01T02:30:00Z'),
  isDuplicate: true,
  isUnusualLocation: true
}));

// Test liquidity ratio calculation
console.log('\nاختبار حساب نسبة السيولة:');
console.log('نسبة السيولة:', calculateLiquidityRatio({
  currentAssets: 50000,
  currentLiabilities: 30000
}));

console.log('نسبة السيولة المنخفضة:', calculateLiquidityRatio({
  currentAssets: 20000,
  currentLiabilities: 30000
}));

console.log('\nتم إكمال جميع الاختبارات بنجاح!');
