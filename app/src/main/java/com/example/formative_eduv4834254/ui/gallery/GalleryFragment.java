package com.example.formative_eduv4834254.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// no annotations to keep tooling simple
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.NavOptions;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.data.Memory;
import com.example.formative_eduv4834254.data.MemoryDao;
import com.example.formative_eduv4834254.data.TripRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.formative_eduv4834254.util.TESManager;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private final List<Memory> data = new ArrayList<>();
    private GalleryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        RecyclerView rv = root.findViewById(R.id.rvGallery);
        rv.setLayoutManager(new GridLayoutManager(rv.getContext(), 3));
    adapter = new GalleryAdapter(data, m -> {
            Bundle args = new Bundle();
            args.putString("photoUri", m.photoUri);
            args.putString("audioUri", m.audioUri);
            args.putString("caption", m.caption);
        NavOptions opts = new NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build();
        Navigation.findNavController(root).navigate(R.id.nav_memory_viewer, args, opts);
            new TESManager(requireContext()).addGalleryInteraction();
    }, this::onItemLongClick);
        rv.setAdapter(adapter);
        return root;
    }

    @Override public void onResume() {
        super.onResume();
        data.clear();
        if (getContext() != null) {
            data.addAll(new MemoryDao(getContext()).getAll());
            if (data.isEmpty()) data.addAll(TripRepository.getMemories(getContext())); // fallback
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void onItemLongClick(Memory m) {
        if (getContext() == null) return;
        CharSequence[] items = new CharSequence[]{ getString(R.string.edit_memory), getString(R.string.delete) };
        new MaterialAlertDialogBuilder(getContext())
                .setItems(items, (d, which) -> {
                    if (which == 0) {
                        showEditDialog(m);
                    } else if (which == 1) {
                        confirmDelete(m);
                    }
                })
                .show();
    }

    private void showEditDialog(Memory m) {
        if (getContext() == null) return;
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_memory, null);
        final android.widget.EditText etCap = dialogView.findViewById(R.id.etCaption);
        final android.widget.EditText etMood = dialogView.findViewById(R.id.etMood);
        etCap.setText(m.caption);
        etMood.setText(m.mood);
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.edit_memory)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (d, which) -> {
                    new MemoryDao(getContext()).updateBasic(m.id,
                            etCap.getText() == null ? "" : etCap.getText().toString(),
                            etMood.getText() == null ? "" : etMood.getText().toString());
                    onResume();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void confirmDelete(Memory m) {
        if (getContext() == null) return;
        new MaterialAlertDialogBuilder(getContext())
                .setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.delete, (d, w) -> {
                    new MemoryDao(getContext()).delete(m.id);
                    TripRepository.deleteMemory(getContext(), m.id);
                    onResume();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}