package com.moodyminji.calitrack;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.moodyminji.calitrack.TrackFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private TextView headerTitle;
    private TextView headerSubtitle;
    private BottomNavigationView bottomNavigation;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        headerTitle = findViewById(R.id.headerTitle);
        headerSubtitle = findViewById(R.id.headerSubtitle);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Set up bottom navigation listener
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_track) {
                    loadFragment(new TrackFragment());
                    updateHeaderForTrack();;
                    return true;
                } else if (itemId == R.id.nav_history) {
                    loadFragment(new HistoryFragment());
                    updateHeader("Your Health History",
                            "View your past activities");
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    loadFragment(new ProfileFragment());
                    updateHeader("Your Profile",
                            "Manage your account settings");
                    return true;
                }
                return false;
            }
        });

        // Load initial fragment (Track by default)
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_track);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void updateHeader(String title, String subtitle) {
        headerTitle.setText(title);
        headerSubtitle.setText(subtitle);
    }
    private void updateHeaderForTrack() {
        String title = "Hey Weirdo, How's your health!"; // A generic default title
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            if (userName != null && !userName.isEmpty()) {
                // If name has spaces, just take the first name
                String firstName = userName.split(" ")[0];
                title = "Hey " + firstName + ", How's your health?";
            }
        }
        updateHeader(title, "Monitor your daily health metrics");
    }

    // Public method for fragments to navigate to Track tab
    public void navigateToTrack() {
        bottomNavigation.setSelectedItemId(R.id.nav_track);
    }
}