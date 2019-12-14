package com.example.CustomCameraApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeMap;

public class GalleryActivity extends AppCompatActivity implements OnImageClickListener {
    private RecyclerView recyclerView;
    private ImageView fullImageView;
    Boolean isPictureFullScreen = false;
    private TreeMap<Integer,Boolean> datesMap = new TreeMap<>();
    String fileArray[][];
    String finalFileArray[][];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.imagegallery);
        fullImageView = findViewById(R.id.fullImage);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<File> createLists = prepareData();
        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), createLists, this,datesMap,finalFileArray);
        recyclerView.setAdapter(adapter);
    }
    private ArrayList<File> prepareData(){
        long current_time = Calendar.getInstance().getTimeInMillis();

        ArrayList<File> files = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory() + "/CustomThumbImage/";
        File f = new File(path);
        File file[] = f.listFiles();
        fileArray = new String[file.length][3];
        for (int i=0; i < file.length; i++)
        {
            File file1 = new File(file[i].getPath());
            files.add(file1);
            long total_time = Calendar.getInstance().getTimeInMillis() - file1.lastModified();
            int hours = (int)total_time/3600000;
            if(!datesMap.containsKey(hours)){
                datesMap.put(hours,false);
            }

        }
        Collections.reverse(files);

        for (int i=0;i<files.size();i++){
            File currentFile = files.get(i);
            long total_time = current_time - currentFile.lastModified();
            int Firsthours = (int)total_time/3600000;
            fileArray[i][0] = currentFile.getPath().toString();
            fileArray[i][2] = Integer.toString(Firsthours);
            if (i!=files.size()-1){
                File nextFile = files.get(i+1);
                long total_time2 = current_time - nextFile.lastModified();
                int Secondhours = (int)total_time2/3600000;
                if(Secondhours == Firsthours){
                    fileArray[i][1] = nextFile.getPath();
                    i++;
                }
            }
        }
        int length =0;
        for(int i=0;i<fileArray.length;i++){
            if (fileArray[i][0]!=null){
                length++;
            }
        }
        finalFileArray = new String[length][3];
        int j=0;
        for (int i=0;i<fileArray.length;i++){
            if (fileArray[i][0]!=null){
                finalFileArray[j][0] = fileArray[i][0];
                finalFileArray[j][1] = fileArray[i][1];
                if (fileArray[i][2]!=null){
                    if (!datesMap.get(Integer.parseInt(fileArray[i][2]))){
                        datesMap.put(Integer.parseInt(fileArray[i][2]),true);
                        finalFileArray[j][2] = fileArray[i][2];
                    }
                }
                j++;
            }

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
