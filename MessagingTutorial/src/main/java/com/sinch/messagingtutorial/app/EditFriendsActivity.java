package com.sinch.messagingtutorial.app;


        import android.app.AlertDialog;
        import android.app.ListActivity;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.HeaderViewListAdapter;
        import android.widget.ListAdapter;
        import android.widget.ListView;

        import com.parse.FindCallback;
        import com.parse.ParseException;
        import com.parse.ParseQuery;
        import com.parse.ParseRelation;
        import com.parse.ParseUser;
        import com.parse.SaveCallback;

        import java.util.List;

public class EditFriendsActivity extends ListActivity
{
    public static final String TAG = EditFriendsActivity.class.getSimpleName();


    public ListView mListView;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //ActionBar supportActionBar = getSupportActionBar();         //creates an action bar object
        //supportActionBar.show();                                    //displays the action bar so overflow menu can be displayed
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);             //enables check mark to be checked in edit friends
    }

    @Override
    protected void onResume()           //when resuming activity, refreshes activity
    {
        super.onResume();               //creating parse query to get list of users of friends

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);     //Key_user is parameter shows what the query will retrieve stored in class ParseConstants
        query.setLimit(1000);                       //sets the limit of the number of search results to maximum of 1000
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null)                   //success on find
                {
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];            //makes an array of size users
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;                                                //fills out the array with usernames
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);
                    addFriendsCheckmarks();
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);        //on fail, outputs an error message
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


    }

    public ListView getListView()                        //helper functions not available in actionbaractivity
    {
        if (mListView == null) {
            mListView = (ListView) findViewById(android.R.id.list);
        }
        return mListView;
    }

    public void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    public ListAdapter getListAdapter()
    {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        if (getListView().isItemChecked(position))              //if item is actually clicked on to add friends
        {
            mFriendsRelation.add(mUsers.get(position));         //adds friend locallay
        }
        else            //to remove friends
        {
            mFriendsRelation.remove(mUsers.get(position));      //removes friend locally
        }
        mCurrentUser.saveInBackground(new SaveCallback()            //saves them to the background
        {
            @Override
            public void done(ParseException e) {           //to add friends
                if (e != null) {                                   //saves changes on the backend
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private void addFriendsCheckmarks()
    {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>()
        {
            @Override
            public void done(List<ParseUser> friends, ParseException e)
            {
                if(e == null)       //query succeeded, list returned -  look for a match
                {
                    for(int i = 0; i < mUsers.size(); i++)
                    {
                        ParseUser user = mUsers.get(i);

                        for(ParseUser friend : friends)
                        {
                            if(friend.getObjectId().equals(user.getObjectId()))
                            {
                                getListView().setItemChecked(i, true);
                            }
                        }
                    }
                }
                else
                {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}
