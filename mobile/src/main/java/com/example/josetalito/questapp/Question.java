package com.example.josetalito.questapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Question object stores all the information required for an independent question. Questions can
 * be used to create Questionnaires. A Question cannot be managed through the system; a single-
 * Question Questionnaire is needed for that purpose.
 * Questions can be either two-choices (yes or not), or multiple-choices.
 * Created by Josetalito on 15/04/2016.
 */
public class Question implements Serializable {

    /**
     * The text to be displayed in a full screen view.
     */
    private String question;

    /**
     * Either the question is required or not before submitting the Questionnaire back.
     */
    private boolean required;

    /**
     * Defines the range of available choices.
     * e.g. yes/no, 1-5, Agree-Disagree, etc.
     */
    private ArrayList<String> choices;

    public Question(String question, boolean required, ArrayList<String> choices) {
        this.question = question;
        this.required = required;
        this.choices = choices;
    }

    /**************************
     * SETTERS AND GETTERS
     **************************/
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }
}

