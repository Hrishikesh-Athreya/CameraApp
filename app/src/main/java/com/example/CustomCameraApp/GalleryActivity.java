package com.example.CustomCameraApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity implements OnImageClickListener {
    private RecyclerView recyclerView;
    private ImageView fullImageView;
    Boolean isPictureFullScreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.imagegallery);
        fullImageView = findViewById(R.id.fullImage);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<File> createLists = prepareData();
        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), createLists, this);
        recyclerView.setAdapter(adapter);
    }
    private ArrayList<File> prepareData(){

        ArrayList<File> files = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory() + "/CustomThumbImage/";
        File f = new File(path);
        File file[] = f.listFiles();
        Log.i("File length", "prepareData: "+file.length);
        for (int i=0; i < file.length; i++)
        {
            Log.i("Main activity", "prepareData: "+file[i].getPath());
            File file1 = new File(file[i].getPath());
            files.add(file1);
        }
        return files;
    }

    @Override
    public void onImageClick(String path) {
        recyclerView.setVisibility(View.GONE);
        fullImageView.setVisibility(View.VISIBLE);
        fullImageView.setImageURI(Uri.parse(path));
        isPictureFullScreen = true;
    }

    @Override
    public void onBackPressed() {
        if (isPictureFullScreen==true){
            recyclerView.setVisibility(View.VISIBLE);
            fullImageView.setVisibility(View.GONE);
            isPictureFullScreen = false;
        }else{
            super.onBackPressed();
        }
    }
}
