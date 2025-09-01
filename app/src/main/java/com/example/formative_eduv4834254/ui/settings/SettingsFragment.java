package com.example.formative_eduv4834254.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.data.SessionManager;

public class SettingsFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        Switch swTheme = root.findViewById(R.id.swTheme);
        Switch swMusic = root.findViewById(R.id.swMusic);
        Spinner spLang = root.findViewById(R.id.spLanguage);

        swTheme.setChecked(SessionManager.isDarkTheme(requireContext()));
        swMusic.setChecked(SessionManager.isMusicEnabled(requireContext()));
        String[] langs = {"en","fr","es"};
        spLang.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, langs));
        String current = SessionManager.getLanguage(requireContext());
        for (int i=0;i<langs.length;i++) if (langs[i].equals(current)) spLang.setSelection(i);

        swTheme.setOnCheckedChangeListener((CompoundButton b, boolean checked) -> {
            SessionManager.setDarkTheme(requireContext(), checked);
            AppCompatDelegate.setDefaultNightMode(checked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
        swMusic.setOnCheckedChangeListener((b, checked) -> SessionManager.setMusicEnabled(requireContext(), checked));
        root.findViewById(R.id.btnSaveLang).setOnClickListener(v -> {
            String lang = (String) spLang.getSelectedItem();
            SessionManager.setLanguage(requireContext(), lang);
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang));
            requireActivity().recreate();
        });
        return root;
    }
}
