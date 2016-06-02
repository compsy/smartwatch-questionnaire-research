package com.example.josetalito.questapp.model;

import com.example.josetalito.questapp.TypeOfQuestion;

import java.util.ArrayList;

/**
 * Created by Josetalito on 01/05/2016.
 */
public class FewAnswers implements Answers {

    String pro, middle, contra;

    public FewAnswers(String pro, String contra) {
        this.pro = pro;
        this.contra = contra;
    }

    public FewAnswers(String pro, String middle, String contra) {
        this(pro, contra);
        this.middle = middle;
    }

    @Override
    public ArrayList<String> getAnswers() {
        ArrayList<String> al = new ArrayList<String>();
        al.add(pro);
        al.add(contra);
        if (middle != null) al.add(middle);
        return al;
    }

    @Override
    public TypeOfQuestion getTypeOfQuestion() {
        return TypeOfQuestion.FEW_ANSWERS;
    }
}
