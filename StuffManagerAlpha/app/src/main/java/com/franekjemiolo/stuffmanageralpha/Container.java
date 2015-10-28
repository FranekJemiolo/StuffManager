package com.franekjemiolo.stuffmanageralpha;

/**
 * Created by frane_000 on 01.09.2015.
 */
// This class is used to represent default_container and also items - through this we can create recursive
// default_container model.
public class Container {

    // The default_container id
    long _id;
    // The parent default_container id -  <0 means no parent default_container.
    long _parentId;
    // It's name
    String _name;
    // The gps location of the default_container.
    // Longitude
    double _longitude;
    // Latitude
    double _latitude;
    // The file path to the image representing the default_container.
    String _imagePath;
    // The time of creation.
    String _dateTime;
    // This number represents how many different items are in the default_container if isContainer = 1,
    // when isContainer=0 this represents the amount of items of this kind.
    long _number;
    // If set to 1 - this is default_container, if 0 - this is item, you cannot store anything in it.
    long _isContainer;

    // Empty constructor
    public Container () {

    }

    // Constructors

    public Container (long id, long parentId, String name, String imagePath, double longitude,
                      double latitude, String dateTime, long number, long isContainer) {
        this._id = id;
        this._parentId = parentId;
        this._name = name;
        this._longitude = longitude;
        this._latitude = latitude;
        this._imagePath = imagePath;
        this._dateTime = dateTime;
        this._number = number;
        this._isContainer = isContainer;
    }

    public Container (String name, long parentId, String imagePath, double longitude,
                      double latitude, String dateTime, long number, long isContainer) {
        this._parentId = parentId;
        this._name = name;
        this._longitude = longitude;
        this._latitude = latitude;
        this._imagePath = imagePath;
        this._dateTime = dateTime;
        this._number = number;
        this._isContainer = isContainer;
    }

    // Getters and setters

    public long getId () {
        return this._id;
    }

    public void setId (long id) {
        this._id = id;
    }

    public long getParentId () {
        return this._parentId;
    }

    public void setParentId (long parentId) {
        this._parentId = parentId;
    }


    public String getName () {
        return this._name;
    }

    public void setName (String name) {
        this._name = name;
    }

    public double getLongitude () {
        return this._longitude;
    }

    public void setLongitude (double longitude) {
        this._longitude = longitude;
    }

    public double getLatitude () {
        return this._latitude;
    }

    public void setLatitude (double latitude) {
        this._latitude = latitude;


    }

    public String getImagePath () {
        return this._imagePath;
    }

    public void setImagePath (String imagePath) {
        this._imagePath = imagePath;
    }

    public String getDateTime () {
        return this._dateTime;
    }

    public void setDateTime (String dateTime) {
        this._dateTime = dateTime;
    }

    public long getNumber () {
        return this._number;
    }

    public void setNumber (long number) {
        this._number = number;
    }


    public long getIsContainer () {
        return this._isContainer;
    }

    public void setIsContainer (long isContainer) {
        this._isContainer = isContainer;
    }

}
