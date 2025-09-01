package com.example.formative_eduv4834254.ui.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.activity.OnBackPressedCallback;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.databinding.FragmentRegistrationBinding;
import com.example.formative_eduv4834254.data.SessionManager;

public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // If already logged in, skip to home once NavController is available
        if (SessionManager.isLoggedIn(requireContext())) {
            NavHostFragment.findNavController(this).navigate(R.id.nav_home);
            return;
        }

        // Disable back navigation while on registration screen
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { /* swallow back */ }
        });

        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.etEmail.getText() == null ? "" : binding.etEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                SessionManager.login(requireContext(), email);
                NavHostFragment.findNavController(this).navigate(R.id.nav_home);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
