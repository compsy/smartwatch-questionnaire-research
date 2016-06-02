package com.example.josetalito.questapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.common.model.Question;
import com.example.josetalito.questapp.R;

public class FewAnswersFragment extends Fragment {

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "FewAnswersFragment";

    private int questionID;

    /**
     * dataPasser is the Activity that holds the current fragment.
     * Such Activity implements an interface that gathers the data sent by the Fragment.
     */
    OnDataPass dataPasser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.yesnoquestion, container, false);

        final Button yes_bttn = (Button) view.findViewById(R.id.yes_bttn);
        final Button no_bttn = (Button) view.findViewById(R.id.no_bttn);
        final Button unsure_bttn = (Button) view.findViewById(R.id.unsure_bttn);

        yes_bttn.setOnClickListener(onClickListener);
        no_bttn.setOnClickListener(onClickListener);
        unsure_bttn.setOnClickListener(onClickListener);

        Bundle bd = this.getArguments();
        Question question = (Question) bd.getSerializable("question");
        int noOfQuestions = question.getChoices().size() == 2? 2 : 3;
        if (noOfQuestions == 2) unsure_bttn.setVisibility(View.GONE);
        questionID = question.getID();
        return view;
    }

    private void registerSelection(View v){
        final Toast toast;
        int buttonID = v.getId();
        String choice;
        Context cxt = this.getActivity().getApplicationContext();
        switch(buttonID) {
            case(R.id.yes_bttn):
                choice = cxt.getString(R.string.bttn_ok);
                toast = Toast.makeText(cxt,
                        choice + " selected",
                        Toast.LENGTH_SHORT);
                Log.i(TAG, choice + " clicked.");
                break;
            case(R.id.no_bttn):
                choice = cxt.getString(R.string.bttn_not_ok);
                toast = Toast.makeText(cxt,
                        choice + " selected",
                        Toast.LENGTH_SHORT);
                Log.i(TAG, choice + " clicked.");
                break;
            default: // unsure button
                choice = cxt.getString(R.string.bttn_unsure);
                toast = Toast.makeText(cxt,
                        choice + " selected",
                        Toast.LENGTH_SHORT);
                Log.i(TAG, choice + " clicked.");
        }
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 50);
        passChoiceToActivity(questionID, choice);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            registerSelection(v);
        }
    };

    public void passChoiceToActivity(int questionID, String data) {
        dataPasser.onDataPass(questionID, data);
    }

    /** Because it is still to recent and deprecated methods should not be used (Google advises so),
     * it is recommended to use both types of onAttach methods.
     */
    /** Calling from API <23 **/
    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        dataPasser = (OnDataPass) a;
    }

    /** Calling from API 23 on **/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;
        if (context instanceof Activity){
            Log.i(TAG, "dataPasser created in FewAnswersFragment.");
            a = (Activity) context;
            dataPasser = (OnDataPass) a;
        }
    }
}
