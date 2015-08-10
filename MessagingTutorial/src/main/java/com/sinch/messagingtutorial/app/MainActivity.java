package com.sinch.messagingtutorial.app;
import java.util.Locale;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.io.FileNotFoundException;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    protected Uri mMediaUri;        //URI stands for uniform resource identfier, the path to specific system

    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10;        //CONSTANT CONVERSION FOR FILE SIZE 10 MB

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch(which)
            {
                case 0:     //take picture
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);       //OPENS DEFAULT CAMERA APP TO TAKE PICS
                    mMediaUri = getOutPutMediaFileUri(MEDIA_TYPE_IMAGE);
                    if(mMediaUri == null)
                    {
                        //display an error message
                        Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_LONG);
                    }
                    else {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }
                    break;
                case 1:     //take video
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutPutMediaFileUri(MEDIA_TYPE_VIDEO);
                    if(mMediaUri == null)
                    {
                        //display an error message
                        Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_LONG);
                    }
                    else
                    {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);       //parse only allows 10mb size limit for free version
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);      //10 is 10 second video limit
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);        //0 means lowest video quality
                        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                    }
                    break;
                case 2:     //choose picture
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                    break;
                case 3:     //choose video
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, R.string.video_file_size_warning, Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                    break;
            }
        }

        private Uri getOutPutMediaFileUri(int mediaType)
        {
            if(isExternalStorageAvailable())
            {
                //get the URI
                // 1. get external storage directory
                String appName = MainActivity.this.getString(R.string.app_name);
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
                // 2. create our subdirectory
                if(! mediaStorageDir.exists())
                {
                    if(! mediaStorageDir.mkdirs())
                    {
                        Log.e(TAG, "Failed to create directory.");
                        return null;
                    }
                }
                // 3. create a file name
                // 4. create the file
                File mediaFile;
                Date now = new Date();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
                String path = mediaStorageDir.getPath() + File.separator;
                if(mediaType == MEDIA_TYPE_IMAGE)
                {
                    mediaFile = new File(path + "IMG" + timeStamp + ".jpg");
                }
                else if(mediaType == MEDIA_TYPE_VIDEO)
                {
                    mediaFile = new File(path + "VID_" + timeStamp + ".mp4");
                }
                else
                {
                    return null;
                }
                Log.d(TAG, "File: " +Uri.fromFile(mediaFile));
                // 5. return the file's URI
                return Uri.fromFile(mediaFile);
            }
            else
            {
                return null;
            }
        }

        private boolean isExternalStorageAvailable()
        {
            String state = Environment.getExternalStorageState();
            if(state.equals(Environment.MEDIA_MOUNTED))             //checks if media storage is avabile
            {
                return true;            //if true, return true
            }
            else
            {
                return false;           //else returns false
            }
        }
    };

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseUser currentUser = ParseUser.getCurrentUser();

        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class); //message service is messaging service by sinch
        startService(serviceIntent);    //starting service intent

        if(currentUser == null)
        {
            navigateToLogin();
        }
        else
        {
            Log.i(TAG, currentUser.getUsername());
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            if(requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST)      //if choose photo or vidoe is picked, checks if data returns null
            {
                if(data == null)        //if null, than data has failed so error toast outputs
                {
                    Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }
                else        //else, mMediaUri gets what get data outputs
                {
                    mMediaUri = data.getData();
                }

                Log.i(TAG, "Media URI: "+ mMediaUri);
                if(requestCode == PICK_VIDEO_REQUEST)       //checks if it is a video request, than if is less than allowed size
                {
                    //make sure file size is less than 10mb
                    int filesize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        filesize = inputStream.available();
                    }
                    catch(FileNotFoundException e)      //catches file not found exceptiosn
                    {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch (IOException e)           //catches IO exceptions
                    {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally             //finally block gets executed no matter what
                    {
                        try
                        {
                            inputStream.close();        //closing input stream
                        }
                        catch(IOException e)
                        {
                            //leaving this code block intentionally blank
                        }
                    }
                    if(filesize >= FILE_SIZE_LIMIT)     //makes sure the file size limit is less than 10MB
                    {
                        Toast.makeText(this, R.string.error_file_size_too_large, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            else
            {
                //add it to the gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//initalizes intent to scan for media
                mediaScanIntent.setData(mMediaUri); //sets the media choosen like photo or video
                sendBroadcast(mediaScanIntent);
            }

//            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
//            recipientsIntent.setData(mMediaUri);

            String fileType;
            if(requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST)//needs to determine before sending to parse what kind of file is it
            {
                fileType = ParseConstants.TYPE_IMAGE;       //equals image file type
            }
            else
            {
                fileType = ParseConstants.TYPE_VIDEO;       //equals video file type
            }
//            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
//            startActivity(recipientsIntent);
        }
        else if(resultCode != RESULT_CANCELED)
        {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToLogin()//helper method to navigate to the login screen
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_logout)
        {
            ParseUser.logOut();
            navigateToLogin();
        }
        else if(id == R.id.media_actions)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(R.array.camera_choices, mDialogListener);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
//        else if (id == R.id.action_edit_friends)
//        {
//            Intent intent = new Intent(this, EditFriendsActivity.class);
//            startActivity(intent);
//        }

        return super.onOptionsItemSelected(item);
    }
}
