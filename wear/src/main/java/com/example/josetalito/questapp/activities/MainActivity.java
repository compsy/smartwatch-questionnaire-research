package com.example.josetalito.questapp.activities;

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

import com.example.common.model.FewAnswers;
import com.example.common.model.ManyAnswers;
import com.example.common.model.Questionnaire;
import com.example.common.model.QuestionnaireBuilder;
import com.example.common.model.SliderAnswer;
import com.example.josetalito.questapp.R;

import java.util.Random;

public class MainActivity extends Activity implements DelayedConfirmationView.DelayedConfirmationListener {

    private int NOTIFICATION_ID = -1;
    private int NOTIFICATION_REQUEST_CODE = 1;
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
        Log.i(TAG, "Main - showNotification called.");

        /* Questionnaire building - Test purposes */
        FewAnswers fanswers = new FewAnswers("yes", "no");
        ManyAnswers manswers = new ManyAnswers();
            manswers.getAnswers().add("Veel te druk");
            manswers.getAnswers().add("Lekker druk");
            manswers.getAnswers().add("Neutraal");
            manswers.getAnswers().add("Lekker rustig");
            manswers.getAnswers().add("Veel te rustig");
        SliderAnswer sanswer = new SliderAnswer("0", "100");

        QuestionnaireBuilder qBuilder = new QuestionnaireBuilder();
        Questionnaire questionnaire = qBuilder.setUpQuestionnaire(3) // Three questions
                .addQuestion("Heeft u sinds het vorige meetmoment geslapen?", true, fanswers)
                .addQuestion("Hoe druk heb ik het?", true, manswers)
                .addQuestion("Hoe gaat het op dit moment met u?", true, sanswer)
                .buildQuestionnaire();
        Log.i(TAG, "Questionnaire built.");

        /** This random number is for test purposes, giving a random ID to both questionnaires and
         * notifications; ie. notifications can be later be dismissed. The logic of the app is meant
         * to be:
         * 1. Notification is received in the watch.
         * 2. Action is taken from it in order to fill in the questionnaire.
         * 3. Once the questionnaire is filled in, the notification disappears.
         * 4. The user is driven Home.
         * The final version of this ID will be the random KEY given by RoQua.
         * Now the random numbers are generated in the interval [0-1337].
         */
        int random = 0 + (int)(Math.random() * 1337);
        questionnaire.setQuestionnaireKey(String.valueOf(random));
        NOTIFICATION_ID = Integer.getInteger(questionnaire.getQuestionnaireKey(), -1);
        Log.i(TAG, "Random: " + random + ", NOTIFICATION_ID: " + NOTIFICATION_ID);

        Intent questionnaireActivityIntent = new Intent(this, QuestionnaireActivity.class);
        questionnaireActivityIntent.putExtra("questionnaire", questionnaire);
        questionnaireActivityIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);

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
