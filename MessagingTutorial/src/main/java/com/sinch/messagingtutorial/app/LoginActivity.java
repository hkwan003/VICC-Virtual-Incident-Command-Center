package com.sinch.messagingtutorial.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class LoginActivity extends Activity
{
    public static final String TAG = LoginActivity.class.getSimpleName();

    private Button signUpButton;
    private Button loginButton;
    private EditText usernameField;
    private EditText passwordField;
    private String username;
    private String password;
    private String result="";
    private Intent intent;
    private Intent serviceIntent;

    String URL = "http://alpha.deep-horizons.net/icc/IncidentControl/Login.php?";
    String ping_username;
    String ping_password;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //http request networking

        //login portion
        intent = new Intent(getApplicationContext(), MainActivity.class);
        serviceIntent = new Intent(getApplicationContext(), MessageService.class);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            startService(serviceIntent);
            startActivity(intent);
        }


        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.signupButton);
        usernameField = (EditText) findViewById(R.id.loginUsername);
        passwordField = (EditText) findViewById(R.id.loginPassword);

        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(myIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                username = usernameField.getText().toString();
                password = passwordField.getText().toString();

                ParseUser.logInInBackground(username, password, new LogInCallback()
                {
                    public void done(ParseUser user, ParseException e)
                    {
                        if (user != null)
                        {
                            startService(serviceIntent);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Calvin's parse DB",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

//                String requestURL = URL+"username="+username+"&password="+password+"&id=OpenHouse";
//
//
//
//                //network http request
//
//                if(NetworkAvail())
//                {
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder().url(requestURL).build();
//                    Call call = client.newCall(request);
//                    call.enqueue(new Callback()
//                    {
//                        @Override
//                        public void onFailure(Request request, IOException e)
//                        {
//                            result = "Request Failed";
//                        }
//                        @Override
//                        public void onResponse(Response response) throws IOException
//                        {
//                            try
//                            {
//                                //result contains the output of server-side script
//                                // "Agent","Commander","Denied"
//                                result = response.body().string();
//                                Log.v(TAG, result);
//                                //if(!response.isSuccessful())
//                                //{
//                                    //stop crashing
//                                //}
//                            }
//                            catch (IOException e)
//                            {
//                                Log.e(TAG, "exception caught", e);
//                            }
//                        }
//                    });
//                }
//                else{
//                    //need internet connection toast
//                    result="Check Network Connection";
//                }
//                if(result.equals("Agent") || result.equals("Commander"))
//                {
//                    ParseUser.logInInBackground(username, password, new LogInCallback() {
//                        public void done(ParseUser user, ParseException e) {
//                            if (user != null) {
//                                startService(serviceIntent);
//                                startActivity(intent);
//                            } else {
//                                Toast.makeText(getApplicationContext(),
//                                        "Calvin's parse DB",
//                                        Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                }
//                else
//                {
//                    Toast.makeText(getApplicationContext(),
//                            result,
//                            Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

    @Override
    public void onDestroy()
    {
        stopService(new Intent(this, MessageService.class));
        super.onDestroy();
    }


    public boolean NetworkAvail()
    {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected())
        {
            isAvailable = true;
        }
        return isAvailable;


    }
}
