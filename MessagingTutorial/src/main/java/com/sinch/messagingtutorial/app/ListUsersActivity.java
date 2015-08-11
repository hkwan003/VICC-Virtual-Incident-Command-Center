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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.app.Service;

public class ListUsersActivity extends Fragment
{
    private String currentUserId;
    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private ListView usersListView;
    //private Button logoutButton;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_list_users, container, false);
        return rootView;
    }

    @Override
    public void onResume()
    {
        setConversationsList();
        super.onResume();


    }

    //display clickable a list of all users
    private void setConversationsList()
    {
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        names = new ArrayList<String>();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("objectId", currentUserId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, com.parse.ParseException e)
            {
                if (e == null)
                {
                    for (int i=0; i<userList.size(); i++)
                    {
                        names.add(userList.get(i).getUsername().toString());
                    }

                    usersListView = (ListView)getView().findViewById(R.id.usersListView);
                    namesArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.user_list_item, names);
                    usersListView.setAdapter(namesArrayAdapter);

                    usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                            openConversation(names, i);
                        }
                    });

                } else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Error loading user list", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //open a conversation with one person
    public void openConversation(ArrayList<String> names, int pos)
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", names.get(pos));
        query.findInBackground(new FindCallback<ParseUser>()
        {
            public void done(List<ParseUser> user, com.parse.ParseException e)
            {
                if (e == null)
                {
                    Intent intent = new Intent(getActivity().getApplicationContext(), MessagingActivity.class);
                    intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    startActivity(intent);
                } else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Error finding that user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
