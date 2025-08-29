package com.example.formative_eduv4834254.ui.budget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.data.ActivityItem;
import com.example.formative_eduv4834254.data.Trip;
import com.example.formative_eduv4834254.data.TripRepository;
import com.example.formative_eduv4834254.data.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    private TextInputEditText etDestination, etNotes, etStartDate, etEndDate, etVisa, etTransport, etMeals, etCustom;
    private TextView tvSubtotal, tvDiscount, tvFinal;
    private final List<ActivityItem> activities = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_budget, container, false);

        etDestination = root.findViewById(R.id.et_destination);
        etNotes = root.findViewById(R.id.et_notes);
        etStartDate = root.findViewById(R.id.et_start_date);
        etEndDate = root.findViewById(R.id.et_end_date);
        etVisa = root.findViewById(R.id.et_visa);
        etTransport = root.findViewById(R.id.et_transport);
        etMeals = root.findViewById(R.id.et_meals);
        etCustom = root.findViewById(R.id.et_custom);

        tvSubtotal = root.findViewById(R.id.tv_subtotal);
        tvDiscount = root.findViewById(R.id.tv_discount);
        tvFinal = root.findViewById(R.id.tv_final);

        etStartDate.setOnClickListener(v -> showDateDialog(true));
        etEndDate.setOnClickListener(v -> showDateDialog(false));

    seedActivities();
    preloadLastSelections();
        RecyclerView rv = root.findViewById(R.id.rv_activities);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new ActivityAdapter(activities, this::recalc));

        TextWatcher watcher = new PlainWatcher(this::recalc);
        etVisa.addTextChangedListener(watcher);
        etTransport.addTextChangedListener(watcher);
        etMeals.addTextChangedListener(watcher);
        etCustom.addTextChangedListener(watcher);

        MaterialButton btnSave = root.findViewById(R.id.btn_save_trip);
        MaterialButton btnConfirm = root.findViewById(R.id.btn_confirm);
        btnSave.setOnClickListener(v -> saveTrip(false));
        btnConfirm.setOnClickListener(v -> saveTrip(true));

        recalc();
        return root;
    }

    private void showDateDialog(boolean isStart) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(getContext(), (DatePicker view, int year, int month, int dayOfMonth) -> {
            String d = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            if (isStart) etStartDate.setText(d); else etEndDate.setText(d);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dlg.show();
    }

    private void seedActivities() {
        if (!activities.isEmpty()) return;
        activities.add(new ActivityItem("Sightseeing", 500));
        activities.add(new ActivityItem("Hiking", 350));
        activities.add(new ActivityItem("Dining", 300));
        activities.add(new ActivityItem("Museum Tour", 200));
        activities.add(new ActivityItem("Beach Day", 260));
    }

    private double safeDouble(TextInputEditText et) {
        CharSequence s = et.getText();
        String t = s == null ? "" : s.toString().trim();
        if (t.isEmpty()) return 0.0;
        try {
            double v = Double.parseDouble(t);
            return v >= 0 ? v : Double.NaN;
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    private void recalc() {
        double visa = safeDouble(etVisa);
        double transport = safeDouble(etTransport);
        double meals = safeDouble(etMeals);
        double custom = safeDouble(etCustom);

        if (Double.isNaN(visa) || Double.isNaN(transport) || Double.isNaN(meals) || Double.isNaN(custom)) {
            Toast.makeText(getContext(), R.string.toast_invalid_number, Toast.LENGTH_SHORT).show();
            return;
        }

        double actSum = 0;
        for (ActivityItem a : activities) if (a.selected) actSum += a.price;
        double subtotal = actSum + visa + transport + meals + custom;

    int tripCount = SessionManager.getTripCount(getContext());
        double discount = tripCount >= 3 ? subtotal * 0.10 : 0.0;
        double finalTotal = subtotal - discount;

        tvSubtotal.setText(getString(R.string.summary_subtotal, getString(R.string.currency_format, subtotal)));
        tvDiscount.setText(getString(R.string.summary_discount, getString(R.string.currency_format, discount)));
        tvFinal.setText(getString(R.string.summary_final, getString(R.string.currency_format, finalTotal)));
    }

    private void saveTrip(boolean toSummary) {
        double visa = safeDouble(etVisa);
        double transport = safeDouble(etTransport);
        double meals = safeDouble(etMeals);
        double custom = safeDouble(etCustom);
        if (Double.isNaN(visa) || Double.isNaN(transport) || Double.isNaN(meals) || Double.isNaN(custom)) {
            Toast.makeText(getContext(), R.string.toast_invalid_number, Toast.LENGTH_SHORT).show();
            return;
        }

        double actSum = 0;
        for (ActivityItem a : activities) if (a.selected) actSum += a.price;
        double subtotal = actSum + visa + transport + meals + custom;
    int tripCount = SessionManager.getTripCount(getContext());
        double discount = tripCount >= 3 ? subtotal * 0.10 : 0.0;
        double finalTotal = subtotal - discount;

        Trip t = new Trip();
        t.id = System.currentTimeMillis();
        t.destination = etDestination.getText() == null ? "" : etDestination.getText().toString().trim();
        t.notes = etNotes.getText() == null ? "" : etNotes.getText().toString().trim();
        t.startDate = etStartDate.getText() == null ? "" : etStartDate.getText().toString().trim();
        t.endDate = etEndDate.getText() == null ? "" : etEndDate.getText().toString().trim();
        t.activities = new ArrayList<>(activities);
        t.visa = visa; t.transport = transport; t.meals = meals; t.custom = custom;
        t.subtotal = subtotal; t.discount = discount; t.total = finalTotal;

        TripRepository.saveTrip(getContext(), t);
        // persist last selections and increment trip count for loyalty
        StringBuilder csv = new StringBuilder();
        for (ActivityItem a : activities) if (a.selected) {
            if (csv.length() > 0) csv.append(',');
            csv.append(a.name);
        }
        SessionManager.saveLastTrip(getContext(), csv.toString(), visa, transport, meals, custom);
        SessionManager.incrementTripCount(getContext());

        if (!toSummary) {
            Toast.makeText(getContext(), R.string.toast_trip_saved, Toast.LENGTH_SHORT).show();
        } else {
            Bundle b = new Bundle();
            b.putString("destination", t.destination);
            b.putString("start", t.startDate);
            b.putString("end", t.endDate);
            b.putString("notes", t.notes);
            b.putDouble("subtotal", t.subtotal);
            b.putDouble("discount", t.discount);
            b.putDouble("total", t.total);
            NavHostFragment.findNavController(this).navigate(R.id.nav_budget_summary, b);
        }
        recalc();
    }

    private void preloadLastSelections() {
        String csv = SessionManager.getLastActivitiesCsv(getContext());
        if (csv != null && !csv.isEmpty()) {
            String[] parts = csv.split(",");
            for (ActivityItem a : activities) {
                for (String p : parts) if (a.name.equals(p)) { a.selected = true; break; }
            }
        }
        etVisa.setText(String.valueOf(SessionManager.getLastVisa(getContext())));
        etTransport.setText(String.valueOf(SessionManager.getLastTransport(getContext())));
        etMeals.setText(String.valueOf(SessionManager.getLastMeals(getContext())));
        etCustom.setText(String.valueOf(SessionManager.getLastCustom(getContext())));
    }

    private static class PlainWatcher implements TextWatcher {
        private final Runnable onChange;
        PlainWatcher(Runnable r) { onChange = r; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) { onChange.run(); }
    }
}
