package com.glowczes.votingapplication;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.glowczes.votingapplication.ui.ProfileFragment;
import com.glowczes.votingapplication.ui.VotingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NotAdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.not_admin_activity);
        BottomNavigationView bnv = findViewById(R.id.not_admin_navigation);

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();

                switch (item.getItemId()) {
                    case R.id.not_admin_profile:
                        Fragment profileFragment = new ProfileFragment();
                        fm.beginTransaction()
                            .replace(R.id.not_admin_fragment_container, profileFragment)
                            .addToBackStack(null)
                            .commit();
                        return true;
                    case R.id.not_admin_vote:
                        Fragment votingFragment = new VotingFragment();
                        fm.beginTransaction()
                                .replace(R.id.not_admin_fragment_container, votingFragment)
                                .addToBackStack(null)
                                .commit();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
