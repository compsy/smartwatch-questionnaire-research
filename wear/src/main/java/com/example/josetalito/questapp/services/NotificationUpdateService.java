package com.example.josetalito.questapp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.common.model.Questionnaire;
import com.example.josetalito.questapp.activities.QuestionnaireActivity;
import com.example.josetalito.questapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.apache.commons.lang3.SerializationUtils;

import static com.google.android.gms.wearable.PutDataRequest.WEAR_URI_SCHEME;

/**
 * Created by Josetalito on 24/04/2016.
 * A Service that runs on the background and listens to questionnaire pushes from the handheld.
 * It communicates with the handheld by means of Google API and a data layer.
 * Because it runs on the background, we use WearableListenerService instead of DataListener, which
 * runs only on the foreground.
 */
public class NotificationUpdateService extends WearableListenerService {

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "WearableListenerService";

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

    private int NOTIFICATION_ID = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();
            if ("DISMISS".equals(action)) {
                dismissNotification();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i(TAG, "onDataChanged called.");
        for(DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                if ("/path/to/data".equals(dataEvent.getDataItem().getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    String title = dataMapItem.getDataMap().getString("NOTIFICATION_TITLE");
                    String content = dataMapItem.getDataMap().getString("NOTIFICATION_CONTENT");
                    byte[] testBytes = dataMapItem.getDataMap().getByteArray("questionnaire");
                    Questionnaire questionnaire = SerializationUtils.deserialize(testBytes);
                    sendNotification(title, content, questionnaire);
                }
            }
        }
    }

    private void sendNotification(String title, String content, Questionnaire questionnaire) {
        Log.i(TAG, "NotificationService - showNotification called.");

        NOTIFICATION_ID = Integer.getInteger(questionnaire.getQuestionnaireKey(), -1);
        ++NOTIFICATION_ID;

        Intent questionnaireActivity = new Intent(this, QuestionnaireActivity.class);
        questionnaireActivity.putExtra("questionnaire", questionnaire);
        questionnaireActivity.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
        PendingIntent pendingQuestionnaireActivity = PendingIntent.getActivity(this, 0, questionnaireActivity, 0);

        Intent dismissIntent = new Intent("DISMISS");
        PendingIntent pendingDeleteIntent = PendingIntent.getService(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.drawer_logo_qapp)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setDeleteIntent(pendingDeleteIntent)
                .addAction(R.drawable.drawer_logo_qapp,
                        getText(R.string.action_launch_activity),
                        pendingQuestionnaireActivity)
                .setContentIntent(pendingQuestionnaireActivity);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    private void dismissNotification() {
        new DismissNotificationCommand(this).execute();
    }


    private class DismissNotificationCommand implements
            GoogleApiClient.ConnectionCallbacks, ResultCallback<DataApi.DeleteDataItemsResult>,
            GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = "DismissNotification";

        private final GoogleApiClient mGoogleApiClient;

        public DismissNotificationCommand(Context context) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        public void execute() {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnected(Bundle bundle) {
            final Uri dataItemUri =
                    new Uri.Builder().scheme(WEAR_URI_SCHEME).path("/path/to/data").build();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Deleting Uri: " + dataItemUri.toString());
            }
            Wearable.DataApi.deleteDataItems(
                    mGoogleApiClient, dataItemUri).setResultCallback(this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended");
        }

        @Override
        public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
            if (!deleteDataItemsResult.getStatus().isSuccess()) {
                Log.e(TAG, "dismissWearableNotification(): failed to delete DataItem");
            }
            mGoogleApiClient.disconnect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed");
        }
    }
}
