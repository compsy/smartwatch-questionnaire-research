package com.example.josetalito.questapp.model;

import com.example.common.TypeOfQuestion;

import java.util.ArrayList;

/**
 * Created by Josetalito on 01/05/2016.
 */
public class ManyAnswers implements Answers {

    ArrayList<String> choices;

    public ManyAnswers() {
        choices = new ArrayList<String>();
    }

    @Override
    public ArrayList<String> getAnswers() {
        return choices;
    }

    @Override
    public TypeOfQuestion getTypeOfQuestion() {
        return TypeOfQuestion.MANY_ANSWERS;
    }
}
