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
import android.widget.SearchView;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    public final static String SEARCHED_NAME = "com.franejemiolo.stuffmanageralpha.SEARCHED_NAME";
    public final static String EXTRA_MESSAGE = "com.franekjemiolo.stuffmanageralpha.PARENT_ID";
    // Types of ordering
    public static final int DEFAULT_ORDERING = 1;
    public static final int NAME_ORDERING = 2;
    public static final int DATE_ORDERING = 3;

    List<Container> containers;
    GridView gvContainers = null;
    ContainerGridAdapter adapterContainers;

    DatabaseHandler db;

    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        if (intent.hasExtra(SEARCHED_NAME)) {
            query = intent.getStringExtra(SEARCHED_NAME).toLowerCase();
            Log.d("tag", query);
        }
        else {
            // nothing entered :<
            Log.d("tag", "Nic nie dostalem...");
        }
        setTitle("Search results");
        db = new DatabaseHandler(this);
        loadData();

    }

    @Override
    protected void onResume() {
        super.onResume();

        setTitle("Search results");
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
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
                Intent intent = new Intent(SearchResultsActivity.this, SearchResultsActivity.class);
                intent.putExtra(SEARCHED_NAME, query);
                SearchResultsActivity.this.startActivity(intent);
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
            return true;
        }
        else if (id == R.id.action_sort_by_name) {
            if ((query != null) && (!query.isEmpty())) {
                containers = db.getAllContainersWithNameOrdered(query, NAME_ORDERING);

                gvContainers = (GridView) findViewById(R.id.containerGridView);
                adapterContainers = new ContainerGridAdapter(this, containers);
                gvContainers.setAdapter(adapterContainers);

                gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(SearchResultsActivity.this, DetailedContainerViewActivity.class);
                        Long parentId = ((Container) parent.getItemAtPosition(position)).getId();
                        Log.d("tag", "setting parentid to " + String.valueOf(parentId));
                        intent.putExtra(EXTRA_MESSAGE, parentId);
                        SearchResultsActivity.this.startActivity(intent);
                    }
                });
            }
            return true;
        }
        else if (id == R.id.action_sort_by_date) {
            if ((query != null) && (!query.isEmpty())) {
                containers = db.getAllContainersWithNameOrdered(query, DATE_ORDERING);

                gvContainers = (GridView) findViewById(R.id.containerGridView);
                adapterContainers = new ContainerGridAdapter(this, containers);
                gvContainers.setAdapter(adapterContainers);

                gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(SearchResultsActivity.this, DetailedContainerViewActivity.class);
                        Long parentId = ((Container) parent.getItemAtPosition(position)).getId();
                        Log.d("tag", "setting parentid to " + String.valueOf(parentId));
                        intent.putExtra(EXTRA_MESSAGE, parentId);
                        SearchResultsActivity.this.startActivity(intent);
                    }
                });
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    private void loadData() {
        // Get all the containers from database


        //createFakeData();

        containers = db.getAllContainersWithName(query);
        //
        gvContainers = (GridView) findViewById(R.id.containerGridView);
        adapterContainers = new ContainerGridAdapter(this, containers);
        gvContainers.setAdapter(adapterContainers);
        gvContainers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchResultsActivity.this, DetailedContainerViewActivity.class);
                Long parentId = ((Container) parent.getItemAtPosition(position)).getId();
                Log.d("tag", "setting parentid to " + String.valueOf(parentId));
                intent.putExtra(EXTRA_MESSAGE, parentId);
                SearchResultsActivity.this.startActivity(intent);
            }
        });
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}