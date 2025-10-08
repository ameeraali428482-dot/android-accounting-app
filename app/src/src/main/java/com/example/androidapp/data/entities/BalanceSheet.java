package com.example.androidapp.data.entities;

import java.util.Map;

public class BalanceSheet {
    private Map<String, Float> assets;
    private Map<String, Float> liabilities;
    private Map<String, Float> equity;

    public BalanceSheet() {
        // Placeholder for a more complex constructor that would populate these maps
    }

    public Map<String, Float> getAssets() {
        return assets;
    }

    public void setAssets(Map<String, Float> assets) {
        this.assets = assets;
    }

    public Map<String, Float> getLiabilities() {
        return liabilities;
    }

    public void setLiabilities(Map<String, Float> liabilities) {
        this.liabilities = liabilities;
    }

    public Map<String, Float> getEquity() {
        return equity;
    }

    public void setEquity(Map<String, Float> equity) {
        this.equity = equity;
    }
}
