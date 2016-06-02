package com.example.common.model;

import com.example.common.TypeOfQuestion;

import java.util.ArrayList;

/**
 * Created by Josetalito on 01/05/2016.
 */
public class SliderAnswer implements Answers {

    String min, max;

    public SliderAnswer(String min, String max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public ArrayList<String> getAnswers() {
        ArrayList<String> al = new ArrayList<String>(2);
        al.add(min);
        al.add(max);
        return al;
    }

    @Override
    public TypeOfQuestion getTypeOfQuestion() {
        return TypeOfQuestion.SLIDER;
    }
}
