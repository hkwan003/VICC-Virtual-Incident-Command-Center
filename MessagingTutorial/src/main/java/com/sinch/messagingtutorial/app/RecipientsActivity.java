package com.sinch.messagingtutorial.app;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.messagingtutorial.app.FileHelper;

import java.util.ArrayList;
import java.util.List;

public class RecipientsActivity extends ListActivity
{

    public static final String TAG = RecipientsActivity.class.getSimpleName();
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected MenuItem mSendMenuItem;       //to use to reference the send button

    protected Uri mMediaUri;        //in charge of files too big and cumbersome like pics and videos
    protected String mFileType;
    protected Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);             //enables check mark to be checked in edit friends

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);//get specific extra based on key located in parse constants
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        //getting query from friends relation
        ParseQuery<ParseUser> query =  mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);       //parse username in ascending order
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];            //makes an array of size users
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;                                                //fills out the array with usernames
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);
                } else if (e.getMessage().equals("java.lang.ClassCastException: java.lang.String cannot be cast to org.json.JSONObject")) {
                    //Do nothing since friends are not yet added.
                    //ignore
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);        //on fail, outputs an error message
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });

        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ParseObject message = createMessage();
                if(message == null)
                {
                    //error message
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_selecting_file)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    //toast live across activities changes
                    //dialogs only live in current activity
                    send(message);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);       //0 is position zero, given only one button in menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(id == R.id.action_send)
        {
            //to be handled
            ParseObject message = createMessage();
            if(message == null)
            {
                //error message
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.error_selecting_file)
                        .setTitle(R.string.error_selecting_file_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else
            {
                //toast live across activities changes
                //dialogs only live in current activity
                send(message);
                finish();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    protected ParseObject createMessage()
    {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);       //creating a new object to have a class of specified information to push to parse server
        message.put(ParseConstants.KEY_SENDER_IDS, ParseUser.getCurrentUser().getObjectId());//gets object ID
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());//gets current username
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientsIds());  //array of recipient IDS
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);   //now has the file type

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);//only way parse allows for uploads is to put it in a byte file

        if(fileBytes == null)       //if anything happens that make it go wrong, than there was a problem with upload
        {
            return null;
        }
        else
        {
            if(mFileType.equals(ParseConstants.TYPE_IMAGE)) //on success, reduces the image down to size that parse can handle
            {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);   //retrieves the file name and renames file
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);

        }
        return message;
    }

    protected ArrayList<String> getRecipientsIds()      //method returns array list
    {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for(int i = 0; i < getListView().getCount(); i++)       //loops through list of checked users to grab user id
        {
            if(getListView().isItemChecked(i))      //if user is checked, add userid to array list
            {
                recipientIds.add(mFriends.get(i).getObjectId());
            }

        }
        return recipientIds;        //returns array list
    }

    protected void send(ParseObject message)        //send means save in backend
    {
        message.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if(e == null)   //null means save in background success
                {
                    //success
                    Toast.makeText(RecipientsActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
                }
                else //error
                {
                    //outputing an error dialog which the user won't be able to miss until pressing ok
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(R.string.error_sending_message)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}