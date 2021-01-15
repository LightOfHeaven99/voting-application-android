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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.glowczes.votingapplication.R;
import com.glowczes.votingapplication.models.Candidate;
import com.glowczes.votingapplication.models.Vote;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VoteView extends Fragment {
    RecyclerView.Adapter<DetailsCandidateViewHolder> candidatesAdapter = new DetailsCandidatesAdapter(new ArrayList<Candidate>());
    String id;
    RecyclerView rc;
    Vote mVote;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vote_resouts_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int x = 10;
        Bundle args = getArguments();
        int y = 10;

        if (args.containsKey("id")) {
            id = args.getString("id");
//            button = requireView().findViewById(R.id.vote_fragment_button);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            rc = requireView().findViewById(R.id.vote_results_fragment_list);
            rc.setAdapter(candidatesAdapter);
            setupButton();
            db.collection("Votes").document(args.getString("id")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Vote vote = documentSnapshot.toObject(Vote.class);
                    mVote = vote;
                    if(vote != null) {
                        for (Candidate c : vote.candidate_list) {
                            ((DetailsCandidatesAdapter)candidatesAdapter).addCandidate(c);
                        }
                    }
                }
            });
        }
    }

    private void setupButton() {
        Button btn = requireView().findViewById(R.id.vote_resuots_fragment_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Votes").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getParentFragmentManager().popBackStack();
                    }
                });
            }
        });
    }
}

class DetailsCandidatesAdapter extends RecyclerView.Adapter<DetailsCandidateViewHolder> {
    public ArrayList<Candidate> c;

    public DetailsCandidatesAdapter(ArrayList<Candidate> candidates) {
        c = candidates;
    }

    public void addCandidate(Candidate newCandidate) {
        c.add(newCandidate);
        Collections.sort(c, new Comparator<Candidate>(){
            public int compare(Candidate o1, Candidate o2){
                if(o1.voteCount == o2.voteCount)
                    return 0;
                return o1.voteCount < o2.voteCount ? 1 : -1;
            }
        });
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        if (c.remove(position) != null)
            notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailsCandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View container = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.candidate_details_item, parent, false);

        return new DetailsCandidateViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsCandidateViewHolder holder, int position) {
        holder.bind(c.get(position));
    }

    @Override
    public int getItemCount() {
        return c.size();
    }


    public void clear() {
        c.clear();
        notifyDataSetChanged();
    }
}

class DetailsCandidateViewHolder extends RecyclerView.ViewHolder {
    public DetailsCandidateViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Candidate c) {
        TextView name = itemView.findViewById(R.id.candidate_details_item_name);
        TextView points = itemView.findViewById(R.id.candidate_details_item_points);

        name.setText(c.name);
        points.setText(Integer.toString(c.voteCount));
    }

}