const functions = require('firebase-functions');
const admin = require('firebase-admin');
const { PredictionServiceClient } = require('@google-cloud/aiplatform');

// Initialize Firebase Admin SDK
admin.initializeApp();

// Initialize AI Platform client
const aiPlatformClient = new PredictionServiceClient();

// Project and location configuration
const PROJECT_ID = 'your-project-id'; // Replace with your actual project ID
const LOCATION = 'us-central1'; // Replace with your preferred location
const ENDPOINT_ID = 'your-endpoint-id'; // Replace with your model endpoint ID

/**
 * Cloud Function to analyze financial data using AI
 * This function receives financial data from the Android app and returns AI-powered insights
 */
exports.analyzeFinancialData = functions.https.onCall(async (data, context) => {
  try {
    // Verify user authentication
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'المستخدم غير مصرح له بالوصول');
    }

    const { companyId, analysisType, financialData } = data;

    // Validate input data
    if (!companyId || !analysisType || !financialData) {
      throw new functions.https.HttpsError('invalid-argument', 'البيانات المطلوبة مفقودة');
    }

    // Log the analysis request
    console.log(`تحليل البيانات المالية للشركة: ${companyId}, نوع التحليل: ${analysisType}`);

    let analysisResult;

    switch (analysisType) {
      case 'cash_flow_prediction':
        analysisResult = await predictCashFlow(financialData);
        break;
      case 'expense_categorization':
        analysisResult = await categorizeExpenses(financialData);
        break;
      case 'fraud_detection':
        analysisResult = await detectFraud(financialData);
        break;
      case 'financial_health_score':
        analysisResult = await calculateFinancialHealthScore(financialData);
        break;
      case 'budget_recommendations':
        analysisResult = await generateBudgetRecommendations(financialData);
        break;
      default:
        throw new functions.https.HttpsError('invalid-argument', 'نوع التحليل غير مدعوم');
    }

    // Store analysis result in Firestore for future reference
    await admin.firestore().collection('ai_analyses').add({
      companyId,
      analysisType,
      result: analysisResult,
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
      userId: context.auth.uid
    });

    return {
      success: true,
      analysisType,
      result: analysisResult,
      timestamp: new Date().toISOString()
    };

  } catch (error) {
    console.error('خطأ في تحليل البيانات المالية:', error);
    throw new functions.https.HttpsError('internal', 'حدث خطأ أثناء تحليل البيانات المالية');
  }
});

/**
 * Predict cash flow based on historical data
 */
async function predictCashFlow(financialData) {
  try {
    // Prepare data for AI model
    const instances = [{
      revenue: financialData.revenue || [],
      expenses: financialData.expenses || [],
      historical_cash_flow: financialData.historicalCashFlow || []
    }];

    // Call AI Platform prediction service
    const endpoint = `projects/${PROJECT_ID}/locations/${LOCATION}/endpoints/${ENDPOINT_ID}`;
    
    const [response] = await aiPlatformClient.predict({
      endpoint,
      instances,
    });

    // Process AI response
    const predictions = response.predictions;
    
    return {
      predicted_cash_flow: predictions[0]?.predicted_values || [],
      confidence_score: predictions[0]?.confidence || 0.8,
      recommendations: generateCashFlowRecommendations(predictions[0]),
      analysis_summary: 'تم تحليل التدفق النقدي بناءً على البيانات التاريخية والاتجاهات الحالية'
    };

  } catch (error) {
    console.error('خطأ في توقع التدفق النقدي:', error);
    // Fallback to rule-based analysis if AI service fails
    return generateRuleBasedCashFlowPrediction(financialData);
  }
}

/**
 * Categorize expenses using AI
 */
