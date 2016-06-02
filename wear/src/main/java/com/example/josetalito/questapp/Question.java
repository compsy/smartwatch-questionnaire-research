package com.example.josetalito.questapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

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
     * Question ID INSIDE a specific Questionnaire.
     */
    private int ID;

    /**
     * Either the question is required or not before submitting the Questionnaire back.
     */
    private boolean required;

    /**
     * Status: 0 - Not scheduled; 1 - Scheduled
     */
    private int status;

    private TypeOfQuestion type;

    private Date startTime;
    private Date endTime;

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

    public Question(String question, boolean required, ArrayList<String> choices, TypeOfQuestion type) {
        this(question, required, choices);
        this.setType(type);
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

    public String toString(){
        return this.question.toString();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public TypeOfQuestion getType() { return type; }

    public void setType(TypeOfQuestion type) { this.type = type; }

}

