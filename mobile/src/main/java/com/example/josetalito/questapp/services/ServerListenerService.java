package com.example.josetalito.questapp.services;

import android.util.Log;

import com.example.common.model.Solutions;
import com.example.josetalito.questapp.RestClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.apache.commons.lang3.SerializationUtils;


/**
 * Created by Josetalito on 24/04/2016.
 * A Service that runs on the background and listens to questionnaire pushes from the server.
 * It communicates with the server by means of a REST API.
 * Because it runs on the background, we use WearableListenerService instead of DataListener, which
 * runs only on the foreground.
 */
public class ServerListenerService extends WearableListenerService {

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "HandheldListenerService";

    /*************************************************
     ************ Google Api Client related variables.
     *************************************************/
    /**
     * Path for DataItems for phone
     */
    private static final String DATA_TO_PHONE = "path/to/phone";
    /**
     * Google Api Client. Interface to use the Wearable Api (from Google Play Services).
     */
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Phone listener service created.");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }


    /** The logic would be:
     * 1. Take the answers from the dataMapItem.
     * 2. Start a new activity and send these answers.
     * 3. The activity (or service) manages sending the data to the REST API.
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i(TAG, "onDataChanged called.");
        for(DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                // Receiving answers from the wearable and sending them to the API
                if (DATA_TO_PHONE.equals(dataEvent.getDataItem().getUri().getPath())) {
                    Log.i(TAG, "Answers received on the phone.");
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    String questionnaireKey = dataMapItem.getDataMap().getString("QUESTIONNAIRE_KEY");
                    byte[] solutionsBytes = dataMapItem.getDataMap().getByteArray("answers");
                    Solutions solutions = SerializationUtils.deserialize(solutionsBytes);
                    // Sending to the API
                    new RestClient().execute(solutions);
                }
            }
        }
    }

}
