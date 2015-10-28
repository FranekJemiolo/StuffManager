package com.franekjemiolo.stuffmanageralpha;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

public class ContainerViewActivity extends AppCompatActivity implements AbsListView.OnScrollListener {


    public final static String REMOVED_ID = "com.franekjemiolo.stuffmanageralpha.REMOVED_ID";
    public final static String EXTRA_MESSAGE = "com.franekjemiolo.stuffmanageralpha.PARENT_ID";
    public final static String CONTAINER_PARENT_ID = "com.franekjemiolo.stuffmanageralpha.CONTAINER_PARENT_ID";
    public final static String CONTAINER_OR_ITEM_MESSAGE = "com.franekjemiolo.stuffmanageralpha.IS_CONTAINER";
    public final static String SEARCHED_NAME = "com.franejemiolo.stuffmanageralpha.SEARCHED_NAME";
    // Types of ordering
    public static final int DEFAULT_ORDERING = 1;
    public static final int NAME_ORDERING = 2;
    public static final int DATE_ORDERING = 3;

    List<Container> containers;
    GridView gvContainers = null;
    ContainerGridAdapter adapterContainers;

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_view);

        // Get all the containers from database

        db = new DatabaseHandler(this);
        //createFakeData();

        containers = db.getAllContainersOfParent(-1l);
        //
        gvContainers = (GridView) findViewById(R.id.containerGridView);
        adapterContainers = new ContainerGridAdapter(this, containers);
        gvContainers.setAdapter(adapterContainers);
        gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContainerViewActivity.this, DetailedContainerViewActivity.class);
                Long parentId = ((Container) parent.getItemAtPosition(position)).getId();
                Log.d("tag", "setting parentid to " + String.valueOf(parentId));
                intent.putExtra(EXTRA_MESSAGE, parentId);
                ContainerViewActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        containers = db.getAllContainersOfParent(-1l);
        //
        gvContainers = (GridView) findViewById(R.id.containerGridView);
        adapterContainers = new ContainerGridAdapter(this, containers);
        gvContainers.setAdapter(adapterContainers);
        gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContainerViewActivity.this, DetailedContainerViewActivity.class);
                Long parentId = ((Container) parent.getItemAtPosition(position)).getId();
                Log.d("tag", "setting parentid to " + String.valueOf(parentId));
                intent.putExtra(EXTRA_MESSAGE, parentId);
                ContainerViewActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_container_view, menu);
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
                Intent intent = new Intent(ContainerViewActivity.this, SearchResultsActivity.class);
                intent.putExtra(SEARCHED_NAME, query);
                ContainerViewActivity.this.startActivity(intent);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent (this, SettingsActivity.class);
            startActivity(intent);
            //return true;
        }
        else if (id == R.id.action_add_container) {
            Intent intent = new Intent(this, AddContainerActivity.class);
            intent.putExtra(CONTAINER_PARENT_ID, -1l);
            intent.putExtra(CONTAINER_OR_ITEM_MESSAGE, 1l);
            startActivity(intent);
            //return true;
        }
        else if (id == R.id.action_add_item) {
            Intent intent = new Intent(this, AddContainerActivity.class);
            intent.putExtra(CONTAINER_PARENT_ID, -1l);
            intent.putExtra(CONTAINER_OR_ITEM_MESSAGE, 0l);
            startActivity(intent);
            //return true;
        }
        else if (id == R.id.search) {
            onSearchRequested();
            //return true;
        }
        else if (id == R.id.action_sort_by_name) {
            containers = db.getAllContainersOfParentOrdered(-1, NAME_ORDERING);

            gvContainers = (GridView) findViewById(R.id.containerGridView);
            adapterContainers = new ContainerGridAdapter(this, containers);
            gvContainers.setAdapter(adapterContainers);

            gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ContainerViewActivity.this, DetailedContainerViewActivity.class);
                    Long parentId = ((Container) parent.getItemAtPosition(position)).getId();
                    Log.d("tag", "setting parentid to " + String.valueOf(parentId));
                    intent.putExtra(EXTRA_MESSAGE, parentId);
                    ContainerViewActivity.this.startActivity(intent);
                }
            });
            //return true;
        }
        else if (id == R.id.action_sort_by_date) {
            containers = db.getAllContainersOfParentOrdered(-1, DATE_ORDERING);

            gvContainers = (GridView) findViewById(R.id.containerGridView);
            adapterContainers = new ContainerGridAdapter(this, containers);
            gvContainers.setAdapter(adapterContainers);

            gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ContainerViewActivity.this, DetailedContainerViewActivity.class);
                    Long parentId = ((Container) parent.getItemAtPosition(position)).getId();
                    Log.d("tag", "setting parentid to " + String.valueOf(parentId));
                    intent.putExtra(EXTRA_MESSAGE, parentId);
                    ContainerViewActivity.this.startActivity(intent);
                }
            });
            //return true;
        }
        else if (id == R.id.action_remove) {
            Intent intent = new Intent(this, RemoveContainerActivity.class);
            intent.putExtra(REMOVED_ID, -1);
            startActivity(intent);
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


    @Override
    public void onDestroy(){
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }

}
