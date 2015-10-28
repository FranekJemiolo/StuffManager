package com.franekjemiolo.stuffmanageralpha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by frane_000 on 31.08.2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // Types of ordering
    public static final int DEFAULT_ORDERING = 1;
    public static final int NAME_ORDERING = 2;
    public static final int DATE_ORDERING = 3;

    // All static variables
    // Database version
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "containersManager";

    // Container table name
    private static final String TABLE_CONTAINERS = "containers";

    // Containers table columns names
    private static final String CONTAINER_ID = "id";
    private static final String CONTAINER_PARENT_ID = "parent_id";
    private static final String CONTAINER_NAME = "name";
    private static final String CONTAINER_IMAGE_PATH = "image_path";
    private static final String CONTAINER_LONGITUDE = "longitude";
    private static final String CONTAINER_LATITUDE = "latitude";
    private static final String CONTAINER_DATE_TIME = "date_time";
    private static final String CONTAINER_NUMBER = "number";
    private static final String CONTAINER_IS_CONTAINER = "is_container";

    public DatabaseHandler (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating the tables
    @Override
    public void onCreate (SQLiteDatabase db) {
        String CREATE_CONTAINERS_TABLE = "CREATE TABLE " + TABLE_CONTAINERS + "("
                + CONTAINER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, "
                + CONTAINER_PARENT_ID + " INTEGER NOT NULL, "
                + CONTAINER_NAME + " TEXT NOT NULL, "
                + CONTAINER_IMAGE_PATH + " TEXT NOT NULL, "
                + CONTAINER_LONGITUDE + " REAL NOT NULL, "
                + CONTAINER_LATITUDE + " REAL NOT NULL, "
                + CONTAINER_DATE_TIME + " TEXT NOT NULL, "
                + CONTAINER_NUMBER + " INTEGER NOT NULL, "
                + CONTAINER_IS_CONTAINER + " INTEGER NOT NULL "
                + ")";
        db.execSQL(CREATE_CONTAINERS_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exsisted
        db.execSQL("DTOP TABLE IF EXISTS " + TABLE_CONTAINERS);

        // Create the tables again
        onCreate(db);
    }


    // Adding new default_container
    public Container addContainer(Container container) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTAINER_PARENT_ID, container.getParentId());
        values.put(CONTAINER_NAME, container.getName());
        values.put(CONTAINER_IMAGE_PATH, container.getImagePath());
        values.put(CONTAINER_LONGITUDE, container.getLongitude());
        values.put(CONTAINER_LATITUDE, container.getLatitude());
        values.put(CONTAINER_DATE_TIME, container.getDateTime());
        values.put(CONTAINER_NUMBER, container.getNumber());
        values.put(CONTAINER_IS_CONTAINER, container.getIsContainer());

        // Inserting row
        Container newcontainer = new Container();
        newcontainer.setId(db.insert(TABLE_CONTAINERS, null, values));
        newcontainer.setParentId(container.getParentId());
        newcontainer.setName(container.getName());
        newcontainer.setImagePath(container.getImagePath());
        newcontainer.setLongitude(container.getLongitude());
        newcontainer.setLatitude(container.getLatitude());
        newcontainer.setDateTime(container.getDateTime());
        newcontainer.setNumber(container.getNumber());
        newcontainer.setIsContainer(container.getIsContainer());
        // Closing the connection to the database
        db.close();
        return newcontainer;
    }

    // Getting single default_container
    public Container getContainer (long id) {
        if (id < 0) {
            return null;
        }
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTAINERS, new String[] { CONTAINER_ID, CONTAINER_PARENT_ID,
                        CONTAINER_NAME, CONTAINER_IMAGE_PATH, CONTAINER_LONGITUDE,
                        CONTAINER_LATITUDE, CONTAINER_DATE_TIME, CONTAINER_NUMBER,
                        CONTAINER_IS_CONTAINER}, CONTAINER_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Container container = new Container(Long.parseLong(cursor.getString(0)),
                    Long.parseLong(cursor.getString(1)), cursor.getString(2), cursor.getString(3),
                    Double.parseDouble(cursor.getString(4)),
                    Double.parseDouble(cursor.getString(5)), cursor.getString(6),
                    Long.parseLong(cursor.getString(7)), Long.parseLong(cursor.getString(8)));
            cursor.close();
            db.close();
            return container;
        }
        else {
            db.close();
            return null;
        }

    }

    // Getting all containers and items with given name
    public List<Container> getAllContainersWithName (String name) {
        List<Container> containerList = new ArrayList<Container>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTAINERS, new String[]{CONTAINER_ID, CONTAINER_PARENT_ID,
                        CONTAINER_NAME, CONTAINER_IMAGE_PATH, CONTAINER_LONGITUDE,
                        CONTAINER_LATITUDE, CONTAINER_DATE_TIME, CONTAINER_NUMBER,
                        CONTAINER_IS_CONTAINER}, CONTAINER_NAME + " = ? COLLATE NOCASE",
                new String[]{name}, null, null, null, null);

        if (cursor == null) {
            db.close();
            return containerList;
        }

        if (cursor.moveToFirst()) {
            do {
                Container container = new Container();
                container.setId(Long.parseLong(cursor.getString(0)));
                container.setParentId(Long.parseLong(cursor.getString(1)));
                container.setName(cursor.getString(2));
                container.setImagePath(cursor.getString(3));
                container.setLongitude(Double.parseDouble(cursor.getString(4)));
                container.setLatitude(Double.parseDouble(cursor.getString(5)));
                container.setDateTime(cursor.getString(6));
                container.setNumber(Long.parseLong(cursor.getString(7)));
                container.setIsContainer(Long.parseLong(cursor.getString(8)));
                containerList.add(container);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return containerList;

    }

    public List<Container> getAllContainersWithNameOrdered (String name, int order) {
        if (order == DEFAULT_ORDERING) {
            return getAllContainersWithName(name);
        }
        else {
            List<Container> containerList = new ArrayList<Container>();

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = null;
            if (order == NAME_ORDERING) {
                cursor = db.query(TABLE_CONTAINERS, new String[]{CONTAINER_ID, CONTAINER_PARENT_ID,
                                CONTAINER_NAME, CONTAINER_IMAGE_PATH, CONTAINER_LONGITUDE,
                                CONTAINER_LATITUDE, CONTAINER_DATE_TIME, CONTAINER_NUMBER,
                                CONTAINER_IS_CONTAINER}, CONTAINER_NAME + " = ? COLLATE NOCASE",
                        new String[]{name}, null, null, CONTAINER_NAME + " COLLATE NOCASE", null);
            }
            else if (order == DATE_ORDERING) {
                cursor = db.query(TABLE_CONTAINERS, new String[]{CONTAINER_ID, CONTAINER_PARENT_ID,
                                CONTAINER_NAME, CONTAINER_IMAGE_PATH, CONTAINER_LONGITUDE,
                                CONTAINER_LATITUDE, CONTAINER_DATE_TIME, CONTAINER_NUMBER,
                                CONTAINER_IS_CONTAINER}, CONTAINER_NAME + " = ? COLLATE NOCASE",
                        new String[]{name}, null, null, CONTAINER_DATE_TIME + " COLLATE NOCASE DESC", null);
            }

            if (cursor == null) {
                db.close();
                return containerList;
            }

            if (cursor.moveToFirst()) {
                do {
                    Container container = new Container();
                    container.setId(Long.parseLong(cursor.getString(0)));
                    container.setParentId(Long.parseLong(cursor.getString(1)));
                    container.setName(cursor.getString(2));
                    container.setImagePath(cursor.getString(3));
                    container.setLongitude(Double.parseDouble(cursor.getString(4)));
                    container.setLatitude(Double.parseDouble(cursor.getString(5)));
                    container.setDateTime(cursor.getString(6));
                    container.setNumber(Long.parseLong(cursor.getString(7)));
                    container.setIsContainer(Long.parseLong(cursor.getString(8)));
                    containerList.add(container);
                } while (cursor.moveToNext());
            }

            db.close();
            cursor.close();
            return containerList;
        }
    }


    // Getting all the default_container with parent's id
    public List<Container> getAllContainersOfParent (long parentId) {
        List<Container> containerList = new ArrayList<Container>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTAINERS, new String[]{CONTAINER_ID, CONTAINER_PARENT_ID,
                        CONTAINER_NAME, CONTAINER_IMAGE_PATH, CONTAINER_LONGITUDE,
                        CONTAINER_LATITUDE, CONTAINER_DATE_TIME, CONTAINER_NUMBER,
                        CONTAINER_IS_CONTAINER}, CONTAINER_PARENT_ID + " = ?",
                new String[]{String.valueOf(parentId)}, null, null, null, null);

        Log.d("Selecting", "Selecting from " + parentId);

        /*Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTAINERS + " WHERE " +
                CONTAINER_PARENT_ID + " = ?",  new String[]{String.valueOf(parentId)});*/

        if (cursor == null) {
            db.close();
            return containerList;
        }

        if (cursor.moveToFirst()) {
            do {
                Container container = new Container();
                container.setId(Long.parseLong(cursor.getString(0)));
                container.setParentId(Long.parseLong(cursor.getString(1)));
                container.setName(cursor.getString(2));
                container.setImagePath(cursor.getString(3));
                container.setLongitude(Double.parseDouble(cursor.getString(4)));
                container.setLatitude(Double.parseDouble(cursor.getString(5)));
                container.setDateTime(cursor.getString(6));
                container.setNumber(Long.parseLong(cursor.getString(7)));
                container.setIsContainer(Long.parseLong(cursor.getString(8)));
                containerList.add(container);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return containerList;

    }

    public List<Container> getAllContainersOfParentOrdered (long parentId, int order) {
        if (order == DEFAULT_ORDERING) {
            return getAllContainersOfParent(parentId);
        }
        else {
            List<Container> containerList = new ArrayList<Container>();

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = null;
            if (order == NAME_ORDERING) {
                cursor = db.query(TABLE_CONTAINERS, new String[]{CONTAINER_ID, CONTAINER_PARENT_ID,
                                CONTAINER_NAME, CONTAINER_IMAGE_PATH, CONTAINER_LONGITUDE,
                                CONTAINER_LATITUDE, CONTAINER_DATE_TIME, CONTAINER_NUMBER,
                                CONTAINER_IS_CONTAINER}, CONTAINER_PARENT_ID + " = ?",
                        new String[]{String.valueOf(parentId)}, null, null, CONTAINER_NAME + " COLLATE NOCASE", null);
            }
            else if (order == DATE_ORDERING) {
                cursor = db.query(TABLE_CONTAINERS, new String[]{CONTAINER_ID, CONTAINER_PARENT_ID,
                                CONTAINER_NAME, CONTAINER_IMAGE_PATH, CONTAINER_LONGITUDE,
                                CONTAINER_LATITUDE, CONTAINER_DATE_TIME, CONTAINER_NUMBER,
                                CONTAINER_IS_CONTAINER}, CONTAINER_PARENT_ID + " = ?",
                        new String[]{String.valueOf(parentId)}, null, null, CONTAINER_DATE_TIME + " COLLATE NOCASE DESC", null);
            }

            if (cursor == null) {
                db.close();
                return containerList;
            }

            if (cursor.moveToFirst()) {
                do {
                    Container container = new Container();
                    container.setId(Long.parseLong(cursor.getString(0)));
                    container.setParentId(Long.parseLong(cursor.getString(1)));
                    container.setName(cursor.getString(2));
                    container.setImagePath(cursor.getString(3));
                    container.setLongitude(Double.parseDouble(cursor.getString(4)));
                    container.setLatitude(Double.parseDouble(cursor.getString(5)));
                    container.setDateTime(cursor.getString(6));
                    container.setNumber(Long.parseLong(cursor.getString(7)));
                    container.setIsContainer(Long.parseLong(cursor.getString(8)));
                    containerList.add(container);
                } while (cursor.moveToNext());
            }

            db.close();
            cursor.close();
            return containerList;
        }
    }



    // Getting all containers
    public List<Container> getAllContainers () {
        List<Container> containerList = new ArrayList<Container>();

        // Select ALL query
        String selectQuery = "SELECT * FROM " + TABLE_CONTAINERS;

        SQLiteDatabase db = this.getReadableDatabase();

        // Run the query
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor == null) {
            db.close();
            return containerList;
        }

        if (cursor.moveToFirst()) {
            do {
                Container container = new Container();
                container.setId(Long.parseLong(cursor.getString(0)));
                container.setParentId(Long.parseLong(cursor.getString(1)));
                container.setName(cursor.getString(2));
                container.setImagePath(cursor.getString(3));
                container.setLongitude(Double.parseDouble(cursor.getString(4)));
                container.setLatitude(Double.parseDouble(cursor.getString(5)));
                container.setDateTime(cursor.getString(6));
                container.setNumber(Long.parseLong(cursor.getString(7)));
                container.setIsContainer(Long.parseLong(cursor.getString(8)));
                containerList.add(container);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return containerList;
    }
    // Getting containers count
    public long getContainersCount () {

        String selectQuery = "SELECT * FROM " + TABLE_CONTAINERS;

        SQLiteDatabase db = this.getReadableDatabase();

        // Run the query
        Cursor cursor = db.rawQuery(selectQuery, null);

        long count = cursor.getCount();

        cursor.close();

        return count;
    }
    // Updating single default_container
    public long updateContainer (Container container) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTAINER_PARENT_ID, container.getParentId());
        values.put(CONTAINER_NAME, container.getName());
        values.put(CONTAINER_IMAGE_PATH, container.getImagePath());
        values.put(CONTAINER_LONGITUDE, container.getLongitude());
        values.put(CONTAINER_LATITUDE, container.getLatitude());
        values.put(CONTAINER_DATE_TIME, container.getDateTime());
        values.put(CONTAINER_NUMBER, container.getNumber());
        values.put(CONTAINER_IS_CONTAINER, container.getIsContainer());

        // updating row
        return db.update(TABLE_CONTAINERS, values, CONTAINER_ID + " = ?",
                new String[] { String.valueOf((container.getId())) });
    }

    // Deleting single default_container
    public void deleteContainer (Container container) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CONTAINERS, CONTAINER_ID + " = ?",
                new String[]{String.valueOf(container.getId())});
        db.close();
    }

    // Delete all containers with given parent
    public void deleteContainersWithParent(Container container) {
        if (container != null) {
            if (container.getIsContainer() == 0) {
                // Item no sub containers, just delete
                deleteContainer(container);
            }
            else if (container.getIsContainer() == 1) {
                // Container - remove all sub containers.
                List<Container> containers = getAllContainersOfParent(container.getId());
                for (Container c : containers) {
                    deleteContainersWithParent(c);
                }
                deleteContainer(container);
            }
        }
    }

    // Delete all containers and their subcontainers
    public void deleteContainers (List<Container> containers) {
        if ((containers != null) && (!containers.isEmpty())) {
            for (Container c : containers) {
                if (c.getParentId() != -1) {
                    Container parentContainer = getContainer(c.getParentId());
                    if (parentContainer != null) {
                        parentContainer.setNumber(0);
                        updateContainer(parentContainer);
                    }
                }
                deleteContainersWithParent(c);
            }
        }

    }
}