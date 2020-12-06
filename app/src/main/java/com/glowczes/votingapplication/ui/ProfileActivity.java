package com.glowczes.votingapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.glowczes.votingapplication.InitActivity;
import com.glowczes.votingapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = (Button) getView().findViewById(R.id.profile_logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                getActivity().startActivity(new Intent(getActivity(), InitActivity.class));
                getActivity().finishAffinity();
                Toast.makeText(getContext(), "Wylogowano", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
