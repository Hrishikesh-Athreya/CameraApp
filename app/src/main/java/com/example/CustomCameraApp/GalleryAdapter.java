package com.example.CustomCameraApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<File> galleryList;
    private Context context;
    private OnImageClickListener mOnImageClickListener;
    private final String path = Environment.getExternalStorageDirectory() + "/CustomImage/";


    public GalleryAdapter(Context context, ArrayList<File> galleryList, OnImageClickListener onImageClickListener) {
        this.galleryList = galleryList;
        this.context = context;
        this.mOnImageClickListener = onImageClickListener;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder viewHolder, int i) {
        viewHolder.title.setText(galleryList.get(i).getName());
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final String currentFileName = galleryList.get(i).getName();
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnImageClickListener.onImageClick(path+currentFileName);
            }
        });
        viewHolder.img.setImageURI(Uri.parse(galleryList.get(i).getPath()));

    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView img;

        public ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            img = view.findViewById(R.id.img);

        }
    }
}