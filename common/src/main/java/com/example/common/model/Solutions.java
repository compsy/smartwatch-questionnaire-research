package com.example.common.model;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;


/**
 * A Solutions object stores the logged answers per question by the user.
 * They are sent to the handheld.
 * Created by Josetalito on 19/05/2016.
 */
public class Solutions implements Serializable {

    private static final String TAG = "Solutions";

    private String questionnaireKey;

    public HashMap<Integer, String> getSolutions() {
        return solutions;
    }

    public void setSolutions(HashMap<Integer, String> solutions) {
        this.solutions = solutions;
    }

    private HashMap<Integer, String> solutions;

    public Solutions(String questionnaireKey, int n) {
        Log.i(TAG, "Solutions created");
        this.questionnaireKey = questionnaireKey;
        solutions = new HashMap<>(n);
    }

}
