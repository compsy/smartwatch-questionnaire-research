package com.example.josetalito.questapp;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements DelayedConfirmationView.DelayedConfirmationListener {

    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_REQUEST_CODE = 1;
    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "WearMainActivity";

    private DismissOverlayView mDismissOverlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mDismissOverlayView = (DismissOverlayView) stub.findViewById(R.id.dismiss_overlay);
                mDismissOverlayView.showIntroIfNecessary();
            }
        });

    }

    @Override
    public void onTimerFinished(View v) {
        Log.d(TAG, "onTimerFinished is called.");
        scroll(View.FOCUS_UP);
    }

    @Override
    public void onTimerSelected(View v) {
        Log.d(TAG, "onTimerSelected is called.");
        scroll(View.FOCUS_UP);
    }

    private void scroll(final int scrollDirection) {
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(scrollDirection);
            }
        });
    }

    /**
     * Handles the button to launch a notification.
     */
    public void showNotification(View view) {
        Log.i(TAG, "showNotification called.");

        /* Questionnaire building - Test purposes */
        ArrayList<String> defaultAnswers = new ArrayList<String>(); defaultAnswers.add("yes"); defaultAnswers.add("no");
        ArrayList<String> q2Answers = new ArrayList<String>();
        q2Answers.add("Veel te druk");
        q2Answers.add("Lekker druk");
        q2Answers.add("Neutraal");
        q2Answers.add("Lekker rustig");
        q2Answers.add("Veel te rustig");
        ArrayList<String> q3Answers = new ArrayList<String>();
        q3Answers.add("0");
        q3Answers.add("100");
        // TODO: Test purposes; q1 is actually MANY_ANSWERS type
        Question q1 = new Question("Heeft u sinds het vorige meetmoment geslapen?", true, defaultAnswers, TypeOfQuestion.FEW_ANSWERS);
        Question q2 = new Question("Hoe druk heb ik het?", true, q2Answers, TypeOfQuestion.MANY_ANSWERS);
        Question q3 = new Question("Hoe gaat het op dit moment met u?", true, q3Answers, TypeOfQuestion.SLIDER);
        ArrayList<Question> questionnaire = new ArrayList<Question>(); questionnaire.add(q1); questionnaire.add(q2); questionnaire.add(q3);
        Questionnaire q = new Questionnaire(questionnaire);
        Log.i(TAG, "Questionnaire built.");

        // TODO: To change the TestActivity back to QuestionnaireActivity
        Intent questionnaireActivityIntent = new Intent(this, QuestionnaireActivity.class);
        questionnaireActivityIntent.putExtra("questionnaire", q);
        Notification notification = new NotificationCompat.Builder(this)
                .setPriority(NotificationCompat.PRIORITY_MAX) // urgent: we need user's attention asap.
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setSmallIcon(R.drawable.drawer_logo_qapp)
                .addAction(R.drawable.drawer_logo_qapp,
                        getText(R.string.action_launch_activity),
                        PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE,
                                questionnaireActivityIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
        finish();
    }
}
