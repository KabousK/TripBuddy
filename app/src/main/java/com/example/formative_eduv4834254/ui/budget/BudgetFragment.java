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
import com.example.formative_eduv4834254.util.BudgetCalculator;
import com.example.formative_eduv4834254.util.TESManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    private TextInputEditText etDestination, etNotes, etStartDate, etEndDate, etVisa, etTransport, etMeals, etCustom;
    private TextView tvSubtotal, tvDiscount, tvFinal;
    private final List<ActivityItem> activities = new ArrayList<>();
    private boolean hasSavedThisSession = false;
    private long currentTripId = -1; // persist id across Save -> Confirm in the same session

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
        btnConfirm.setOnClickListener(v -> {
            v.setEnabled(false); // guard against rapid double taps
            saveTrip(true);
            v.postDelayed(() -> v.setEnabled(true), 600);
        });

        // If editing an existing trip, prefill
    if (getArguments() != null && getArguments().containsKey("tripId")) {
            long id = getArguments().getLong("tripId", -1);
            if (id != -1 && getContext()!=null) prefillTrip(id);
        }

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

    int tripCount = SessionManager.getTripCount(getContext());
    double subtotal = BudgetCalculator.subtotal(activities, visa, transport, meals, custom);
    double discount = (tripCount >= 3) ? subtotal * 0.10 : 0.0;
        double finalTotal = BudgetCalculator.total(subtotal, discount);

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

        // Prevent saving completely empty trips (no destination, no dates, no notes, zero costs, no activities)
        boolean anyActivity = false; for (ActivityItem a: activities) if (a.selected) { anyActivity = true; break; }
        boolean hasText = notEmpty(etDestination) || notEmpty(etNotes) || notEmpty(etStartDate) || notEmpty(etEndDate);
        boolean hasCosts = visa>0 || transport>0 || meals>0 || custom>0;
        if (!anyActivity && !hasText && !hasCosts) {
            Toast.makeText(getContext(), R.string.toast_trip_empty, Toast.LENGTH_SHORT).show();
            return;
        }

    int tripCount = SessionManager.getTripCount(getContext());
    double subtotal = BudgetCalculator.subtotal(activities, visa, transport, meals, custom);
    double discount = (tripCount >= 3) ? subtotal * 0.10 : 0.0;
    double finalTotal = BudgetCalculator.total(subtotal, discount);

    Trip t;
    long editId = getArguments()!=null? getArguments().getLong("tripId", -1) : -1;
    if (editId != -1) {
        t = TripRepository.getTrip(getContext(), editId);
        if (t==null) t = new Trip();
        t.id = editId;
        currentTripId = editId;
    } else {
        t = new Trip();
        if (currentTripId == -1) currentTripId = System.currentTimeMillis();
        t.id = currentTripId;
    }
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
    // Only count TES/Trip when creating a brand new trip for the first time in this session (not edits)
    if (editId == -1 && !hasSavedThisSession) {
        SessionManager.incrementTripCount(getContext());
        TESManager tes = new TESManager(requireContext());
        tes.addNewTrip();
        if (discount > 0) tes.addLoyaltyUsed();
        hasSavedThisSession = true;
    }

        if (!toSummary) {
            Toast.makeText(getContext(), R.string.toast_trip_saved, Toast.LENGTH_SHORT).show();
        } else {
            // Confirm: go to Trips list and clear the form for next planning session
            NavHostFragment.findNavController(this).navigate(R.id.nav_trips);
            clearForm();
            SessionManager.clearLastTrip(requireContext());
            hasSavedThisSession = false; // reset for next plan session
        }
        recalc();
    }

    private boolean notEmpty(TextInputEditText et) { return et.getText()!=null && !et.getText().toString().trim().isEmpty(); }

    private void prefillTrip(long id) {
        Trip t = TripRepository.getTrip(getContext(), id);
        if (t == null) return;
    currentTripId = id;
        etDestination.setText(t.destination);
        etNotes.setText(t.notes);
        etStartDate.setText(t.startDate);
        etEndDate.setText(t.endDate);
        etVisa.setText(String.valueOf(t.visa));
        etTransport.setText(String.valueOf(t.transport));
        etMeals.setText(String.valueOf(t.meals));
        etCustom.setText(String.valueOf(t.custom));
        // restore activities by name
        for (ActivityItem a : activities) {
            if (t.activities != null) {
                for (ActivityItem b : t.activities) if (a.name.equals(b.name) && b.selected) { a.selected = true; break; }
            }
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

    @Override public void onResume() {
        super.onResume();
        // If not editing an existing trip, ensure the form is fresh on next app cycle
        if (getArguments() == null || !getArguments().containsKey("tripId")) {
            clearForm();
            hasSavedThisSession = false;
            currentTripId = -1;
        }
    }

    private void clearForm() {
        if (etDestination!=null) etDestination.setText("");
        if (etNotes!=null) etNotes.setText("");
        if (etStartDate!=null) etStartDate.setText("");
        if (etEndDate!=null) etEndDate.setText("");
        if (etVisa!=null) etVisa.setText("");
        if (etTransport!=null) etTransport.setText("");
        if (etMeals!=null) etMeals.setText("");
        if (etCustom!=null) etCustom.setText("");
        for (ActivityItem a : activities) a.selected = false;
    currentTripId = -1;
    }

    private static class PlainWatcher implements TextWatcher {
        private final Runnable onChange;
        PlainWatcher(Runnable r) { onChange = r; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) { onChange.run(); }
    }
}
