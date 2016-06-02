package com.example.josetalito.questapp;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class FewAnswersFragment extends Fragment{

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "FewAnswersFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.yesnoquestion, container, false);

        final Button yes_bttn = (Button) view.findViewById(R.id.yes_bttn);
        final Button no_bttn = (Button) view.findViewById(R.id.no_bttn);
        final Button unsure_bttn = (Button) view.findViewById(R.id.unsure_bttn);

        yes_bttn.setOnClickListener(onClickListener);
        no_bttn.setOnClickListener(onClickListener);
        unsure_bttn.setOnClickListener(onClickListener);

        Bundle bd = this.getArguments();
        int noOfQuestions = bd.getInt("numberofquestions");
        if (noOfQuestions == 2) unsure_bttn.setVisibility(View.GONE);
        return view;
    }

    public void registerSelection(View v){
        final Toast toast;
        int buttonID = v.getId();
        switch(buttonID) {
            case(R.id.yes_bttn):
                toast = Toast.makeText(this.getActivity().getApplicationContext(),
                        "I selected",
                        Toast.LENGTH_SHORT);
                Log.i(TAG, "I clicked.");
                break;
            case(R.id.no_bttn):
                toast = Toast.makeText(this.getActivity().getApplicationContext(),
                        "X selected",
                        Toast.LENGTH_SHORT);
                Log.i(TAG, "X clicked.");
                break;
            default: // unsure button
                toast = Toast.makeText(this.getActivity().getApplicationContext(),
                        "? selected",
                        Toast.LENGTH_SHORT);
                Log.i(TAG, "? clicked.");
        }
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 50);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            registerSelection(v);
        }
    };
}
