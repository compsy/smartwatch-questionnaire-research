package com.example.josetalito.questapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Questionnaire is the core data structure. They are sent to/sent back between the handheld and
 * the wearable. They store and manage Question objects.
 * Created by Josetalito on 15/04/2016.
 */
public class Questionnaire implements Serializable {

    private long ID;
    private ArrayList<Question> questions;
    // private <?> deadline;?

    public Questionnaire(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public ArrayList<Question> getQuestion() {
        return questions;
    }

    public void setQuestion(ArrayList<Question> questions) {
        this.questions = questions;
    }
}

