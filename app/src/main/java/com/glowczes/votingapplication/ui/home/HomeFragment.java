package com.glowczes.votingapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.glowczes.votingapplication.R;
import com.glowczes.votingapplication.VotingDetailsFragment;
import com.glowczes.votingapplication.VotingFragment;
import com.glowczes.votingapplication.models.Vote;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    RecyclerView.Adapter<VotingViewHolder> votesAdapter = new VotingsAdapter(new ArrayList<Vote>(), this);
    private HomeViewModel homeViewModel;
    RecyclerView rc;


    @Override
    public void onStart() {
        super.onStart();
        ((VotingsAdapter)votesAdapter).clear();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rc = requireView().findViewById(R.id.home_fragment_list);
        getVotes();

        rc.setAdapter(votesAdapter);
    }


    private void getVotes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Votes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot x : queryDocumentSnapshots.getDocuments()) {
                    ((VotingsAdapter)votesAdapter).add(x.toObject(Vote.class));
                }
            }
        });
    }
}

class VotingsAdapter extends RecyclerView.Adapter<VotingViewHolder> {
    public ArrayList<Vote> votes;
    public HomeFragment fragment;

    public VotingsAdapter(ArrayList<Vote> votes, HomeFragment fragment) {
        this.votes = votes;
        this.fragment = fragment;
    }

    public void clear() {
        votes.clear();
        notifyDataSetChanged();
    }

    public void add(Vote vote) {
        votes.add(vote);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VotingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View container = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vote_list_item, parent, false);
        return new VotingViewHolder(container, fragment);
    }

    @Override
    public void onBindViewHolder(@NonNull VotingViewHolder holder, int position) {
        holder.bind(votes.get(position));
    }

    @Override
    public int getItemCount() {
        return votes.size();
    }
}

class VotingViewHolder extends RecyclerView.ViewHolder {

    private HomeFragment fragment;

    public VotingViewHolder(@NonNull View itemView, HomeFragment fragment) {
        super(itemView);
        this.fragment = fragment;
    }

    public void bind(final Vote vote) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final VoteView f = new VoteView();
                Bundle args = new Bundle();
                args.putString("id", vote.id);
                f.setArguments(args);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();

                db.collection("Votes")
                        .document(vote.id)
                        .collection("voters")
                        .document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                            Toast.makeText(fragment.requireContext(), "Już zagłosowałeś!", Toast.LENGTH_LONG).show();
                        else {
                            fragment.getParentFragmentManager().beginTransaction()
                                    .replace(R.id.nav_host_fragment, f)
                                    .addToBackStack(f.toString())
                                    .commit();
                        }
                    }
                });


            }
        });

        TextView text = itemView.findViewById(R.id.vote_list_item_title);
        text.setText(vote.name);
    }
}
