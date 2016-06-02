package com.example.josetalito.questapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.common.model.Solutions;

/**
 * Created by Josetalito on 23/05/2016.
 */
public class RestClient extends AsyncTask<Solutions, Void, Void> {

    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "RESTAPIClient";
    /**
     * API address
     */
    private static final String apiAddress = "http://localhost:8080/api/v1/dossiers/1/responses/";

    @Override
    protected Void doInBackground(Solutions... params) {
        Log.i(TAG, "doInBackground called.");
        return null;
    }

    protected void onPostExecute() {
        // TODO: Send feedback to user (ie. a message).
    }
}
