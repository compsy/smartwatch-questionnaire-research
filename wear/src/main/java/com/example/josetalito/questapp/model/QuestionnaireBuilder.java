package com.example.josetalito.questapp.model;

/**
 * Created by Josetalito on 01/05/2016.
 */
public class QuestionnaireBuilder {

    private Questionnaire q;

    public QuestionnaireBuilder setUpQuestionnaire(int n) {
        q = new Questionnaire(n);
        return this;
    }

    public QuestionnaireBuilder addQuestion(String text, boolean required, Answers answers) {
        Question question = new Question(text, required, answers);
        q.addQuestion(question);
        return this;
    }

    public Questionnaire buildQuestionnaire(){
        return q;
    }
}
