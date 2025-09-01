package com.example.formative_eduv4834254.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.formative_eduv4834254.R;
import com.example.formative_eduv4834254.databinding.FragmentHomeBinding;
import com.example.formative_eduv4834254.data.SessionManager;
import com.example.formative_eduv4834254.util.TESManager;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

                binding = FragmentHomeBinding.inflate(inflater, container, false);
                View root = binding.getRoot();

                // Welcome headline and stats
                homeViewModel.getText().observe(getViewLifecycleOwner(), s -> {
                        int trips = SessionManager.getTripCount(requireContext());
                        int tes = new TESManager(requireContext()).getScore();
                        binding.tvWelcome.setText(R.string.home_welcome);
                        binding.tvStats.setText("Trips: " + trips + "   TES: " + tes);
                });

                // Optional: attempt to load user-provided logo named "Trip Buddy Logo.png" in app assets if present.
                // If not present, preview uses launcher icon via tools:srcCompat in XML.

        // Button navigation
        binding.btnViewGallery.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_gallery));
        binding.btnPlanTrip.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_budget));
        binding.btnCreateMemory.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.nav_memories));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}