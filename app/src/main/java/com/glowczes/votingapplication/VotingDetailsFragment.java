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

import com.glowczes.votingapplication.models.Candidate;
import com.glowczes.votingapplication.models.Vote;
import com.glowczes.votingapplication.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VotingDetailsFragment extends Fragment {
    RecyclerView.Adapter<DetailsCandidateViewHolder> candidatesAdapter = new DetailsCandidatesAdapter(new ArrayList<Candidate>());
    ItemTouchHelper stdc = new ItemTouchHelper(new SimpleItemTouchHelperCallback(candidatesAdapter));
    RecyclerView rc;
    Button button;
    String id;
    Vote mVote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.voting_details_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((DetailsCandidatesAdapter)candidatesAdapter).clear();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();

        if (args.containsKey("id")) {
            id = args.getString("id");
            button = requireView().findViewById(R.id.vote_fragment_button);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            rc = requireView().findViewById(R.id.voting_details_fragment_list);
            rc.setAdapter(candidatesAdapter);
            stdc.attachToRecyclerView(rc);
            setupButton();
            db.collection("Votes").document(args.getString("id")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Vote vote = documentSnapshot.toObject(Vote.class);
                    mVote = vote;
                    for (Candidate c : vote.candidate_list) {
                        ((DetailsCandidatesAdapter)candidatesAdapter).addCandidate(c);
                    }
                }
            });
            Toast.makeText(getContext(), args.getString("id"), Toast.LENGTH_LONG).show();
        }
    }

    private void setupButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRequest();
            }
        });
    }

    private void performRequest() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (id == null) {
            Toast.makeText(getContext(), "Nie udało się zagłosować", Toast.LENGTH_LONG).show();
            return;
        }
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String, Object> result = new HashMap<>();
        ArrayList<Candidate> newCandidates = new ArrayList<>();

        boolean isAfterBlank = false;
        ArrayList<Candidate> candidates = ((DetailsCandidatesAdapter)candidatesAdapter).c;

        for(int i = 0; i < candidates.size(); i++) {
            int additionalVotes = candidates.size() - i;
            Candidate c = new Candidate();
            String name = candidates.get(i).name;
            c.name = name;

            if(name.toLowerCase().equals("blank")) {
                isAfterBlank = true;
            }

            c.voteCount =  isAfterBlank ? candidates.get(i).voteCount : candidates.get(i).voteCount + additionalVotes;
            if (name.toLowerCase().equals("blank")) {
                c.voteCount = candidates.get(i).voteCount + additionalVotes;
            }

            newCandidates.add(c);
        }

        result.put("candidate_list", newCandidates);
        mVote.candidate_list = newCandidates;

        db.
             collection("Votes")
            .document(id)
            .set(mVote.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db
                                .collection("Votes")
                                .document(id)
                                .collection("voters")
                                .document(user.getUid()).set(mVote.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.not_admin_fragment_container, new VotingFragment())
                                                .addToBackStack(null)
                                                .commit();
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
                .inflate(R.layout.canditade_item, parent, false);

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

class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    RecyclerView.Adapter<DetailsCandidateViewHolder> adapter;
    int dragFrom = -1;
    int dragTo = -1;
    public SimpleItemTouchHelperCallback(RecyclerView.Adapter<DetailsCandidateViewHolder> adapter) {
        this.adapter = adapter;
    }
    @Override public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragflags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragflags,0);
    }

    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        if(viewHolder.getItemViewType() != target.getItemViewType()){
            return false;
        }

        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();


        if(dragFrom == -1) {
            dragFrom =  fromPosition;
        }

        dragTo = toPosition;

        if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            reallyMoved(dragFrom, dragTo);
            dragFrom = dragTo = -1;
        }

        // and notify the adapter that its dataset has changed
        adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        //nestedScrollView.requestDisallowInterceptTouchEvent(false);
        //recyclerView.setNestedScrollingEnabled(false);

        return true;
    }

    private void reallyMoved(int dragFrom, int dragTo) {
        if(dragFrom == 0 || dragTo == ((DetailsCandidatesAdapter)adapter).c.size()){
            return;
        }
        ArrayList<Candidate> w = ((DetailsCandidatesAdapter)adapter).c;
        Collections.swap(w, dragFrom, dragTo == -1 ? 0 : dragTo);
//        adapter.notifyItemMoved(dragFrom, dragTo == -1 ? 0 : dragTo);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }
}

class DetailsCandidateViewHolder extends RecyclerView.ViewHolder {
    public DetailsCandidateViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Candidate c) {
        TextView name = itemView.findViewById(R.id.candidate_item_name);

        name.setText(c.name);
    }

}