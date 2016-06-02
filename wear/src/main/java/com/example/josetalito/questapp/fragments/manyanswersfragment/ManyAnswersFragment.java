package com.example.josetalito.questapp.fragments.manyanswersfragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.common.model.Question;
import com.example.josetalito.questapp.R;
import com.example.josetalito.questapp.fragments.OnDataPass;

import java.util.ArrayList;

public class ManyAnswersFragment extends Fragment implements WearableListView.ClickListener {

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "ManyAnswersFragment";

    private ArrayList<ListViewChoice> choices = new ArrayList<>();
    private WearableListView wearView;
    private int questionID;

    /**
     * dataPasser is the Activity that holds the current fragment.
     * Such Activity implements an interface that gathers the data sent by the Fragment.
     */
    OnDataPass dataPasser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manychoices, container, false);
        wearView = (WearableListView) view.findViewById(R.id.wearable_list);

        Bundle bd = this.getArguments();
        Question question = (Question) bd.getSerializable("question");

        ArrayList<String> quest_answers = (ArrayList<String>) question.getChoices();
        int limit = quest_answers.size(); int i;
        for (i=0; i < limit; i++) {
            ListViewChoice ls = new ListViewChoice(/*R.id.icon, */quest_answers.get(i));
            choices.add(ls);
        }
        wearView.setAdapter(new ListViewAdapter(this.getActivity().getApplicationContext(), choices));
        wearView.setClickListener(this);
        questionID = this.getArguments().getInt("questionid");
        return view;
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        final Toast toast;
        String choice = new String();
        choice = choices.get(viewHolder.getLayoutPosition()).text;
        toast = Toast.makeText(this.getActivity().getApplicationContext(),
                choice,
                Toast.LENGTH_SHORT);
        toast.show();
        Log.i(TAG, choice + " clicked.");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 50);
        passChoiceToActivity(questionID, choice);
        // TODO: change image of clicked element
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    private class ListViewAdapter extends WearableListView.Adapter  {

        /**
         * Debugging tag for logging messages.
         */
        private static final String TAG = "WearListViewAdapter";

        private Context context;
        private ArrayList<ListViewChoice> listViewChoices;

        public ListViewAdapter(Context context, ArrayList<ListViewChoice> listViewChoices) {
            this.context = context;
            this.listViewChoices = listViewChoices;
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(new RowView(context));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
            RowView listViewRowView = (RowView) viewHolder.itemView;
            final ListViewChoice listViewItem = listViewChoices.get(i);

            //listViewRowView.getImage().setImageResource(listViewItem.icon);
            listViewRowView.getText().setText(listViewItem.text.toString());
        }

        @Override
        public int getItemCount() {
            return listViewChoices.size();
        }
    }

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
