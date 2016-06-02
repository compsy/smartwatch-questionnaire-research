package com.example.josetalito.questapp.fragments.manyanswersfragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josetalito.questapp.R;

import java.util.ArrayList;

public class FewAnswersFragment_ManyPages extends Fragment{

    private ArrayList<ListViewChoice> choices = new ArrayList<>();
    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "FewAnswersFragment_MP";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.manychoices_severalpages, container, false);

        Bundle bd = this.getArguments();
        int limit = bd.getInt("limit");
        Log.i(TAG, "limit: " + limit);
        ArrayList<String> quest_answers = (ArrayList<String>) this.getArguments().getSerializable("choices");
        int size = quest_answers.size(); int i;
        int loop_limit= (limit*3) + 3;
        for (i=limit*3; i < (size); i++) {
            ListViewChoice ls = new ListViewChoice(/*R.id.icon, */quest_answers.get(i));
            choices.add(ls);
            Log.i(TAG, "limit: " + limit + " and choice: " + ls.text);
            if (i == loop_limit-1) break;
        }

        final TextView first_choice = (TextView) view.findViewById(R.id.first_choice);
        final TextView second_choice = (TextView) view.findViewById(R.id.second_choice);
        final TextView third_choice = (TextView) view.findViewById(R.id.third_choice);

        ArrayList<TextView> at = new ArrayList<TextView>(3);
        at.add(first_choice); at.add(second_choice); at.add(third_choice);
        i=limit*3;
        int l;
        Log.i(TAG, "Before - : " + limit + ". i: " + i + ", choices.size(): " + choices.size());
        for(l=0; l < choices.size() ; l++) {
            TextView t = at.get(l);
            t.setText(choices.get(l).text);
        }
        Log.i(TAG, "After - : " + limit);

        first_choice.setOnClickListener(onClickListener);
        second_choice.setOnClickListener(onClickListener);
        third_choice.setOnClickListener(onClickListener);
        return view;
    }

    public void registerSelection(View v){
        final Toast toast;
        int buttonID = v.getId();
        switch(buttonID) {
            case(R.id.first_choice):
                toast = Toast.makeText(this.getActivity().getApplicationContext(),
                        "Option selected",
                        Toast.LENGTH_SHORT);
                Log.i(TAG, "First selected.");
                break;
            case(R.id.no_bttn):
                toast = Toast.makeText(this.getActivity().getApplicationContext(),
                        "Option selected",
                        Toast.LENGTH_SHORT);
                Log.i(TAG, "Second clicked.");
                break;
            default: // unsure button
                toast = Toast.makeText(this.getActivity().getApplicationContext(),
                        "Option selected",
                        Toast.LENGTH_SHORT);
                Log.i(TAG, "Third clicked.");
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
