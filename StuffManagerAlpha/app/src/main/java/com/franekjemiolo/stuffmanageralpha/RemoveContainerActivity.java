package com.franekjemiolo.stuffmanageralpha;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RemoveContainerActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    // Types of ordering
    public static final int DEFAULT_ORDERING = 1;
    public static final int NAME_ORDERING = 2;
    public static final int DATE_ORDERING = 3;

    public final static String REMOVED_ID = "com.franekjemiolo.stuffmanageralpha.REMOVED_ID";
    public final static String SEARCHED_NAME = "com.franejemiolo.stuffmanageralpha.SEARCHED_NAME";
    public final static String EXTRA_MESSAGE = "com.franekjemiolo.stuffmanageralpha.PARENT_ID";
    public final static String CONTAINER_PARENT_ID = "com.franekjemiolo.stuffmanageralpha.CONTAINER_PARENT_ID";
    public static final String CONTAINER_OR_ITEM_MESSAGE = "com.franekjemiolo.stuffmanageralpha.IS_CONTAINER";
    static final int NO_LOCATION_GIVEN = 1000;

    List<Container> containers;
    GridView gvContainers = null;
    RemoveContainerGridAdapter adapterContainers;

    DatabaseHandler db;

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


    // The list of currently selected containers
    List<Container> selectedContainers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedContainers = new ArrayList<Container>();
        setContentView(R.layout.activity_remove_container);
        Intent intent = getIntent();

        parentId = intent.getLongExtra(REMOVED_ID, -1l);

        Log.d("tag", "Got parentId " + parentId);

        db = new DatabaseHandler(this);

        setTitle("Remove");
        if (parentId != -1) {
            container = db.getContainer(parentId);



            locationTextView = (TextView) findViewById(R.id.locationText);

            if (container.getLatitude() == NO_LOCATION_GIVEN || container.getLongitude() == NO_LOCATION_GIVEN) {
                if (container.getIsContainer() == 1l) {
                    locationTextView.setText(R.string.no_location_text_container);
                }
            } else {
                locationTextView.setText("Latitude: " + container.getLatitude() + "\n"
                        + "Longitude: " + container.getLongitude());

            }


        /*nameTextView = (TextView) findViewById(R.id.containerName);
        nameTextView.setText(container.getName());*/

            numberTextView = (TextView) findViewById(R.id.numberText);
            if (container.getIsContainer() == 1) {
                numberTextView.setText("There are " + container.getNumber() + " objects of different " +
                        "kind in this container");
            }
            imgView = (ImageView) findViewById(R.id.containerImage);
            path = container.getImagePath();
            setPic();
        }
        else {
            LinearLayout ll = (LinearLayout) findViewById(R.id.containerLayout);
            if (ll != null) {
                ll.removeAllViews();
                ll.setVisibility(View.GONE);
            }
        }

        containers = db.getAllContainersOfParent(parentId);

        gvContainers = (GridView) findViewById(R.id.containerGridView);
        adapterContainers = new RemoveContainerGridAdapter(this, containers);
        gvContainers.setAdapter(adapterContainers);

        gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedContainers.contains((Container) parent.getItemAtPosition(position))) {
                    selectedContainers.remove((Container) parent.getItemAtPosition(position));
                    ImageView hl_img = (ImageView) view.findViewById(R.id.highlight_image);
                    hl_img.setVisibility(View.INVISIBLE);
                } else {
                    selectedContainers.add((Container) parent.getItemAtPosition(position));
                    ImageView hl_img = (ImageView) view.findViewById(R.id.highlight_image);
                    hl_img.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        if (parentId != -1) {
            container = db.getContainer(parentId);



            locationTextView = (TextView) findViewById(R.id.locationText);

            if (container.getLatitude() == NO_LOCATION_GIVEN || container.getLongitude() == NO_LOCATION_GIVEN) {
                if (container.getIsContainer() == 1l) {
                    locationTextView.setText(R.string.no_location_text_container);
                }
            } else {
                locationTextView.setText("Latitude: " + container.getLatitude() + "\n"
                        + "Longitude: " + container.getLongitude());

            }


        /*nameTextView = (TextView) findViewById(R.id.containerName);
        nameTextView.setText(container.getName());*/

            numberTextView = (TextView) findViewById(R.id.numberText);
            if (container.getIsContainer() == 1) {
                numberTextView.setText("There are " + container.getNumber() + " objects of different " +
                        "kind in this container");
            }
            imgView = (ImageView) findViewById(R.id.containerImage);
            path = container.getImagePath();
            setPic();
        }
        else {
            LinearLayout ll = (LinearLayout) findViewById(R.id.containerLayout);
            if (ll != null) {
                ll.removeAllViews();
                ll.setVisibility(View.GONE);
            }
        }

        containers = db.getAllContainersOfParent(parentId);

        gvContainers = (GridView) findViewById(R.id.containerGridView);
        adapterContainers = new RemoveContainerGridAdapter(this, containers);
        gvContainers.setAdapter(adapterContainers);

        gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedContainers.contains((Container) parent.getItemAtPosition(position))) {
                    selectedContainers.remove((Container) parent.getItemAtPosition(position));
                    adapterContainers.removeFromSelected((Container) parent.getItemAtPosition(position));
                    ((ImageView) view.findViewById(R.id.highlight_image)).setVisibility(View.INVISIBLE);
                } else {
                    selectedContainers.add((Container) parent.getItemAtPosition(position));
                    adapterContainers.addToSelected((Container) parent.getItemAtPosition(position));
                    ((ImageView) view.findViewById(R.id.highlight_image)).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remove_container, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (container != null) {
            if (container.getIsContainer() == 1) {
                //noinspection SimplifiableIfStatement
                if (id == R.id.action_sort_by_name) {
                    selectedContainers.clear();
                    containers = db.getAllContainersOfParentOrdered(parentId, NAME_ORDERING);

                    gvContainers = (GridView) findViewById(R.id.containerGridView);
                    adapterContainers = new RemoveContainerGridAdapter(this, containers);
                    gvContainers.setAdapter(adapterContainers);

                    gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (selectedContainers.contains((Container) parent.getItemAtPosition(position))) {
                                selectedContainers.remove((Container) parent.getItemAtPosition(position));
                                adapterContainers.removeFromSelected((Container) parent.getItemAtPosition(position));
                                ((ImageView)view.findViewById(R.id.highlight_image)).setVisibility(View.INVISIBLE);
                            }
                            else {
                                selectedContainers.add((Container) parent.getItemAtPosition(position));
                                adapterContainers.addToSelected((Container) parent.getItemAtPosition(position));
                                ((ImageView)view.findViewById(R.id.highlight_image)).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                else if (id == R.id.action_sort_by_date) {
                    selectedContainers.clear();
                    containers = db.getAllContainersOfParentOrdered(parentId, DATE_ORDERING);

                    gvContainers = (GridView) findViewById(R.id.containerGridView);
                    adapterContainers = new RemoveContainerGridAdapter(this, containers);
                    gvContainers.setAdapter(adapterContainers);

                    gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (selectedContainers.contains((Container) parent.getItemAtPosition(position))) {
                                selectedContainers.remove((Container) parent.getItemAtPosition(position));
                                adapterContainers.removeFromSelected((Container) parent.getItemAtPosition(position));
                                ((ImageView)view.findViewById(R.id.highlight_image)).setVisibility(View.INVISIBLE);
                            }
                            else {
                                selectedContainers.add((Container) parent.getItemAtPosition(position));
                                adapterContainers.addToSelected((Container) parent.getItemAtPosition(position));
                                ((ImageView)view.findViewById(R.id.highlight_image)).setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                else if (id == R.id.action_remove_containers) {
                    if (!selectedContainers.isEmpty()) {
                        FragmentManager fragmentManager = getFragmentManager();
                        RemoveContainersDialogFragment newFragment = new RemoveContainersDialogFragment();
                        newFragment.show(fragmentManager, "dialog");
                    }
                    else {
                        // do nothing, no containers to delete
                        finish();
                    }
                }
            }
        }
        else {
            if (id == R.id.action_sort_by_name) {
                selectedContainers.clear();
                containers = db.getAllContainersOfParentOrdered(-1, NAME_ORDERING);

                gvContainers = (GridView) findViewById(R.id.containerGridView);
                adapterContainers = new RemoveContainerGridAdapter(this, containers);
                gvContainers.setAdapter(adapterContainers);

                gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (selectedContainers.contains((Container) parent.getItemAtPosition(position))) {
                            selectedContainers.remove((Container) parent.getItemAtPosition(position));
                            adapterContainers.removeFromSelected((Container) parent.getItemAtPosition(position));
                            ((ImageView)view.findViewById(R.id.highlight_image)).setVisibility(View.INVISIBLE);
                        }
                        else {
                            selectedContainers.add((Container) parent.getItemAtPosition(position));
                            adapterContainers.addToSelected((Container) parent.getItemAtPosition(position));
                            ((ImageView)view.findViewById(R.id.highlight_image)).setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            else if (id == R.id.action_sort_by_date) {
                selectedContainers.clear();
                containers = db.getAllContainersOfParentOrdered(-1, DATE_ORDERING);

                gvContainers = (GridView) findViewById(R.id.containerGridView);
                adapterContainers = new RemoveContainerGridAdapter(this, containers);
                gvContainers.setAdapter(adapterContainers);

                gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (selectedContainers.contains((Container) parent.getItemAtPosition(position))) {
                            selectedContainers.remove((Container) parent.getItemAtPosition(position));
                            adapterContainers.removeFromSelected((Container) parent.getItemAtPosition(position));
                            ((ImageView)view.findViewById(R.id.highlight_image)).setVisibility(View.INVISIBLE);
                        }
                        else {
                            selectedContainers.add((Container) parent.getItemAtPosition(position));
                            adapterContainers.addToSelected((Container) parent.getItemAtPosition(position));
                            ((ImageView)view.findViewById(R.id.highlight_image)).setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            else if (id == R.id.action_remove_containers) {
                if (!selectedContainers.isEmpty()) {
                    FragmentManager fragmentManager = getFragmentManager();
                    RemoveContainersDialogFragment newFragment = new RemoveContainersDialogFragment();
                    newFragment.show(fragmentManager, "dialog");
                }
                else {
                    // do nothing, no containers to delete
                    finish();
                }
            }
        }
        return super.onOptionsItemSelected(item);
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
        }
        else if (path.equals("no_image")) {
            // No image, load default.
            imgView.setImageResource(R.drawable.default_container);
        }
        else {

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
    }


    public void removeContainers() {
        db.deleteContainers(selectedContainers);
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
