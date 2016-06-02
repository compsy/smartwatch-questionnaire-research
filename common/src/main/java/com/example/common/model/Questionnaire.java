package com.example.common.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * A Questionnaire is the core data structure. They are sent to the wearable.
 * They store and manage Question objects.
 * Created by Josetalito on 15/04/2016.
 */
public class Questionnaire implements Serializable {

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "Questionnaire";

    private String questionnaireKey;
    private ArrayList<Question> questions;
    private Date startTime;
    private Date endTime;

    public Questionnaire(int n) {
        questions = new ArrayList<Question>(n);
        questionnaireKey = new String();
    };

    public Questionnaire(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestion(ArrayList<Question> questions) { this.questions = questions;
    }

    public void addQuestion(Question question) {
        Log.i(TAG, "Questionnaire size: " + questions.size());
        int theID = questions.size();
        question.setID(theID+1);
        questions.add(question);
        Log.i(TAG, "QuestionID: " + (theID + 1));
    }

    public String getQuestionnaireKey() {
        return questionnaireKey;
    }

    public void setQuestionnaireKey(String questionnaireKey) {
        this.questionnaireKey = questionnaireKey;
    }
}

