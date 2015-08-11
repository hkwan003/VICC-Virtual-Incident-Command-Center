package com.sinch.messagingtutorial.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;


public class FriendsFragment extends android.support.v4.app.ListFragment
{
    //default variables to retrieve list of friends
    public static final String TAG = FriendsFragment.class.getSimpleName();
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected String[] usernames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        return rootView;
    }

    @Override
    public void onResume()  //this method is in charge of retrieving all friends from the backend
    {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mFriends = friends;
                    usernames = new String[mFriends.size()];            //makes an array of size users
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;                                                //fills out the array with usernames
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, usernames);
                    setListAdapter(adapter);
                } else if (e.getMessage().equals("java.lang.ClassCastException: java.lang.String cannot be cast to org.json.JSONObject"))
                {
                    //Do nothing since friends are not yet added.
                    //ignore
                }
                else
                {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());        //on fail, outputs an error message
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        openConversation(usernames, position);
        //Toast.makeText(getActivity(), "Error finding that user", Toast.LENGTH_LONG).show();
    }

    public void openConversation(final String[] usernames, final int pos)
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", usernames[pos]);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, ParseException e)
            {
                if (e == null)
                {
                    //start the messaging activity
                    Toast.makeText(getActivity(), usernames[pos], Toast.LENGTH_SHORT).show();
                } else
                {
                    Toast.makeText(getActivity(), "Error finding that user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}