package com.example.androidapp.utils;

import android.util.Log;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class AIManager {

    private static final String TAG = "AIManager";
    private FirebaseFunctions mFunctions;

    public AIManager() {
        mFunctions = FirebaseFunctions.getInstance();
    }

    public void analyzeFinancialData(String companyId, String analysisType, Map<String, Object> financialData, final AIManagerCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("companyId", companyId);
        data.put("analysisType", analysisType);
        data.put("financialData", financialData);

        mFunctions.getHttpsCallable("analyzeFinancialData")
                .call(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HttpsCallableResult result = task.getResult();
                        if (result != null && result.getData() instanceof Map) {
                            Map<String, Object> response = (Map<String, Object>) result.getData();
                            callback.onSuccess(response);
                        } else {
                            callback.onFailure(new Exception("Invalid response from Firebase Function"));
                        }
                    } else {
                        Log.e(TAG, "Firebase Function call failed", task.getException());
                        callback.onFailure(task.getException());
                    }
                });
    }

    public interface AIManagerCallback {
        void onSuccess(Map<String, Object> result);
        void onFailure(Exception e);
    }
}
