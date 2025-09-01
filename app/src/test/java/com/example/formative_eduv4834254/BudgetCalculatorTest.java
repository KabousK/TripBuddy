package com.example.formative_eduv4834254;

import com.example.formative_eduv4834254.data.ActivityItem;
import com.example.formative_eduv4834254.util.BudgetCalculator;

import org.junit.Test;
import java.util.Arrays;

import static org.junit.Assert.*;

public class BudgetCalculatorTest {
    @Test public void subtotalAndDiscountAndTotal_work() {
        double sub = BudgetCalculator.subtotal(
                Arrays.asList(new ActivityItem("A", 100), new ActivityItem("B", 200)),
                50, 25);
        // Activities not selected by default, so subtotal should be only customs
        assertEquals(75.0, sub, 0.001);
        // Select one and recompute
        ActivityItem a = new ActivityItem("A", 100); a.selected = true;
        sub = BudgetCalculator.subtotal(Arrays.asList(a), 50);
        assertEquals(150.0, sub, 0.001);

        double disc = BudgetCalculator.discount(sub, 3, 0.1);
        assertEquals(15.0, disc, 0.001);
        double total = BudgetCalculator.total(sub, disc);
        assertEquals(135.0, total, 0.001);
    }
}
