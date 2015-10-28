package com.franekjemiolo.stuffmanageralpha;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class DetailedContainerViewActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    // Types of ordering
    public static final int DEFAULT_ORDERING = 1;
    public static final int NAME_ORDERING = 2;
    public static final int DATE_ORDERING = 3;

    public final static String SEARCHED_NAME = "com.franejemiolo.stuffmanageralpha.SEARCHED_NAME";
    public final static String REMOVED_ID = "com.franekjemiolo.stuffmanageralpha.REMOVED_ID";
    public final static String EXTRA_MESSAGE = "com.franekjemiolo.stuffmanageralpha.PARENT_ID";
    public final static String CONTAINER_PARENT_ID = "com.franekjemiolo.stuffmanageralpha.CONTAINER_PARENT_ID";
    public static final String CONTAINER_OR_ITEM_MESSAGE = "com.franekjemiolo.stuffmanageralpha.IS_CONTAINER";
    public static final String IS_EDITED = "com.franekjemiolo.stuffmanageralpha.IS_EDITED";
    public static final String ID_EDITED = "com.franekjemiolo.stuffmanageralpha.ID_EDITED";
    static final int NO_LOCATION_GIVEN = 1000;
    List<Container> containers;
    GridView gvContainers = null;
    ContainerGridAdapter adapterContainers;

    DatabaseHandler db;

    // Name text view
    //TextView nameTextView;

    // Location text view
    TextView locationTextView;

    // Button for loading coordinates to navigation
    Button showMapButton;

    // Container image view
    ImageView imgView;

    // Number text view
    TextView numberTextView;
    // The path to the image
    String path;

    // The parent container id.
    long parentId = -1l;

    // The parent container
    Container container;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_container_view);

        Intent intent = getIntent();

        parentId = intent.getLongExtra(EXTRA_MESSAGE, -1l);

        Log.d("tag", "Got parentId " + parentId);

        db = new DatabaseHandler(this);

        container = db.getContainer(parentId);

        setTitle(container.getName());

        locationTextView = (TextView) findViewById(R.id.locationText);
        showMapButton = (Button) findViewById(R.id.showMapButton);;
        if (container.getLatitude() == NO_LOCATION_GIVEN || container.getLongitude() == NO_LOCATION_GIVEN) {
            if (container.getIsContainer() == 1l) {
                locationTextView.setText(R.string.no_location_text_container);
            }
            else if (container.getIsContainer() == 0l) {
                locationTextView.setText(R.string.no_location_text_item);
            }
            showMapButton.setVisibility(View.INVISIBLE);
        }
        else {
            locationTextView.setText("Latitude: " + container.getLatitude() + "\n"
                    + "Longitude: " + container.getLongitude());

            if (container.getIsContainer() == 0) {
                showMapButton.setText("Show item on map");
            }

            showMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", container.getLatitude(), container.getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    DetailedContainerViewActivity.this.startActivity(intent);
                }
            });
        }


        /*nameTextView = (TextView) findViewById(R.id.containerName);
        nameTextView.setText(container.getName());*/

        numberTextView = (TextView) findViewById(R.id.numberText);
        if (container.getIsContainer() == 1) {
            numberTextView.setText("There are " + container.getNumber() + " objects of different " +
                    "kind in this container");
        }
        else if (container.getIsContainer() == 0) {
            numberTextView.setText("You have " + container.getNumber() + " objects of this type");
        }

        imgView = (ImageView) findViewById(R.id.containerImage);
        path = container.getImagePath();
        setPic();

        containers = db.getAllContainersOfParent(parentId);

        gvContainers = (GridView) findViewById(R.id.containerGridView);
        adapterContainers = new ContainerGridAdapter(this, containers);
        gvContainers.setAdapter(adapterContainers);

        gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DetailedContainerViewActivity.this, DetailedContainerViewActivity.class);
                Long parentId = ((Container) parent.getItemAtPosition(position)).getId();
                Log.d("tag", "setting parentid to " + String.valueOf(parentId));
                intent.putExtra(EXTRA_MESSAGE, parentId);
                DetailedContainerViewActivity.this.startActivity(intent);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        container = db.getContainer(parentId);

        setTitle(container.getName());

        locationTextView = (TextView) findViewById(R.id.locationText);
        showMapButton = (Button) findViewById(R.id.showMapButton);;
        if (container.getLatitude() == NO_LOCATION_GIVEN || container.getLongitude() == NO_LOCATION_GIVEN) {
            if (container.getIsContainer() == 1l) {
                locationTextView.setText(R.string.no_location_text_container);
            }
            else if (container.getIsContainer() == 0l) {
                locationTextView.setText(R.string.no_location_text_item);
            }
            showMapButton.setVisibility(View.INVISIBLE);
            showMapButton.setVisibility(View.INVISIBLE);
        }
        else {
            locationTextView.setText("Latitude: " + container.getLatitude() + "\n"
                    + "Longitude: " + container.getLongitude());
            showMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", container.getLatitude(), container.getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    DetailedContainerViewActivity.this.startActivity(intent);
                }
            });
        }

        /*nameTextView = (TextView) findViewById(R.id.containerName);
        nameTextView.setText(container.getName());*/

        numberTextView = (TextView) findViewById(R.id.numberText);
        if (container.getIsContainer() == 1) {
            numberTextView.setText("There are " + container.getNumber() + " objects of different " +
                    "kind in this container");
        }
        else if (container.getIsContainer() == 0) {
            numberTextView.setText("You have " + container.getNumber() + " objects of this type");
        }

        imgView = (ImageView) findViewById(R.id.containerImage);
        path = container.getImagePath();
        setPic();
        containers = db.getAllContainersOfParent(parentId);
        //
        gvContainers = (GridView) findViewById(R.id.containerGridView);
        adapterContainers = new ContainerGridAdapter(this, containers);
        gvContainers.setAdapter(adapterContainers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // if this is item we will load different menu
        if (container != null) {
            if (container.getIsContainer() == 0) {
                getMenuInflater().inflate(R.menu.menu_detailed_item_view, menu);
            }
            else if (container.getIsContainer() == 1) {
                getMenuInflater().inflate(R.menu.menu_detailed_container_view, menu);
            }
        }
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(DetailedContainerViewActivity.this, SearchResultsActivity.class);
                intent.putExtra(SEARCHED_NAME, query);
                DetailedContainerViewActivity.this.startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent (this, SettingsActivity.class);
            startActivity(intent);
        }

        if (container != null) {
            if (container.getIsContainer() == 0) {
                if (id == R.id.action_settings) {
                    Intent intent = new Intent (this, SettingsActivity.class);
                    startActivity(intent);
                    //return true;
                }
                else if (id == R.id.action_remove) {
                    FragmentManager fragmentManager = getFragmentManager();
                    RemoveItemDialogFragment newFragment = new RemoveItemDialogFragment();
                    newFragment.show(fragmentManager, "dialog");

                }
                else if (id == R.id.action_edit) {
                    Intent intent = new Intent(this, AddContainerActivity.class);
                    intent.putExtra(CONTAINER_PARENT_ID, container.getParentId());
                    intent.putExtra(CONTAINER_OR_ITEM_MESSAGE, 0l);
                    intent.putExtra(IS_EDITED, true);
                    intent.putExtra(ID_EDITED, container.getId());
                    startActivity(intent);
                }
            }
            else if (container.getIsContainer() == 1) {
                //noinspection SimplifiableIfStatement
                if (id == R.id.action_add_container) {
                    Intent intent = new Intent(this, AddContainerActivity.class);
                    intent.putExtra(CONTAINER_PARENT_ID, parentId);
                    intent.putExtra(CONTAINER_OR_ITEM_MESSAGE, 1l);
                    startActivity(intent);
                    //return true;
                } else if (id == R.id.action_add_item) {
                    Intent intent = new Intent(this, AddContainerActivity.class);
                    intent.putExtra(CONTAINER_PARENT_ID, parentId);
                    intent.putExtra(CONTAINER_OR_ITEM_MESSAGE, 0l);
                    startActivity(intent);
                    //return true;
                }
                else if (id == R.id.action_sort_by_name) {
                    containers = db.getAllContainersOfParentOrdered(parentId, NAME_ORDERING);

                    gvContainers = (GridView) findViewById(R.id.containerGridView);
                    adapterContainers = new ContainerGridAdapter(this, containers);
                    gvContainers.setAdapter(adapterContainers);

                    gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(DetailedContainerViewActivity.this, DetailedContainerViewActivity.class);
                            Long parentId = ((Container) parent.getItemAtPosition(position)).getId();
                            Log.d("tag", "setting parentid to " + String.valueOf(parentId));
                            intent.putExtra(EXTRA_MESSAGE, parentId);
                            DetailedContainerViewActivity.this.startActivity(intent);
                        }
                    });
                    //return true;
                }
                else if (id == R.id.action_sort_by_date) {
                    containers = db.getAllContainersOfParentOrdered(parentId, DATE_ORDERING);

                    gvContainers = (GridView) findViewById(R.id.containerGridView);
                    adapterContainers = new ContainerGridAdapter(this, containers);
                    gvContainers.setAdapter(adapterContainers);

                    gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(DetailedContainerViewActivity.this, DetailedContainerViewActivity.class);
                            Long parentId = ((Container) parent.getItemAtPosition(position)).getId();
                            Log.d("tag", "setting parentid to " + String.valueOf(parentId));
                            intent.putExtra(EXTRA_MESSAGE, parentId);
                            DetailedContainerViewActivity.this.startActivity(intent);
                        }
                    });
                    //return true;
                }
                else if (id == R.id.action_remove) {
                    Intent intent = new Intent(this, RemoveContainerActivity.class);
                    intent.putExtra(REMOVED_ID, parentId);
                    startActivity(intent);
                }
                else if (id == R.id.action_edit) {
                    Intent intent = new Intent(this, AddContainerActivity.class);
                    intent.putExtra(CONTAINER_PARENT_ID, container.getParentId());
                    intent.putExtra(CONTAINER_OR_ITEM_MESSAGE, 1l);
                    intent.putExtra(IS_EDITED, true);
                    intent.putExtra(ID_EDITED, container.getId());
                    startActivity(intent);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isLvBusy() {
        return false;
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        /*switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                lvBusy = false;
                adapterProducts.notifyDataSetChanged();
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                lvBusy = true;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                lvBusy = true;
                break;
        }*/
    }


    private void setPic() {
        // Get the dimensions of the View
        if (path == null) {
            Log.d("tag", "setPicFailed... due to path null");
            imgView.setImageResource(R.drawable.default_container);
        }
        else if (path.equals("no_image")) {
            // No image, load default.
            imgView.setImageResource(R.drawable.default_container);
        }
        else {
            File f = new File(path);
            if (f.exists()) {
                f = null;
                ViewTreeObserver vto = imgView.getViewTreeObserver();
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        imgView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int targetW = imgView.getMeasuredWidth();
                        int targetH = imgView.getMeasuredHeight();

                        if (targetH == 0 || targetW == 0) {
                            Log.d("tag", "nie dziala cos z wielkoscia");
                        }

                        // Get the dimensions of the bitmap
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bmOptions.inJustDecodeBounds = true;
                        Log.d("tag", "Loading from " + path);
                        BitmapFactory.decodeFile(path, bmOptions);
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;

                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = scaleFactor;

                        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);

                        imgView.setImageBitmap(bitmap);
                        return true;
                    }
                });
            }
            else {
                imgView.setImageResource(R.drawable.default_container);
            }
        }
    }


    public void removeThisItem () {
        if (container != null) {
            if (container.getIsContainer() == 0) {
                Log.d("tag", "Deleting item");
                db.deleteContainer(container);
                finish();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}