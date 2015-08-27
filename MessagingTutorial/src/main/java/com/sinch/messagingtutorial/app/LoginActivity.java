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

    private int increment = 0;

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
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null)
        {
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

                String requestURL = URL+"username="+username+"&password="+password+"&id=OpenHouse";


                ParseUser.logInInBackground(username, password, new LogInCallback()
                {
                    public void done(ParseUser user, ParseException e)
                    {
                        if (user != null)
                        {
                            MyApplication.updateParseInstallation(user);
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

            }
        });
    }
    @Override
    public void onDestroy()
    {
        stopService(new Intent(this, MessageService.class));
        super.onDestroy();
    }
}