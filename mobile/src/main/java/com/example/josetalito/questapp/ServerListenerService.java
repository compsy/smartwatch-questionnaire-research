package com.example.josetalito.questapp;

import android.widget.Toast;

import com.google.android.gms.wearable.WearableListenerService;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

import org.json.*;


/**
 * Created by Josetalito on 24/04/2016.
 * A Service that runs on the background and listens to questionnaire pushes from the server.
 * It communicates with the server by means of a REST API.
 */
public class ServerListenerService extends WearableListenerService {
    /**
     * Debugging tag for logging messages.
     */
    private static final String TAG = "PhoneServerListenerService";


}
