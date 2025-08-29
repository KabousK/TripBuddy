package com.example.formative_eduv4834254.ui.gallery;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.formative_eduv4834254.R;

import java.io.IOException;

public class MemoryViewerFragment extends Fragment {
    private ImageView iv;
    private TextView tv;
    private MediaPlayer mp;
    private Uri audioUri;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memory_viewer, container, false);
        iv = v.findViewById(R.id.ivFull);
        tv = v.findViewById(R.id.tvCaption);
        View btnPlay = v.findViewById(R.id.btnPlay);
        View btnPause = v.findViewById(R.id.btnPause);
        View btnStop = v.findViewById(R.id.btnStop);

        Bundle args = getArguments();
        if (args != null) {
            String p = args.getString("photoUri");
            String a = args.getString("audioUri");
            String c = args.getString("caption");
            if (p != null) {
                iv.setImageURI(Uri.parse(p));
                iv.setAlpha(0f);
                iv.animate().alpha(1f).setDuration(250).start();
            }
            if (c != null) tv.setText(c);
            if (a != null) audioUri = Uri.parse(a);
        }

        btnPlay.setOnClickListener(v1 -> play());
        btnPause.setOnClickListener(v12 -> pause());
        btnStop.setOnClickListener(v13 -> stop());

        // tap to close
        iv.setOnClickListener(v14 -> requireActivity().onBackPressed());
        return v;
    }

    private void play() {
        if (audioUri == null) {
            Toast.makeText(requireContext(), "No audio", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (mp == null) {
                mp = new MediaPlayer();
                mp.setDataSource(requireContext(), audioUri);
                mp.setOnCompletionListener(m -> stop());
                mp.prepare();
            }
            mp.start();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Unable to play", Toast.LENGTH_SHORT).show();
        }
    }

    private void pause() {
        if (mp != null && mp.isPlaying()) mp.pause();
    }

    private void stop() {
        if (mp != null) { mp.stop(); mp.release(); mp = null; }
    }

    @Override public void onStop() { super.onStop(); stop(); }
}
