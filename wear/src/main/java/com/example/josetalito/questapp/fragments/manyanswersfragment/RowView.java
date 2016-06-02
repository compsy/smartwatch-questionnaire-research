package com.example.josetalito.questapp.fragments.manyanswersfragment;

/**
 * Created by Josetalito on 27/04/2016.
 */
import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.josetalito.questapp.R;

public class RowView extends FrameLayout implements WearableListView.OnCenterProximityListener {

    private ImageView image;
    private TextView text;

    public RowView(Context context) {
        super(context);
        View.inflate(context, R.layout.list_item, this);
        //image = (ImageView) findViewById(R.id.icon);
        text = (TextView) findViewById(R.id.answer_choice);
    }

    // TODO: to fix later when text does not fit on the rescaled item
    @Override
    public void onCenterPosition(boolean b) {
        //image.animate().scaleX(1.2f).scaleY(1.2f).alpha(1).setDuration(200);
        text.animate().scaleX(1.2f).scaleY(1.2f).alpha(1).setDuration(200);
    }

    @Override
    public void onNonCenterPosition(boolean b) {
        //image.animate().scaleX(1f).scaleY(1f).alpha(0.6f).setDuration(200);
        text.animate().scaleX(1f).scaleY(1f).alpha(0.6f).setDuration(200);
    }

    public ImageView getImage() {
        return image;
    }

    public TextView getText() {
        return text;
    }
}
