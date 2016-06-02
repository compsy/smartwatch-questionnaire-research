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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common.model.Question;
import com.example.josetalito.questapp.R;

public class SliderFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private Toast toast;
    private SeekBar seekBar;
    private TextView textView;
    private int progress = 0;

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "SliderFragment";
    private int questionID;

    /**
     * dataPasser is the Activity that holds the current fragment.
     * Such Activity implements an interface that gathers the data sent by the Fragment.
     */
    OnDataPass dataPasser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.sliderquestion, container, false);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);

        Bundle bd = this.getArguments();
        Question question = (Question) bd.getSerializable("question");
        int maxValue = Integer.parseInt(question.getChoices().get(1));
        questionID = question.getID();

        seekBar.setMax(maxValue);
        seekBar.setClickable(true);
        seekBar.setFocusable(true);
        seekBar.setEnabled(true);

        textView = (TextView) view.findViewById(R.id.current_value);
        textView.setText(String.valueOf(maxValue/2) + "/" + seekBar.getMax());
        /*getActivity().runOnUiThread(new Runnable() {
            public void run() {
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekBar.setProgress(progress);
                        textView.setText(seekBar.getProgress() + "/" + seekBar.getMax());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        textView.setText(seekBar.getProgress() + "/" + seekBar.getMax());
                    }


                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textView.setText(seekBar.getProgress() + "/" + seekBar.getMax());
                        Log.i(TAG, "Slider moved. Value: " + seekBar.getProgress());
                        textView.setText(seekBar.getProgress() + "/" + seekBar.getMax());
                        toast = Toast.makeText(getActivity(), // ?
                                "Answer registered",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 50);
                        passChoiceToActivity(questionID, String.valueOf(seekBar.getProgress()));
                        Log.i(TAG, "Slider moved. Value: " + seekBar.getProgress());
                    }
                });
            }
        });*/
        seekBar.setOnSeekBarChangeListener(this);
        return view;
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textView.setText(progress + "/" + seekBar.getMax());
        this.progress = progress;
        Log.i(TAG, "onProgressChanged - slider");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        textView.setText(progress + "/" + seekBar.getMax());
        Log.i(TAG, "onStartTrackingTouch - slider");
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        textView.setText(progress + "/" + seekBar.getMax());
        toast = Toast.makeText(this.getActivity().getApplicationContext(),
                "Answer registered",
                Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 50);
        passChoiceToActivity(questionID, String.valueOf(progress));
        Log.i(TAG, "Slider moved. Value: " + progress);
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
            Log.i(TAG, "dataPasser created in SliderFragment.");
            a = (Activity) context;
            dataPasser = (OnDataPass) a;
        }
    }

}
