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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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