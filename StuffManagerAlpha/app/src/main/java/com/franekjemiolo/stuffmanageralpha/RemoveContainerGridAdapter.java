package com.franekjemiolo.stuffmanageralpha;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frane_000 on 05.09.2015.
 */
public class RemoveContainerGridAdapter extends ArrayAdapter<Container> {

    public static final int MAX_LINE_SIZE = 20;

    private Context mContext;
    List<Container> containerList;
    Container container;
    List<Container> selected;

    public RemoveContainerGridAdapter (Context context, List<Container> containerList) {
        super(context, R.layout.grid_item, containerList);

        this.mContext = context;
        this.containerList = containerList;
        this.selected = new ArrayList<Container>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        container = getItem(position);
        ContainerViewHolder holder;

        if (convertView == null) {
            convertView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
            convertView = vi.inflate(R.layout.remove_grid_item, parent, false);

            holder = new ContainerViewHolder();
            holder.img = (ImageView)convertView.findViewById(R.id.image);
            holder.del_img = (ImageView)convertView.findViewById(R.id.delete_image);
            holder.hl_img = (ImageView)convertView.findViewById(R.id.highlight_image);
            holder.title = (TextView)convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        }
        else {
            holder = (ContainerViewHolder) convertView.getTag();
        }

        holder.populate(container, selected.contains(container));

        return convertView;
    }

    public void addToSelected(Container c) {
        if (c != null) {
            if (!this.selected.contains(c)) {
                this.selected.add(c);
            }
        }
    }

    public void removeFromSelected(Container c) {
        if (c != null) {
            if (this.selected.contains(c)) {
                this.selected.remove(c);
            }
        }
    }

    static class ContainerViewHolder {
        // The image of the container/item
        public ImageView img;
        // The delete icon
        public ImageView del_img;
        // The highlight image
        public ImageView hl_img;
        public TextView title;
        int finalHeight, finalWidth;
        File file;

        void populate(Container c, boolean selected) {
            if (c.getName().length() > MAX_LINE_SIZE) {
                title.setText(c.getName().substring(0,MAX_LINE_SIZE-1) + "..." + "(" + c.getNumber() + ")");
            }
            else {
                title.setText(c.getName() + " (" + c.getNumber() + ")");
            }

            // Now we should load the image from file.
            if (c.getImagePath() == "no_image") {
                img.setImageResource(R.drawable.default_container);
                del_img.setImageResource(R.drawable.ic_delete_white_48dp);
                hl_img.setImageResource(R.drawable.item_highlight);
                if (selected) {
                    hl_img.setVisibility(View.VISIBLE);
                }
                else {
                    hl_img.setVisibility(View.INVISIBLE);
                }
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
                del_img.setImageResource(R.drawable.ic_delete_white_48dp);
                hl_img.setImageResource(R.drawable.item_highlight);
                if (selected) {
                    hl_img.setVisibility(View.VISIBLE);
                }
                else {
                    hl_img.setVisibility(View.INVISIBLE);
                }
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
                del_img.setImageResource(R.drawable.ic_delete_white_48dp);
                hl_img.setImageResource(R.drawable.item_highlight);
                if (selected) {
                    hl_img.setVisibility(View.VISIBLE);
                }
                else {
                    hl_img.setVisibility(View.INVISIBLE);
                }
            }
        }


    }
}
