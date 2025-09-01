package com.example.formative_eduv4834254.ui.trips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.data.Trip;
import com.example.formative_eduv4834254.data.TripRepository;
import androidx.navigation.Navigation;

public class TripDetailsFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trip_details, container, false);
        long id = getArguments() != null ? getArguments().getLong("tripId", -1) : -1;
        Trip t = id == -1 || getContext()==null ? null : TripRepository.getTrip(getContext(), id);

        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvDates = v.findViewById(R.id.tvDates);
        TextView tvNotes = v.findViewById(R.id.tvNotes);
        TextView tvCosts = v.findViewById(R.id.tvCosts);

        if (t != null) {
            tvTitle.setText(t.destination == null || t.destination.isEmpty() ? getString(R.string.untitled_trip) : t.destination);
            tvDates.setText(getString(R.string.summary_dates, ns(t.startDate), ns(t.endDate)));
            tvNotes.setText(getString(R.string.summary_notes, ns(t.notes)));
            String costs = getString(R.string.summary_subtotal, getString(R.string.currency_format, t.subtotal)) + "\n"
                    + getString(R.string.summary_discount, getString(R.string.currency_format, t.discount)) + "\n"
                    + getString(R.string.summary_final, getString(R.string.currency_format, t.total));
            tvCosts.setText(costs);
            // Edit available via 3-dot menu in trips list; no inline edit button here.
        }
        return v;
    }
    private String ns(String s){ return s==null?"":s; }
}