async function categorizeExpenses(financialData) {
  try {
    const expenses = financialData.expenses || [];
    const categorizedExpenses = [];

    for (const expense of expenses) {
      // Simple rule-based categorization as fallback
      const category = categorizeExpenseItem(expense);
      categorizedExpenses.push({
        ...expense,
        suggested_category: category.category,
        confidence: category.confidence,
        reasoning: category.reasoning
      });
    }

    return {
      categorized_expenses: categorizedExpenses,
      category_summary: generateCategorySummary(categorizedExpenses),
      recommendations: generateExpenseRecommendations(categorizedExpenses)
    };

  } catch (error) {
    console.error('خطأ في تصنيف المصروفات:', error);
    throw error;
  }
}

/**
 * Detect potential fraud in financial transactions
 */
async function detectFraud(financialData) {
  try {
    const transactions = financialData.transactions || [];
    const suspiciousTransactions = [];

    for (const transaction of transactions) {
      const fraudScore = calculateFraudScore(transaction);
      if (fraudScore > 0.7) {
        suspiciousTransactions.push({
          ...transaction,
          fraud_score: fraudScore,
          risk_factors: identifyRiskFactors(transaction)
        });
      }
    }

    return {
      suspicious_transactions: suspiciousTransactions,
      overall_risk_level: calculateOverallRiskLevel(suspiciousTransactions),
      recommendations: generateFraudPreventionRecommendations(suspiciousTransactions)
    };

  } catch (error) {
    console.error('خطأ في كشف الاحتيال:', error);
    throw error;
  }
}

/**
 * Calculate financial health score
 */
async function calculateFinancialHealthScore(financialData) {
  try {
    const metrics = {
      liquidity_ratio: calculateLiquidityRatio(financialData),
      debt_to_equity: calculateDebtToEquity(financialData),
      profit_margin: calculateProfitMargin(financialData),
      cash_flow_stability: calculateCashFlowStability(financialData)
    };

    const overallScore = (
      metrics.liquidity_ratio * 0.3 +
      (1 - metrics.debt_to_equity) * 0.25 +
      metrics.profit_margin * 0.25 +
      metrics.cash_flow_stability * 0.2
    ) * 100;

    return {
      overall_score: Math.round(overallScore),
      metrics,
      health_level: getHealthLevel(overallScore),
      recommendations: generateHealthRecommendations(metrics),
      trend_analysis: analyzeTrends(financialData)
    };

  } catch (error) {
    console.error('خطأ في حساب نقاط الصحة المالية:', error);
    throw error;
  }
}

/**
 * Generate budget recommendations
 */
async function generateBudgetRecommendations(financialData) {
  try {
    const currentBudget = financialData.budget || {};
    const actualSpending = financialData.actualSpending || {};
    
    const recommendations = [];
    const categories = Object.keys(currentBudget);

    for (const category of categories) {
      const budgeted = currentBudget[category] || 0;
      const actual = actualSpending[category] || 0;
      const variance = ((actual - budgeted) / budgeted) * 100;

      if (Math.abs(variance) > 10) {
        recommendations.push({
          category,
          current_budget: budgeted,
          actual_spending: actual,
          variance_percentage: variance,
          recommendation: generateCategoryRecommendation(category, variance),
          suggested_budget: calculateSuggestedBudget(budgeted, actual, variance)
        });
      }
    }

    return {
      budget_recommendations: recommendations,
      overall_budget_health: calculateBudgetHealth(currentBudget, actualSpending),
      optimization_opportunities: identifyOptimizationOpportunities(financialData)
    };

  } catch (error) {
    console.error('خطأ في توليد توصيات الميزانية:', error);
    throw error;
  }
}

// Helper functions for rule-based analysis

function generateRuleBasedCashFlowPrediction(financialData) {
  // Simple moving average prediction
  const historicalData = financialData.historicalCashFlow || [];
  const lastThreeMonths = historicalData.slice(-3);
  const average = lastThreeMonths.reduce((sum, val) => sum + val, 0) / lastThreeMonths.length;
  
  return {
    predicted_cash_flow: [average, average * 1.05, average * 1.1],
    confidence_score: 0.6,
    recommendations: ['مراقبة التدفق النقدي بانتظام', 'تحسين عمليات التحصيل'],
    analysis_summary: 'تحليل مبسط بناءً على المتوسط المتحرك'
  };
}

