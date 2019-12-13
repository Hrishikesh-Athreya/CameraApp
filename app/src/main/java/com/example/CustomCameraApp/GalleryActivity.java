package com.example.CustomCameraApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        RecyclerView recyclerView = findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<File> createLists = prepareData();
        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);
    }
    private ArrayList<File> prepareData(){

        ArrayList<File> files = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory() + "/CustomImage/";
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
}
