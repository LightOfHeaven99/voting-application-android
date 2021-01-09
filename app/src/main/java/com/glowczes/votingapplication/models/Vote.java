package com.glowczes.votingapplication.models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Vote {
    public String id = "";
    public String name = "";
    public ArrayList<Candidate> candidate_list = new ArrayList<>();
    public boolean isOpen = true;
    public ArrayList<String> votersId = new ArrayList<>();

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("candidate_list", candidate_list);
        result.put("isOpen", isOpen);
        result.put("id", id);
        return result;
    }
}