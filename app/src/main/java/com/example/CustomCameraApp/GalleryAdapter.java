package com.example.CustomCameraApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeMap;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<File> galleryList;
    private Context context;
    private OnImageClickListener mOnImageClickListener;
    private final String path = Environment.getExternalStorageDirectory() + "/CustomImage/";
    private TreeMap<Integer,Boolean> datesMap;
    private int previousI = -2;
    private int previousDate =0;
    String[][] finalFileArray;


    public GalleryAdapter(Context context, ArrayList<File> galleryList, OnImageClickListener onImageClickListener, TreeMap<Integer,Boolean> datesmap, String[][] finalFileArray) {

        this.galleryList = galleryList;
        this.context = context;
        this.mOnImageClickListener = onImageClickListener;
        this.datesMap = datesmap;
        this.finalFileArray = finalFileArray;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder viewHolder, int i) {
        String nextPathName = null;
            viewHolder.img1.setImageURI(Uri.parse(finalFileArray[i][0]));
            if (finalFileArray[i][1] != null) {
                viewHolder.img2.setImageURI(Uri.parse(finalFileArray[i][1]));
                nextPathName = finalFileArray[i][1].substring(finalFileArray[i][1].lastIndexOf("/")+1);
            }
            if (finalFileArray[i][2]!=null) {
                if (Integer.parseInt(finalFileArray[i][2]) == 0) {
                    viewHolder.timeTextView.setVisibility(View.VISIBLE);
                    viewHolder.timeTextView.setText("Earlier this hour...");
                } else if (Integer.parseInt(finalFileArray[i][2]) == 1) {
                    viewHolder.timeTextView.setVisibility(View.VISIBLE);
                    viewHolder.timeTextView.setText("1 hour ago...");
                } else {
                    viewHolder.timeTextView.setVisibility(View.VISIBLE);
                    viewHolder.timeTextView.setText(finalFileArray[i][2]+" hours ago...");
                }
            }
            final String currentFilename=finalFileArray[i][0].substring(finalFileArray[i][0].lastIndexOf("/")+1);
        viewHolder.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnImageClickListener.onImageClick(path+currentFilename);
                if (finalFileArray[i][3]=="1"){
                    Toast.makeText(context,"Synced",Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(nextPathName!=null){
            final String finalNextPathName = nextPathName;
            viewHolder.img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnImageClickListener.onImageClick(path+ finalNextPathName);
                    if (finalFileArray[i][3]=="1"){
                        Toast.makeText(context,"Synced",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return finalFileArray.length;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setData(String[][] finalFileArray) {
        this.finalFileArray = finalFileArray;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img1;
        private ImageView img2;
        private TextView timeTextView;

        public ViewHolder(View view) {
            super(view);
            img1 = view.findViewById(R.id.img1);
            img2 = view.findViewById(R.id.img2);
            timeTextView = view.findViewById(R.id.timeTextView);

        }
    }
}