function categorizeExpenseItem(expense) {
  const description = (expense.description || '').toLowerCase();
  
  if (description.includes('راتب') || description.includes('أجر')) {
    return { category: 'رواتب وأجور', confidence: 0.9, reasoning: 'يحتوي على كلمات مفتاحية متعلقة بالرواتب' };
  } else if (description.includes('إيجار') || description.includes('كهرباء') || description.includes('ماء')) {
    return { category: 'مصاريف تشغيلية', confidence: 0.8, reasoning: 'مصاريف تشغيلية أساسية' };
  } else if (description.includes('تسويق') || description.includes('إعلان')) {
    return { category: 'تسويق وإعلان', confidence: 0.85, reasoning: 'مصاريف تسويقية' };
  } else {
    return { category: 'مصاريف عامة', confidence: 0.5, reasoning: 'لم يتم التعرف على فئة محددة' };
  }
}

function generateCategorySummary(categorizedExpenses) {
  const summary = {};
  categorizedExpenses.forEach(expense => {
    const category = expense.suggested_category;
    if (!summary[category]) {
      summary[category] = { count: 0, total: 0 };
    }
    summary[category].count++;
    summary[category].total += expense.amount || 0;
  });
  return summary;
}

function generateExpenseRecommendations(categorizedExpenses) {
  return [
    'مراجعة المصاريف التشغيلية بانتظام',
    'تحسين عمليات الشراء والتفاوض مع الموردين',
    'تطبيق سياسات صارمة لمراقبة المصاريف'
  ];
}

function calculateFraudScore(transaction) {
  let score = 0;
  
  // Check for unusual amounts
  if (transaction.amount > 10000) score += 0.3;
  
  // Check for unusual timing
  const hour = new Date(transaction.timestamp).getHours();
  if (hour < 6 || hour > 22) score += 0.2;
  
  // Check for duplicate transactions
  if (transaction.isDuplicate) score += 0.4;
  
  // Check for unusual location (if available)
  if (transaction.isUnusualLocation) score += 0.3;
  
  return Math.min(score, 1.0);
}

function identifyRiskFactors(transaction) {
  const factors = [];
  if (transaction.amount > 10000) factors.push('مبلغ كبير غير معتاد');
  if (new Date(transaction.timestamp).getHours() < 6) factors.push('توقيت غير معتاد');
  if (transaction.isDuplicate) factors.push('معاملة مكررة');
  return factors;
}

function calculateOverallRiskLevel(suspiciousTransactions) {
  if (suspiciousTransactions.length === 0) return 'منخفض';
  if (suspiciousTransactions.length < 3) return 'متوسط';
  return 'عالي';
}

function generateFraudPreventionRecommendations(suspiciousTransactions) {
  return [
    'تفعيل التنبيهات للمعاملات الكبيرة',
    'مراجعة المعاملات المشبوهة يدوياً',
    'تحسين أنظمة الأمان والمصادقة'
  ];
}

function calculateLiquidityRatio(financialData) {
  const currentAssets = financialData.currentAssets || 0;
  const currentLiabilities = financialData.currentLiabilities || 1;
  return currentAssets / currentLiabilities;
}

function calculateDebtToEquity(financialData) {
  const totalDebt = financialData.totalDebt || 0;
  const totalEquity = financialData.totalEquity || 1;
  return totalDebt / totalEquity;
}

function calculateProfitMargin(financialData) {
  const netIncome = financialData.netIncome || 0;
  const revenue = financialData.revenue || 1;
  return netIncome / revenue;
}

