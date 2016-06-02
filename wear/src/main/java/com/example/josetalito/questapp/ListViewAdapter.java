package com.example.josetalito.questapp;

/**
 * Created by Josetalito on 27/04/2016.
 */
import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ListViewAdapter extends WearableListView.Adapter  {

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

        listViewRowView.getImage().setImageResource(listViewItem.icon);
        listViewRowView.getText().setText(listViewItem.text.toString());
    }

    @Override
    public int getItemCount() {
        return listViewChoices.size();
    }
}
