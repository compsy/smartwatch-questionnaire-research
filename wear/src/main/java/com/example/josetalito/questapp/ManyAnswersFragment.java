package com.example.josetalito.questapp;

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

import java.util.ArrayList;

public class ManyAnswersFragment extends Fragment implements WearableListView.ClickListener {

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "ManyAnswersFragment";

    private ArrayList<ListViewChoice> choices = new ArrayList<>();
    private WearableListView wearView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manychoices, container, false);
        wearView = (WearableListView) view.findViewById(R.id.wearable_list);
        ArrayList<String> quest_answers = (ArrayList<String>) this.getArguments().getSerializable("choices");
        int limit = quest_answers.size(); int i;
        for (i=0; i < limit; i++) {
            ListViewChoice ls = new ListViewChoice(/*R.id.icon, */quest_answers.get(i));
            choices.add(ls);
        }
        wearView.setAdapter(new ListViewAdapter(this.getActivity().getApplicationContext(), choices));
        wearView.setClickListener(this);
        return view;
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        final Toast toast;
        toast = Toast.makeText(this.getActivity().getApplicationContext(),
                choices.get(viewHolder.getLayoutPosition()).text,
                Toast.LENGTH_SHORT);
        toast.show();
        Log.i(TAG, choices.get(viewHolder.getLayoutPosition()).text + " clicked.");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 50);
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
}
