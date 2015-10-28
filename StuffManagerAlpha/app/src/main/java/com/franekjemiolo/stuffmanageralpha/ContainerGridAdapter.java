package com.franekjemiolo.stuffmanageralpha;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

/**
 * Created by frane_000 on 01.09.2015.
 */
public class ContainerGridAdapter extends ArrayAdapter<Container> {

    public static final int MAX_LINE_SIZE = 20;

    private Context mContext;
    List<Container> containerList;
    Container container;

    public ContainerGridAdapter (Context context, List<Container> containerList) {
        super(context, R.layout.grid_item, containerList);

        this.mContext = context;
        this.containerList = containerList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        container = getItem(position);
        ContainerViewHolder holder;

        if (convertView == null) {
            convertView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
            convertView = vi.inflate(R.layout.grid_item, parent, false);

            holder = new ContainerViewHolder();
            holder.img = (ImageView)convertView.findViewById(R.id.image);
            holder.title = (TextView)convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        }
        else {
            holder = (ContainerViewHolder) convertView.getTag();
        }

        holder.populate(container);

        return convertView;
    }

    static class ContainerViewHolder {
        public ImageView img;
        public TextView title;
        int finalHeight, finalWidth;
        File file;

        void populate(Container c) {
            if (c.getName().length() > MAX_LINE_SIZE) {
                title.setText(c.getName().substring(0,MAX_LINE_SIZE-1) + "..." + "(" + c.getNumber() + ")");
            }
            else {
                title.setText(c.getName() + " (" + c.getNumber() + ")");
            }

            // Now we should load the image from file.
            if (c.getImagePath() == "no_image") {
                img.setImageResource(R.drawable.default_container);
                return;
            }
            file = new File(c.getImagePath());
            if (file.exists()) {

                ViewTreeObserver vto = img.getViewTreeObserver();
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        img.getViewTreeObserver().removeOnPreDrawListener(this);
                        finalHeight = img.getMeasuredHeight();
                        finalWidth = img.getMeasuredWidth();
                        int targetW = finalWidth;
                        int targetH = finalHeight;


                        // Get the dimensions of the bitmap
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bmOptions.inJustDecodeBounds = true;
                        Log.d("tag", "Loading from " + file.getAbsolutePath());
                        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;

                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = scaleFactor;

                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

                        img.setImageBitmap(bitmap);
                        return true;
                    }
                });
                /*int targetW = finalWidth;
                int targetH = finalHeight;

                if ((targetH <= 0) || (targetW <= 0)) {
                    Log.d("tag", "populate failed..");
                    Log.d("tag", String.valueOf(targetH) + " " + String.valueOf(targetW));
                    return;
                }
                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                Log.d("tag", "Loading from " + file.getAbsolutePath());
                BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

                img.setImageBitmap(bitmap);*/
            }
            else {
                Log.d("Tag", "Image does not exist");
                img.setImageResource(R.drawable.default_container);
            }
        }

        void populate(Container c, boolean isBusy) {
            if (c.getName().length() > MAX_LINE_SIZE) {
                title.setText(c.getName().substring(0,MAX_LINE_SIZE-1) + "..." + "(" + c.getNumber() + ")");
            }
            else {
                title.setText(c.getName() + " (" + c.getNumber() + ")");
            }
            /*if (!isBusy) {
                // Download from somewhere
            } else set default*/
            // Now we should load the image from file.
            if (c.getImagePath() == "no_image") {
                img.setImageResource(R.drawable.default_container);
                return;
            }
            file = new File(c.getImagePath());
            if (file.exists()) {

                ViewTreeObserver vto = img.getViewTreeObserver();
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        img.getViewTreeObserver().removeOnPreDrawListener(this);
                        finalHeight = img.getMeasuredHeight();
                        finalWidth = img.getMeasuredWidth();
                        int targetW = finalWidth;
                        int targetH = finalHeight;

                        // Get the dimensions of the bitmap
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bmOptions.inJustDecodeBounds = true;
                        Log.d("tag", "Loading from " + file.getAbsolutePath());
                        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;

                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = scaleFactor;

                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

                        img.setImageBitmap(bitmap);
                        return true;
                    }
                });

                /*
                img.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        finalHeight = img.getHeight();
                        finalWidth = img.getWidth();
                        img.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
                int targetW = finalWidth;
                int targetH = finalHeight;
                if ((targetH <= 0) || (targetW <= 0)) {
                    Log.d("tag", "populate failed...");
                    Log.d("tag", String.valueOf(targetH) + " " + String.valueOf(targetW));
                    return;
                }
                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                Log.d("tag", "Loading from " + file.getAbsolutePath());
                BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

                img.setImageBitmap(bitmap);*/
            }
            else {
                Log.d("Tag", "Image does not exist");
                img.setImageResource(R.drawable.default_container);
            }
        }
    }

}
