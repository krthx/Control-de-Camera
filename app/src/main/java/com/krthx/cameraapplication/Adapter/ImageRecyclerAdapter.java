package com.krthx.cameraapplication.Adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.krthx.cameraapplication.R;

import java.util.List;

/**
 * Created by bryam on 14/07/17.
 */
public class ImageRecyclerAdapter  extends RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder> {
    private List<Bitmap> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imgView;
        public ViewHolder(View v) {
            super(v);
            imgView = (ImageView) v.findViewById(R.id.img_card);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ImageRecyclerAdapter(List<Bitmap> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ImageRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imagethumb, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ImageView img = (ImageView)v.findViewById(R.id.img_card);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.imgView.setImageBitmap(mDataset.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public List<Bitmap> Images() {
        return mDataset;
    }
}


