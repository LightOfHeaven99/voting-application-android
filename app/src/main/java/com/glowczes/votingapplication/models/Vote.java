package com.glowczes.votingapplication.models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Vote {
    public String name = "";
    public ArrayList<Candidate> candidate_list = new ArrayList<Candidate>();

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);

        return result;
    }
}
