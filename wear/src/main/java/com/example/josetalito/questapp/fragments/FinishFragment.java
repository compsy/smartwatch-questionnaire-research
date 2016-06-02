package com.example.josetalito.questapp.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.josetalito.questapp.R;
import com.example.josetalito.questapp.activities.QuestionnaireActivity;

/**
 * Created by Josetalito on 19/05/2016.
 */
public class FinishFragment extends Fragment {

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "FinishFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.finishfragment, container, false);

        final Button button_finish = (Button) view.findViewById(R.id.button_finish);

        button_finish.setOnClickListener(onClickListener);

        return view;
    }

    private boolean validateQuestionnaire(View v) {
        int NumberOfQuestions = ((QuestionnaireActivity)getActivity()).getQ().getQuestions().size();
        int NumberOfSolutions = ((QuestionnaireActivity)getActivity()).getSolutions().getSolutions().size();
        Log.i(TAG, "Validating questionnaire... " +
            "Number of questions: " + NumberOfQuestions + ", number of solutions: " + NumberOfSolutions);
        return NumberOfQuestions == NumberOfSolutions ? true : false;
    }

    private void finishQuestionnaireActivity() {
        this.getActivity().finish();
    }

    private void provideFeedback() {
        Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Answers sent!");
        // Showing some nice feedback to the user
        startActivity(intent);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Toast toast;
            boolean valid = validateQuestionnaire(v);
            if (valid == true) {
                Log.i(TAG, "The questionnaire was validated.");
                provideFeedback();
                finishQuestionnaireActivity();
            } else {
                toast = Toast.makeText(getActivity().getApplicationContext(),
                        R.string.not_validated,
                        Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 70);
                Log.i(TAG, "The questionnaire could not be validated.");
            }
        }
    };
}
