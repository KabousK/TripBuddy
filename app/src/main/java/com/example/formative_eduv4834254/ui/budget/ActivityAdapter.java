package com.example.formative_eduv4834254.ui.budget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.data.ActivityItem;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.VH> {
    public interface OnActivityToggled { void onToggled(); }
    private final List<ActivityItem> items;
    private final OnActivityToggled listener;

    public ActivityAdapter(List<ActivityItem> items, OnActivityToggled listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        ActivityItem item = items.get(pos);
        h.cb.setText(item.name);
        h.cb.setChecked(item.selected);
        h.price.setText(h.itemView.getContext().getString(R.string.currency_format, item.price));
        h.cb.setOnCheckedChangeListener((b, checked) -> {
            item.selected = checked;
            if (listener != null) listener.onToggled();
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        CheckBox cb; TextView price;
        VH(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.cb_activity);
            price = itemView.findViewById(R.id.tv_price);
        }
    }
}
