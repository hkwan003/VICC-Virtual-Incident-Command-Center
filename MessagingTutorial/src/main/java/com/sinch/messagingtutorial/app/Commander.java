package com.sinch.messagingtutorial.app;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.app.Service;

public class Commander extends Fragment
{
    private EditText mIncidentReport;       //variable for edittext for user to type in information
    private String mCurrentUser;            //string to hold string of current user

    Button btnShowLocation;     //initalize button for button to output toast the GPS coordinates
    Button sendIncident;        //initialize button for sending incident button
    GPSTracker gps;         //initalize GPS tracker class to retrieve GPS coordinates

    double latitude;        //holds latitude
    double longitude;       //holds longitude
    String ObjectID;        //holds objectID of incident report sent
    String incident_report;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_commander, container, false);
        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        btnShowLocation = (Button) getView().findViewById(R.id.GPS_Button);     //linking up button
        sendIncident = (Button) getView().findViewById(R.id.send_button);       //link up button with send
        mIncidentReport = (EditText) getView().findViewById(R.id.incident_message);
        btnShowLocation.setOnClickListener(new View.OnClickListener()       //onclick listener to show GPS location button

        {
            @Override
            public void onClick(View v) {
                gps = new GPSTracker(getActivity());        //new instance of GPS tracker in Commander fragment

                if (gps.canGetLocation)      //if GPS is turned on, then will be able to retrieve information
                {
                    latitude = gps.getLatitude();       //sets latitude
                    longitude = gps.getLongitude();     //sets longitude


                    Toast.makeText(getActivity().getApplicationContext(),       //make text toast of the coordinates and outputs it
                            "Your Location is -\nLat: " + latitude + "\nLong: " + longitude,
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    gps.showSettingsAlert();        //else, opens GPS settings for USER to turn on
                }
            }
        });

        sendIncident.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                incident_report = mIncidentReport.getText().toString();     //gets user input to push to Parse
                gps = new GPSTracker(getActivity());
                if (gps.canGetLocation)      //if GPS is turned on, then will be able to retrieve information
                {
                    latitude = gps.getLatitude();       //sets latitude
                    longitude = gps.getLongitude();     //sets longitude

                    ParseObject Score = new ParseObject("Score");
                    Score.put("Username", incident_report);
                    Score.put("Latitude", latitude);
                    Score.put("Longitude", longitude);
                    Score.saveInBackground();

                    Toast.makeText(getActivity().getApplicationContext(),       //make text toast of the coordinates and outputs it
                            "Message Sent",
                            Toast.LENGTH_LONG).show();
                    ((EditText) getActivity().findViewById(R.id.incident_message)).setText("");
                }
                else
                {
                    gps.showSettingsAlert();        //else, opens GPS settings for USER to turn on
                }


            }
        });





    }


}