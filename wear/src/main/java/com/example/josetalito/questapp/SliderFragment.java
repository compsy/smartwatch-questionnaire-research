package com.example.josetalito.questapp;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SliderFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private Toast toast;
    private SeekBar seekBar;
    private TextView textView;
    private int progress = 0;

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "SliderFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.sliderquestion, container, false);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        int maxValue = this.getArguments().getInt("MaxValue");
        seekBar.setMax(maxValue);
        textView = (TextView) view.findViewById(R.id.current_value);
        textView.setText(String.valueOf(maxValue/2) + "/" + seekBar.getMax());
        seekBar.setOnSeekBarChangeListener(this);
        return view;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textView.setText(progress + "/" + seekBar.getMax());
        this.progress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        textView.setText(progress + "/" + seekBar.getMax());
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        textView.setText(progress + "/" + seekBar.getMax());
        toast = Toast.makeText(this.getActivity().getApplicationContext(),
                "Answer registered.",
                Toast.LENGTH_SHORT);
        toast.show();
        Log.i(TAG, "Slider moved. Value: " + progress);
    }

}
