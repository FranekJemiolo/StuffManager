package com.franekjemiolo.stuffmanageralpha;

import android.app.AlertDialog;
import android.app.Dialog;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddContainerActivity extends AppCompatActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {


    public static final String PREFERENCES_NAME = "com.franekjemiolo.stuffmanageralpha.MyPreferencesFile";
    public final static String CONTAINER_PARENT_ID = "com.franekjemiolo.stuffmanageralpha.CONTAINER_PARENT_ID";
    public static final String CONTAINER_OR_ITEM_MESSAGE = "com.franekjemiolo.stuffmanageralpha.IS_CONTAINER";
    public static final String IS_EDITED = "com.franekjemiolo.stuffmanageralpha.IS_EDITED";
    public static final String ID_EDITED = "com.franekjemiolo.stuffmanageralpha.ID_EDITED";

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int NO_LOCATION_GIVEN = 1000;

    static final int NO_PARENT = -1;

    static final int CONTAINER = 1;
    static final int ITEM = 0;

    static final String NO_GPS = "GPS is not working";

    // Edit text for container name
    private EditText nameEdit;

    // ImageView of the thumbnail - where we show the image
    private ImageView mImageView;
    // Dimensions of imageview;
    private int imageWidth;
    private int imageHeight;

    int finalHeight;
    int finalWidth;

    // The path to the photo
    private static String mCurrentPhotoPath  = null;

    // Container Name.
    private String mContainerName = "Default";

    // The date taken in format yyyyMMdd_HHmmss.
    private String mDateTime;

    // The gps locations.
    // If they are set to NO_LOCATION_GIVEN = 1000 then user has not entered location.
    private double mLatitude = NO_LOCATION_GIVEN;
    private double mLongitude = NO_LOCATION_GIVEN;

    // Parent id (we will read it in the intent)
    private long mParentId = NO_PARENT;

    // Is it a container.
    private long mIsContainer = CONTAINER;

    // The number to represent quantity.
    private long mNumber = 0;

    // The temporary file for image.
    private File temporaryImage;

    // This is to ensure we disable location client after connecting it.
    private boolean connected = false;

    // The database handler
    DatabaseHandler db;

    // Text view displaying container/item
    private TextView nameTextView;

    // Button to show save Container/ Item
    private Button saveContainerButton;

    // Button to take photo
    private Button takePhotoButton;


    // Text view and edit text responsible for item quantity.
    private TextView numberTextView;
    private EditText numberEditText;

    // We will read from the preferences and set according to it if we always take location or not.
    private boolean takeLocation = true;

    //private boolean loadPic = false;

    // locations objects
    LocationClient mLocationClient;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;


    // Text views for location
    TextView latitudeText;
    TextView longitudeText;

    // Is it new item/container or just edition?
    private boolean isEdited;

    // Edited container
    private long containerId;
    private Container edited_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tag", "onCreate");
        setContentView(R.layout.activity_add_container);

        Intent intent = getIntent();

        isEdited = false;
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.containsKey(IS_EDITED)) {
                    isEdited = true;
                }
                if (extras.containsKey(ID_EDITED)) {
                    containerId = extras.getLong(ID_EDITED);
                }
                else {
                    containerId = -1;
                }
                if (extras.containsKey(CONTAINER_OR_ITEM_MESSAGE)) {
                    mIsContainer = extras.getLong(CONTAINER_OR_ITEM_MESSAGE);
                }
                if (extras.containsKey(CONTAINER_PARENT_ID)) {
                    mParentId = extras.getLong(CONTAINER_PARENT_ID);
                }
            }
        }
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initiate database
        db = new DatabaseHandler(this);

        numberTextView = (TextView) findViewById(R.id.numberTextView);

        numberEditText = (EditText) findViewById(R.id.numberEditText);

        nameTextView = (TextView) findViewById(R.id.insertTextView);

        saveContainerButton = (Button) findViewById(R.id.saveButton);

        takePhotoButton = (Button) findViewById(R.id.takePhotoButton);

        if (isEdited) {
            if (mIsContainer == 1) {
                nameTextView.setText(R.string.edit_container_name);
                takePhotoButton.setText(R.string.take_photo_container);
                saveContainerButton.setText(R.string.save_button);
                numberTextView.setVisibility(View.GONE);
                numberEditText.setVisibility(View.GONE);
                setTitle("Edit container");
            } else if (mIsContainer == 0) {
                nameTextView.setText(R.string.edit_item_name);
                takePhotoButton.setText(R.string.take_photo_item);
                saveContainerButton.setText(R.string.save_item_button);
                setTitle("Edit item");
            }
        }
        else {
            if (mIsContainer == 1) {
                nameTextView.setText(R.string.insert_container_name);
                takePhotoButton.setText(R.string.take_photo_container);
                saveContainerButton.setText(R.string.save_button);
                numberTextView.setVisibility(View.GONE);
                numberEditText.setVisibility(View.GONE);
                numberEditText = null;
                numberTextView = null;
                setTitle("New container");
            } else if (mIsContainer == 0) {
                nameTextView.setText(R.string.insert_item_name);
                takePhotoButton.setText(R.string.take_photo_item);
                saveContainerButton.setText(R.string.save_item_button);
                setTitle("New item");
            }
        }
        // Initiate edit text
        nameEdit = (EditText) findViewById(R.id.editText);
        //nameEdit.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        // Initiates image view
        mImageView = (ImageView) findViewById(R.id.takePhotoImageView);

        if (isEdited) {
            if (containerId != -1) {
                edited_container = db.getContainer(containerId);
                if (edited_container != null) {
                    copyFile(edited_container.getImagePath(), "probny.jpg", getExternalFilesDir(null).toString());
                    mCurrentPhotoPath = getExternalFilesDir(null).toString() + "/probny.jpg";
                    nameEdit.setText(edited_container.getName());
                    mDateTime = edited_container.getDateTime();
                    mNumber = edited_container.getNumber();
                    if (numberEditText != null) {
                        numberEditText.setText(String.valueOf(mNumber));
                    }
                }
            }
        }

        /*if (loadPic) {
            setPic();
            loadPic = false;
        }*/
        latitudeText = (TextView) findViewById(R.id.latitudeText);
        longitudeText = (TextView) findViewById(R.id.longitudeText);
        setPic();
        /*this.loadPreferences();

        if (takeLocation) {
            if(checkEnableGPS()) {
                // 3. create LocationClient
                mLocationClient = new LocationClient(this, this, this);

                // 4. create & set LocationRequest for Location update
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                // Set the update interval to 5 seconds
                mLocationRequest.setInterval(1000 * 5);
                // Set the fastest update interval to 1 second
                mLocationRequest.setFastestInterval(1000 * 1);
            }
            else {
                Log.d("tag", "Gps after check not enabled");
                latitudeText.setText(NO_GPS);
                longitudeText.setText(NO_GPS);
            }
        }
        else {
            Log.d("tag", "Take location is off");
            latitudeText.setText(NO_GPS);
            longitudeText.setText(NO_GPS);
        }*/



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_container, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent (this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if (temporaryImage != null) {
                if(temporaryImage.exists()) {
                    temporaryImage.delete();
                }
            }
            temporaryImage = null;
            try {
                temporaryImage = createImageFile();
            } catch (IOException ex) {
                // do nothing.
                return;
            }
            if (temporaryImage != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temporaryImage));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("activityresult", "activity result..");
            setPic();
            //loadPic = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("resume", "resuming...");

    }

    private File createImageFile () throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "probny" + ".jpg";
        File storageDir = getExternalFilesDir(null);
        File image = new File(storageDir, imageFileName);
        Log.d("tag", String.valueOf(image.exists()));
        if (image.exists()) {
            boolean deleted = image.delete();
            if (!deleted) {
                Log.d("Deletion", "Error in deletion");
                throw new IOException();
            }
            Log.d("tag", String.valueOf(image.exists()));
            //image.createNewFile();
            //Log.d("tag", String.valueOf(image.exists()));
        }


        // Save a file; path for use with ACTION_VIEW intents
        if (mCurrentPhotoPath != null) {
            File removed = new File(mCurrentPhotoPath);
            if (removed.exists()) {
                removed.delete();
            }
            removed = null;
            mCurrentPhotoPath = null;
        }
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        imageWidth=mImageView.getWidth();
        imageHeight=mImageView.getHeight();
    }

    private void setPic() {
        // Get the dimensions of the View
        if (mCurrentPhotoPath == null) {
            Log.d("tag", "setPicFailed... due to path null");
            return;
        }

        ViewTreeObserver vto = mImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                finalHeight = mImageView.getMeasuredHeight();
                finalWidth = mImageView.getMeasuredWidth();
                int targetW = finalWidth;
                int targetH = finalHeight;

                if (targetH == 0 || targetW == 0) {
                    Log.d("tag", "nie dziala cos z wielkoscia");
                }

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                Log.d("tag", "Loading from " + mCurrentPhotoPath);
                BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

                mImageView.setImageBitmap(bitmap);
                return true;
            }
        });
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void saveContainer(View view) {



        // First we will have to redirect the image path and rename it.

        // Getting name
        mContainerName = nameEdit.getText().toString();
        if (mContainerName.isEmpty()) {
            mContainerName = "Default name";
        }

        // Getting dateTime
        mDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Getting number if it is item
        if (mIsContainer == 0) {
            if (numberEditText.getText().toString().isEmpty()) {
                mNumber = 1;
            }
            else {
                mNumber = Long.valueOf(numberEditText.getText().toString());
            }
        }

        // Redirecting image...
        String imageFileName = mContainerName + mDateTime + ".jpg";
        File internalDir = getFilesDir();

        if(mCurrentPhotoPath == null) {
            imageFileName = "no_image";
            Log.d("tag", "Saving...");
            Log.d("tag", "Name " + mContainerName + ", ParentId " + mParentId + ", ImagePath "
                    + imageFileName + ", Longitude " + mLongitude + ", Latitude " + mLatitude
                    + ", DateTime " + mDateTime + ", Number " + mNumber + ", IsContainer "
                    + mIsContainer);

            // Creating new container.
            Container container = new Container (
                    mContainerName,
                    mParentId,
                    imageFileName,
                    mLongitude,
                    mLatitude,
                    mDateTime,
                    mNumber,
                    mIsContainer
            );

            // Adding container to database.
            if (isEdited) {
                File del = new File(edited_container.getImagePath());
                if (del.exists()) {
                    del.delete();
                }
                container.setId(edited_container.getId());
                db.updateContainer(container);
            }
            else {
                Container addedContainer = db.addContainer(container);
                Container parentContainer = db.getContainer(mParentId);
                if (parentContainer != null) {
                    parentContainer.setNumber(parentContainer.getNumber() + 1l);
                    long changedContainer = db.updateContainer(parentContainer);
                }
            }
            finish();
        }
        else {
            copyFile(mCurrentPhotoPath, imageFileName, internalDir.getPath());

            if (temporaryImage != null) {
                if (temporaryImage.exists()) {
                    temporaryImage.delete();
                }
            }
            if (mCurrentPhotoPath != null) {
                File removed = new File(mCurrentPhotoPath);
                if (removed.exists()) {
                    removed.delete();
                }
                removed = null;
                mCurrentPhotoPath = null;
            }
            mCurrentPhotoPath = internalDir.getAbsolutePath() + "/" + imageFileName;
            // Checking if it is the right picture by setting it again.
            //setPic();

            Log.d("tag", "Saving...");
            Log.d("tag", "Name " + mContainerName + ", ParentId " + mParentId + ", ImagePath "
                    + mCurrentPhotoPath + ", Longitude " + mLongitude + ", Latitude " + mLatitude
                    + ", DateTime " + mDateTime + ", Number " + mNumber + ", IsContainer "
                    + mIsContainer);

            // Creating new container.
            Container container = new Container (
                    mContainerName,
                    mParentId,
                    mCurrentPhotoPath,
                    mLongitude,
                    mLatitude,
                    mDateTime,
                    mNumber,
                    mIsContainer
            );

            mCurrentPhotoPath = null;

            // Adding container to database.
            if (isEdited) {
                File del = new File(edited_container.getImagePath());
                if (del.exists()) {
                    del.delete();
                }
                container.setId(edited_container.getId());
                db.updateContainer(container);
            }
            else {
                Container addedContainer = db.addContainer(container);
                Container parentContainer = db.getContainer(mParentId);
                if (parentContainer != null) {
                    parentContainer.setNumber(parentContainer.getNumber() + 1l);
                    long changedContainer = db.updateContainer(parentContainer);
                }
            }
            finish();
        }

    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {
        InputStream in = null;
        OutputStream out = null;
        if (inputPath.equals(outputPath + "/" + inputFile)) {
            return;
        }
        try {

            // Create output directory if it doesnt exist
            File dir = new File (outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + "/" + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.loadPreferences();

        if (takeLocation) {
            if(checkEnableGPS()) {
                // 3. create LocationClient
                mLocationClient = new LocationClient(this, this, this);

                // 4. create & set LocationRequest for Location update
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                // Set the update interval to 5 seconds
                mLocationRequest.setInterval(1000 * 5);
                // Set the fastest update interval to 1 second
                mLocationRequest.setFastestInterval(1000 * 1);

                mLocationClient.connect();
                connected = true;
            }
            else {
                if (isEdited) {
                    if (edited_container != null) {
                        latitudeText.setText(String.valueOf(edited_container.getLatitude()));
                        longitudeText.setText(String.valueOf(edited_container.getLongitude()));
                        return;
                    }
                }
                Log.d("tag", "Gps after check not enabled");
                latitudeText.setText(NO_GPS);
                longitudeText.setText(NO_GPS);
            }
        }
        else {
            if (isEdited) {
                if (edited_container != null) {
                    latitudeText.setText(String.valueOf(edited_container.getLatitude()));
                    longitudeText.setText(String.valueOf(edited_container.getLongitude()));
                    return;
                }
            }
            Log.d("tag", "Take location is off");
            latitudeText.setText(NO_GPS);
            longitudeText.setText(NO_GPS);
        }

        /*// 1. connect the client.
        if (takeLocation) {
            mLocationClient.connect();
            connected = true;
        }
        else {
            Log.d("tag", "Take location is off");
        }*/
    }


    @Override
    protected void onStop() {
        super.onStop();
        // 1. disconnecting the client invalidates it.
        if (connected) {
            if (mLocationClient != null) {
                mLocationClient.disconnect();
            }
            connected = false;
        }
    }

    // GooglePlayServicesClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    // GooglePlayServicesClient.ConnectionCallbacks
    @Override
    public void onConnected(Bundle arg0) {

        if(mLocationClient != null)
            mLocationClient.requestLocationUpdates(mLocationRequest,  this);

        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        if(mLocationClient != null){
            // get location
            mCurrentLocation = mLocationClient.getLastLocation();
            try{
                mLatitude = mCurrentLocation.getLatitude();
                mLongitude = mCurrentLocation.getLongitude();
                latitudeText.setText(String.valueOf(mLatitude));
                longitudeText.setText(String.valueOf(mLongitude));


            }catch(NullPointerException npe){

                //Toast.makeText(this, "Failed to Connect", Toast.LENGTH_SHORT).show();
                longitudeText.setText(NO_GPS);
                latitudeText.setText(NO_GPS);

                // switch on location service intent
                //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //startActivity(intent);
            }
        }

    }
    @Override
    public void onDisconnected() {
        //Toast.makeText(this, "Disconnected.", Toast.LENGTH_SHORT).show();

    }

    // LocationListener
    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this, "Location changed.", Toast.LENGTH_SHORT).show();
        mCurrentLocation = mLocationClient.getLastLocation();
        try {
            mLatitude = mCurrentLocation.getLatitude();
            mLongitude = mCurrentLocation.getLongitude();
            latitudeText.setText(String.valueOf(mLatitude));
            longitudeText.setText(String.valueOf(mLongitude));
        } catch(NullPointerException npe) {
            // do nothing.
            longitudeText.setText(NO_GPS);
            latitudeText.setText(NO_GPS);
        }
    }


    // We are checking if user has GPS.
    private boolean checkEnableGPS(){
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider == null) {
            FragmentManager fragmentManager = getFragmentManager();
            EnableGPSDialogFragment newFragment = new EnableGPSDialogFragment();
            newFragment.setCancelable(false);
            newFragment.show(fragmentManager, "dialog");
            return false;
        }
        else if(provider.toLowerCase().contains("gps")){
            //GPS Enabled
            Log.d("tag", provider);
            return true;
        }else{
            FragmentManager fragmentManager = getFragmentManager();
            EnableGPSDialogFragment newFragment = new EnableGPSDialogFragment();
            newFragment.setCancelable(false);
            newFragment.show(fragmentManager, "dialog");
            return false;
        }

    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }

    public void loadPreferences() {
        SharedPreferences sharedPref = this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean defaultValue = true;
        takeLocation = sharedPref.getBoolean(getString(R.string.location_enabled), defaultValue);
    }


}
