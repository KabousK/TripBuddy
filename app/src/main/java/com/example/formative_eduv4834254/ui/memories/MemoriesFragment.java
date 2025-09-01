package com.example.formative_eduv4834254.ui.memories;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.data.Memory;
import com.example.formative_eduv4834254.data.TripRepository;
import com.example.formative_eduv4834254.data.MemoryDao;
import com.example.formative_eduv4834254.util.TESManager;

import java.io.IOException;

public class MemoriesFragment extends Fragment {

    private Uri photoUri;
    private Uri audioUri;
    private MediaPlayer mediaPlayer;
    private ImageView ivPreview;
    private EditText etCaption, etLocation, etMood;

    private final ActivityResultLauncher<String[]> pickImage = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    // Persist read access so Gallery can load later
                    try {
                        requireContext().getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException ignore) { }
                    photoUri = uri;
                    if (ivPreview != null) {
                        ivPreview.setImageURI(uri);
                        ivPreview.setAlpha(0f);
                        ivPreview.animate().alpha(1f).setDuration(250).start();
                    }
                }
            }
    );

    private final ActivityResultLauncher<String[]> pickAudio = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    try {
                        requireContext().getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException ignore) { }
                    audioUri = uri;
                    Toast.makeText(requireContext(), getString(R.string.btn_pick_audio), Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_memories, container, false);

        ivPreview = root.findViewById(R.id.iv_preview);
    etCaption = root.findViewById(R.id.et_caption);
    etLocation = root.findViewById(R.id.et_location);
    etMood = root.findViewById(R.id.et_mood);

    root.findViewById(R.id.btn_pick_photo).setOnClickListener(v -> pickImage.launch(new String[]{"image/*"}));
    root.findViewById(R.id.btn_pick_audio).setOnClickListener(v -> pickAudio.launch(new String[]{"audio/*"}));

        root.findViewById(R.id.btn_play).setOnClickListener(v -> play());
        root.findViewById(R.id.btn_pause).setOnClickListener(v -> pause());
        root.findViewById(R.id.btn_stop).setOnClickListener(v -> stop());

        root.findViewById(R.id.btn_save_memory).setOnClickListener(v -> saveMemory(v));

        return root;
    }

    private void saveMemory(View anchor) {
        if (photoUri == null) {
            Toast.makeText(requireContext(), "Please pick a photo.", Toast.LENGTH_SHORT).show();
            return;
        }
        Memory m = new Memory();
        m.id = System.currentTimeMillis();
        m.caption = etCaption != null ? String.valueOf(etCaption.getText()) : "";
    m.photoUri = photoUri.toString();
        m.audioUri = audioUri != null ? audioUri.toString() : null;
        m.createdAt = System.currentTimeMillis();
    m.location = etLocation != null && etLocation.getText() != null ? etLocation.getText().toString() : null;
    m.mood = etMood != null && etMood.getText() != null ? etMood.getText().toString() : null;
    // Save to SQLite for Deliverable 5
    new MemoryDao(requireContext()).insert(m);
    // keep existing simple store for gallery fallback
    TripRepository.addMemory(requireContext(), m);
    // TES: memory with photo event
    new TESManager(requireContext()).addMemoryWithPhoto();
        Toast.makeText(requireContext(), "Memory saved.", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(anchor).navigate(R.id.nav_gallery);
    }

    private void play() {
        if (audioUri == null) {
            Toast.makeText(requireContext(), "Pick music first.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(requireContext(), audioUri);
                mediaPlayer.setOnCompletionListener(mp -> stop());
                mediaPlayer.prepare();
            }
            mediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Unable to play audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stop();
    }
}
