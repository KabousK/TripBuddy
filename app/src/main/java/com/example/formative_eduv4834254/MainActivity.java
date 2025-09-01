package com.example.formative_eduv4834254;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.OnBackPressedCallback;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.formative_eduv4834254.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private OnBackPressedCallback backToHomeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Make Home the only top-level destination so others show Up and Back goes to Home
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    // Initialize header email and keep it fresh on destination changes
    updateHeaderEmail();

        // Intercept system Back (including gesture) on Gallery/Memories/Budget to return to Home
        backToHomeCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                boolean popped = navController.popBackStack(R.id.nav_home, false);
                if (!popped) {
                    navController.navigate(R.id.nav_home);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backToHomeCallback);

        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            boolean inTopLevelNonHome = destination.getId() == R.id.nav_gallery
                    || destination.getId() == R.id.nav_memories
                    || destination.getId() == R.id.nav_budget;
            backToHomeCallback.setEnabled(inTopLevelNonHome);
            updateHeaderEmail();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
            return true;
        } else if (item.getItemId() == R.id.action_sign_out) {
            com.example.formative_eduv4834254.data.SessionManager.logout(this);
            com.google.android.material.navigation.NavigationView navigationView = findViewById(R.id.nav_view);
            if (navigationView != null) {
                View header = navigationView.getHeaderView(0);
                if (header != null) {
                    android.widget.TextView tvEmail = header.findViewById(R.id.textView);
                    if (tvEmail != null) tvEmail.setText("");
                }
            }
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        androidx.navigation.NavOptions opts = new androidx.navigation.NavOptions.Builder()
            .setPopUpTo(R.id.mobile_navigation, true)
            .build();
        navController.navigate(R.id.nav_registration, null, opts);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
    return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void updateHeaderEmail() {
        com.google.android.material.navigation.NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView == null) return;
        View header = navigationView.getHeaderView(0);
        if (header == null) return;
        android.widget.TextView tvEmail = header.findViewById(R.id.textView);
        if (tvEmail == null) return;
        String email = com.example.formative_eduv4834254.data.SessionManager.getEmail(this);
        tvEmail.setText(email == null ? "" : email);
    }
}