package com.example.formative_eduv4834254.util;

import com.example.formative_eduv4834254.data.ActivityItem;

import java.util.List;

/**
 * Pure functions for subtotal/discount/total math and safe parsing.
 * Keeps UI code thin and makes unit testing easy.
 */
public final class BudgetCalculator {

    private BudgetCalculator() {}

    public static double subtotal(List<ActivityItem> predefined, double... customExpenses) {
        double sum = 0.0;
        if (predefined != null) {
            for (ActivityItem a : predefined) {
                if (a != null && a.selected) sum += Math.max(0.0, a.price);
            }
        }
        if (customExpenses != null) {
            for (double v : customExpenses) sum += Math.max(0.0, v);
        }
        return round2(sum);
    }

    public static double discount(double subtotal, int tripCount, double rate) {
        if (tripCount >= 3 && rate > 0.0) {
            return round2(subtotal * rate);
        }
        return 0.0;
    }

    public static double total(double subtotal, double discount) {
        return round2(Math.max(0.0, subtotal - discount));
    }

    public static double parseMoney(String raw) {
        if (raw == null) return 0.0;
        try {
            double v = Double.parseDouble(raw.trim());
            return v < 0 ? Double.NaN : v;
        } catch (NumberFormatException nfe) {
            return Double.NaN;
        }
    }

    public static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
