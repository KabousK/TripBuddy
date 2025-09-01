package com.example.formative_eduv4834254.ui.trips;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.data.Trip;
import com.example.formative_eduv4834254.data.TripRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class TripsFragment extends Fragment {
    private final List<Trip> data = new ArrayList<>();
    private TripsAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trips, container, false);
        RecyclerView rv = v.findViewById(R.id.rvTrips);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    adapter = new TripsAdapter(data, trip -> {
            Bundle b = new Bundle();
            b.putLong("tripId", trip.id);
            Navigation.findNavController(v).navigate(R.id.nav_trip_details, b);
    }, trip -> showDeleteDialog(trip.id));
        rv.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fabAddTrip);
        fab.setOnClickListener(view -> Navigation.findNavController(v).navigate(R.id.nav_budget));
    // Long press on toolbar title area could toggle selection; simpler: long-press any item starts selection (handled in adapter)
        return v;
    }

    @Override public void onResume() { super.onResume(); load(); requireActivity().invalidateOptionsMenu(); }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) { super.onCreate(savedInstanceState); setHasOptionsMenu(true); }

    @Override public void onCreateOptionsMenu(@NonNull android.view.Menu menu, @NonNull android.view.MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trips_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();
    if (id == R.id.action_select) { if (adapter!=null) adapter.setSelectionMode(!adapter.isSelectionMode()); return true; }
        if (id == R.id.action_delete_selected) { if (adapter!=null && getContext()!=null) { adapter.deleteSelected(getContext()); load(); } return true; }
        if (id == R.id.action_edit_selected) {
            if (adapter!=null) {
                Long sel = adapter.getSingleSelected();
                if (sel != null) { Bundle b = new Bundle(); b.putLong("tripId", sel); Navigation.findNavController(requireView()).navigate(R.id.nav_budget, b); }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void load() {
        data.clear();
        if (getContext() != null) data.addAll(TripRepository.getTrips(getContext()));
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(long id) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.delete, (d, which) -> {
                    TripRepository.deleteTrip(requireContext(), id);
                    load();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    static class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.VH> {
        interface OnClick { void run(Trip t); }
        interface OnDelete { void run(Trip t); }
        private final List<Trip> data; private final OnClick onClick; private final OnDelete onDelete;
        private final java.util.Set<Long> selected = new java.util.HashSet<>();
        private boolean selectionMode = false;
        void setSelectionMode(boolean on){ selectionMode = on; notifyDataSetChanged(); }
        boolean isSelectionMode(){ return selectionMode; }
        TripsAdapter(List<Trip> d, OnClick c, OnDelete del) { data=d; onClick=c; onDelete=del; }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int p) {
            Trip t = data.get(p);
            h.title.setText(t.destination == null || t.destination.isEmpty() ? h.itemView.getContext().getString(R.string.untitled_trip) : t.destination);
            h.subtitle.setText(h.itemView.getContext().getString(R.string.summary_dates, nullSafe(t.startDate), nullSafe(t.endDate)));
            h.cb.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
            h.cb.setOnCheckedChangeListener(null);
            h.cb.setChecked(selected.contains(t.id));
            h.cb.setOnCheckedChangeListener((b, ckd) -> { if (ckd) selected.add(t.id); else selected.remove(t.id); });
            h.itemView.setOnClickListener(v -> {
                if (selectionMode) { h.cb.performClick(); }
                else onClick.run(t);
            });
            h.btnOverflow.setOnClickListener(v -> {
                android.widget.PopupMenu pm = new android.widget.PopupMenu(v.getContext(), v);
                pm.getMenuInflater().inflate(R.menu.menu_trip_item, pm.getMenu());
                pm.setOnMenuItemClickListener(mi -> {
                    int id = mi.getItemId();
                    if (id == R.id.action_edit) {
                        Bundle b = new Bundle(); b.putLong("tripId", t.id);
                        Navigation.findNavController(v).navigate(R.id.nav_budget, b);
                        return true;
                    } else if (id == R.id.action_delete) {
                        onDelete.run(t);
                        return true;
                    }
                    return false;
                });
                pm.show();
            });
            h.itemView.setOnLongClickListener(v -> { setSelectionMode(true); return true; });
        }
        private String nullSafe(String s){ return s==null?"":s; }
        @Override public int getItemCount(){ return data.size(); }
        void deleteSelected(android.content.Context c) {
            for (Long id : new java.util.HashSet<>(selected)) { TripRepository.deleteTrip(c, id); }
            selected.clear();
        }
        Long getSingleSelected() { return selected.size()==1 ? selected.iterator().next() : null; }
    static class VH extends RecyclerView.ViewHolder {
            TextView title, subtitle; View btnOverflow; android.widget.CheckBox cb;
            VH(@NonNull View item) { super(item); title=item.findViewById(R.id.tvTripTitle); subtitle=item.findViewById(R.id.tvTripSubtitle); btnOverflow=item.findViewById(R.id.btnOverflow); cb=item.findViewById(R.id.cbSelect); }
        }
    }
}