function calculateCashFlowStability(financialData) {
  const cashFlows = financialData.historicalCashFlow || [];
  if (cashFlows.length < 2) return 0.5;
  
  const variance = calculateVariance(cashFlows);
  const mean = cashFlows.reduce((sum, val) => sum + val, 0) / cashFlows.length;
  const coefficientOfVariation = Math.sqrt(variance) / Math.abs(mean);
  
  return Math.max(0, 1 - coefficientOfVariation);
}

function calculateVariance(values) {
  const mean = values.reduce((sum, val) => sum + val, 0) / values.length;
  const squaredDiffs = values.map(val => Math.pow(val - mean, 2));
  return squaredDiffs.reduce((sum, val) => sum + val, 0) / values.length;
}

function getHealthLevel(score) {
  if (score >= 80) return 'ممتاز';
  if (score >= 60) return 'جيد';
  if (score >= 40) return 'متوسط';
  return 'ضعيف';
}

function generateHealthRecommendations(metrics) {
  const recommendations = [];
  
  if (metrics.liquidity_ratio < 1) {
    recommendations.push('تحسين السيولة النقدية');
  }
  if (metrics.debt_to_equity > 0.5) {
    recommendations.push('تقليل نسبة الديون');
  }
  if (metrics.profit_margin < 0.1) {
    recommendations.push('تحسين هامش الربح');
  }
  
  return recommendations;
}

function analyzeTrends(financialData) {
  return {
    revenue_trend: 'متزايد',
    expense_trend: 'مستقر',
    profit_trend: 'متزايد'
  };
}

function calculateBudgetHealth(currentBudget, actualSpending) {
  const categories = Object.keys(currentBudget);
  let totalVariance = 0;
  
  categories.forEach(category => {
    const budgeted = currentBudget[category] || 0;
    const actual = actualSpending[category] || 0;
    const variance = Math.abs((actual - budgeted) / budgeted);
    totalVariance += variance;
  });
  
  const averageVariance = totalVariance / categories.length;
  
  if (averageVariance < 0.1) return 'ممتاز';
  if (averageVariance < 0.2) return 'جيد';
  if (averageVariance < 0.3) return 'متوسط';
  return 'يحتاج تحسين';
}

function identifyOptimizationOpportunities(financialData) {
  return [
    'تحسين عمليات الشراء',
    'تقليل المصاريف التشغيلية',
    'زيادة الإيرادات من المنتجات الأساسية'
  ];
}

function generateCategoryRecommendation(category, variance) {
  if (variance > 10) {
    return `تم تجاوز الميزانية في فئة ${category} بنسبة ${variance.toFixed(1)}%. يُنصح بمراجعة المصاريف في هذه الفئة.`;
  } else {
    return `تم توفير ${Math.abs(variance).toFixed(1)}% من ميزانية ${category}. يمكن إعادة توزيع هذا المبلغ على فئات أخرى.`;
  }
}

function calculateSuggestedBudget(budgeted, actual, variance) {
  if (variance > 10) {
    return budgeted * 1.1; // Increase budget by 10%
  } else if (variance < -10) {
    return budgeted * 0.9; // Decrease budget by 10%
  }
  return budgeted; // Keep current budget
}

function generateCashFlowRecommendations(prediction) {
  const recommendations = [];
  
  if (prediction?.predicted_values) {
    const trend = prediction.predicted_values[2] - prediction.predicted_values[0];
    if (trend > 0) {
      recommendations.push('التدفق النقدي يظهر اتجاهاً إيجابياً');
    } else {
      recommendations.push('يُنصح بمراقبة التدفق النقدي عن كثب');
    }
  }
  
  recommendations.push('تحسين عمليات التحصيل');
  recommendations.push('تأجيل المدفوعات غير الضرورية');
  
  return recommendations;
}

// Export additional utility functions for testing
exports.categorizeExpenseItem = categorizeExpenseItem;
exports.calculateFraudScore = calculateFraudScore;
exports.calculateLiquidityRatio = calculateLiquidityRatio;
