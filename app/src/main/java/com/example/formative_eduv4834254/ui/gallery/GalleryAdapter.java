package com.example.formative_eduv4834254.ui.gallery;

import android.content.ContentResolver;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.data.Memory;

import java.io.IOException;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.VH> {
    public interface OnItemClick { void onClick(Memory m); }
    public interface OnItemLongClick { void onLongClick(Memory m); }

    private final List<Memory> data;
    private final OnItemClick click;
    private final OnItemLongClick longClick;

    public GalleryAdapter(List<Memory> data, OnItemClick click, OnItemLongClick longClick) {
        this.data = data;
        this.click = click;
        this.longClick = longClick;
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Memory m = data.get(pos);
        h.tv.setText(m.caption == null ? "" : m.caption);
        Uri uri = m.photoUri == null ? null : Uri.parse(m.photoUri);
        if (uri != null) {
            try {
                ContentResolver cr = h.itemView.getContext().getContentResolver();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.Source src = ImageDecoder.createSource(cr, uri);
                    h.iv.setImageDrawable(ImageDecoder.decodeDrawable(src));
                } else {
                    h.iv.setImageURI(uri);
                }
            } catch (SecurityException | IOException e) {
                h.iv.setImageResource(R.drawable.ic_menu_camera);
            }
        } else {
            h.iv.setImageResource(R.drawable.ic_menu_camera);
        }
    h.itemView.setOnClickListener(v -> click.onClick(m));
    h.itemView.setOnLongClickListener(v -> { if (longClick != null) longClick.onLongClick(m); return true; });
        h.iv.setAlpha(0f);
        h.iv.animate().alpha(1f).setDuration(200).start();
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView iv; TextView tv;
        VH(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.ivPhoto);
            tv = itemView.findViewById(R.id.tvCaption);
        }
    }
}
