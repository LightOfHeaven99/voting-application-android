package com.glowczes.votingapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.glowczes.votingapplication.models.Candidate;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class NewVoteFragment extends Fragment {
    private ArrayList<Candidate> candidates = new ArrayList<>();
    RecyclerView.Adapter<CandidateViewHolder> candidatesAdapter = new CandidatesAdapter(candidates);
    ItemTouchHelper stdc = new ItemTouchHelper(new SwipeToDeleteCallback((CandidatesAdapter) candidatesAdapter));
    RecyclerView rc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_vote, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rc = requireView().findViewById(R.id.add_new_vote_recycler_view);
        rc.setAdapter(candidatesAdapter);
        Button btn = requireView().findViewById(R.id.add_new_vote_add_candidate_button);
        stdc.attachToRecyclerView(rc);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CandidatesAdapter)candidatesAdapter).addCandidate(resetCandidate());
            }
        });
    }

    private Candidate resetCandidate() {
        View layout = requireView().findViewById(R.id.add_new_vote_add_candidate);
        TextView candidateName = layout.findViewById(R.id.add_candidate_item_name);
        TextView candidateAbout = layout.findViewById(R.id.add_candidate_item_about);

        //narazie zdjęcia nie są obsługiwane
        ImageView candidateProfile = layout.findViewById(R.id.add_candidate_item_profile);

        Candidate candidate = new Candidate();
        candidate.about = candidateAbout.getText().toString();
        candidate.name = candidateName.getText().toString();

        candidateName.setText(null);
        candidateAbout.setText(null);

        return candidate;
    }
}

class CandidatesAdapter extends RecyclerView.Adapter<CandidateViewHolder> {
    private ArrayList<Candidate> c;

    public CandidatesAdapter(ArrayList<Candidate> candidates) {
        c = candidates;
    }

    public void addCandidate(Candidate newCandidate) {
        c.add(newCandidate);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        if(c.remove(position) != null)
            notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View container = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.canditade_item, parent, false);

        return new CandidateViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        holder.bind(c.get(position));
    }

    @Override
    public int getItemCount() {
        return c.size();
    }
}

class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private CandidatesAdapter adapter;

    public SwipeToDeleteCallback(CandidatesAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        adapter.deleteItem(position);
    }
}

class CandidateViewHolder extends RecyclerView.ViewHolder {
    public CandidateViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(Candidate c) {
        ImageView profile = itemView.findViewById(R.id.candidate_item_profile);
        TextView name = itemView.findViewById(R.id.candidate_item_name);
        TextView about = itemView.findViewById(R.id.candidate_item_about);

        name.setText(c.name);
        about.setText(c.about);
    }
}
