package com.example.josetalito.questapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.common.R;
import com.example.common.model.FewAnswers;
import com.example.common.model.ManyAnswers;
import com.example.common.model.Questionnaire;
import com.example.common.model.QuestionnaireBuilder;
import com.example.common.model.SliderAnswer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;

/**
 * TODO: Refactoring of mGoogleApiClient.
 * Refactoring mGoogleApiClient: It should be in ServerListenerService; here only for testing purposes (e.g. synchronization
 * between wearable and the phone and data exchange.
 */
public class MainActivity extends AppCompatActivity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "PhoneMainActivity";

    /*************************************************
     ************ Google Api Client related variables.
     *************************************************/
    /**
     * Path for DataItems.
     */
    private static final String COUNT_KEY = "com.example.key.count";
    /**
     * Google Api Client. Interface to use the Wearable Api (from Google Play Services).
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Variable for test purposes. TODO: to be deleted.
     */
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.josetalito.questapp.R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        final Button bttn_notification = (Button) findViewById(com.example.josetalito.questapp.R.id.bttn_notification);
        bttn_notification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Notification button pressed.");
                sendNotification();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void sendNotification() {
        if (mGoogleApiClient.isConnected()) {
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
            Log.i(TAG, "Phone: notification sent.");

            /** END - ADDING THE QUESTIONNAIRE **/

            PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/path/to/data");
            // Make sure the data item is unique. Usually, this will not be required, as the payload
            // (in this case the title and the content of the notification) will be different for almost all
            // situations. However, in this example, the text and the content are always the same, so we need
            // to disambiguate the data item by adding a field that contains teh current time in milliseconds.
            dataMapRequest.getDataMap().putDouble("NOTIFICATION_TIMESTAMP", System.currentTimeMillis());
            dataMapRequest.getDataMap().putString("NOTIFICATION_TITLE", getString(com.example.common.R.string.notification_title));
            dataMapRequest.getDataMap().putString("NOTIFICATION_CONTENT", getString(com.example.common.R.string.notification_content));

            // Serializing the questionnaire
            byte[] testBytes = SerializationUtils.serialize(questionnaire);
            dataMapRequest.getDataMap().putByteArray("questionnaire", testBytes);

            dataMapRequest.setUrgent(); // We want it to be delivered straight away
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
        }
        else {
            Log.e(TAG, "No connection to wearable available!");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        /*for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/count") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    //updateCount(dataMap.getInt(COUNT_KEY));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }*/
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
