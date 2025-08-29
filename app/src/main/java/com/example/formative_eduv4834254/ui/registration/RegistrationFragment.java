package com.example.formative_eduv4834254.ui.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.databinding.FragmentRegistrationBinding;
import com.example.formative_eduv4834254.data.SessionManager;

public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // If already logged in, skip to home
        if (SessionManager.isLoggedIn(requireContext())) {
            Navigation.findNavController(root).navigate(R.id.nav_home);
        }

        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.etEmail.getText() == null ? "" : binding.etEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                SessionManager.login(requireContext(), email);
                Navigation.findNavController(v).navigate(R.id.nav_home);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
