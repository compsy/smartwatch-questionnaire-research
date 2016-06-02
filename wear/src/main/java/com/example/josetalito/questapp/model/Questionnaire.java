package com.example.josetalito.questapp.model;

import com.example.josetalito.questapp.model.Question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * A Questionnaire is the core data structure. They are sent to/sent back between the handheld and
 * the wearable. They store and manage Question objects.
 * Created by Josetalito on 15/04/2016.
 */
public class Questionnaire implements Serializable {

    private String questionnaireKey;
    private ArrayList<Question> questions;
    private Date startTime;
    private Date endTime;

    public Questionnaire(int n) {
        questions = new ArrayList<Question>(n);
    };

    public Questionnaire(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestion(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public String getQuestionnaireKey() {
        return questionnaireKey;
    }

    public void setQuestionnaireKey(String questionnaireKey) {
        this.questionnaireKey = questionnaireKey;
    }
}

