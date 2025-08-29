package com.example.formative_eduv4834254.ui.summary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.formative_eduv4834254.R;

import java.util.Locale;

public class BudgetSummaryFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_budget_summary, container, false);

        Bundle b = getArguments() != null ? getArguments() : new Bundle();
        String dest = b.getString("destination", "");
        String start = b.getString("start", "");
        String end = b.getString("end", "");
        String notes = b.getString("notes", "");
        double subtotal = b.getDouble("subtotal", 0);
        double discount = b.getDouble("discount", 0);
        double total = b.getDouble("total", 0);

        ((TextView) v.findViewById(R.id.tv_dest))
                .setText(getString(R.string.summary_destination, dest));
        ((TextView) v.findViewById(R.id.tv_dates))
                .setText(getString(R.string.summary_dates, start, end));
        ((TextView) v.findViewById(R.id.tv_notes))
                .setText(getString(R.string.summary_notes, notes));

        String cur = getString(R.string.currency_format);
        ((TextView) v.findViewById(R.id.tv_summary_subtotal))
                .setText(getString(R.string.summary_subtotal, String.format(Locale.getDefault(), cur, subtotal)));
        ((TextView) v.findViewById(R.id.tv_summary_discount))
                .setText(getString(R.string.summary_discount, String.format(Locale.getDefault(), cur, discount)));
        ((TextView) v.findViewById(R.id.tv_summary_total))
                .setText(getString(R.string.summary_final, String.format(Locale.getDefault(), cur, total)));

        return v;
    }
}
