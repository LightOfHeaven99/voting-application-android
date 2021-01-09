package com.glowczes.votingapplication;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.glowczes.votingapplication.R;
import com.glowczes.votingapplication.models.Vote;
import com.glowczes.votingapplication.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VotingFragment extends Fragment {
    RecyclerView.Adapter<VotingViewHolder> votesAdapter = new VotingsAdapter(new ArrayList<Vote>(), this);
    RecyclerView rc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vote_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((VotingsAdapter)votesAdapter).clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        rc = requireView().findViewById(R.id.vote_fragment_list);


        rc.setAdapter(votesAdapter);

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
    public VotingFragment fragment;

    public VotingsAdapter(ArrayList<Vote> votes, VotingFragment fragment) {
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

    private VotingFragment fragment;

    public VotingViewHolder(@NonNull View itemView, VotingFragment fragment) {
        super(itemView);
        this.fragment = fragment;
    }

    public void bind(final Vote vote) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final VotingDetailsFragment f = new VotingDetailsFragment();
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
                                    .replace(R.id.not_admin_fragment_container, f)
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

