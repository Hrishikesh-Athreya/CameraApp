package com.example.CustomCameraApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.TreeMap;

public class GalleryActivity extends AppCompatActivity implements OnImageClickListener {
    private RecyclerView recyclerView;
    private ImageView fullImageView;
    Boolean isPictureFullScreen = false;
    private TreeMap<Integer,Boolean> datesMap = new TreeMap<>();
    String[][] fileArray;
    String[][] finalFileArray;
    DatabaseReference mDatabase;
    ArrayList<String> syncFilesArray;
    ArrayList<String> syncedFiles;
    ArrayList<String> fullSyncedList;

    final String path = Environment.getExternalStorageDirectory() + "/CustomThumbImage/";
    final String NO_INTERNET_TOAST = "No internet available";
    final String ALL_IMAGES_UPLOADED_TOAST = "All images have been uploaded";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Gallery");
        setContentView(R.layout.activity_gallery);
        recyclerView = findViewById(R.id.imagegallery);
        fullImageView = findViewById(R.id.fullImage);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ArrayList<File> createLists = prepareData();
        GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), createLists, this,datesMap,finalFileArray);
        recyclerView.setAdapter(adapter);
        mDatabase.child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        syncedFiles = (ArrayList<String>)dataSnapshot.getValue();
                        fullSyncedList = (ArrayList<String>)dataSnapshot.getValue();
                        for(int i=0;i<finalFileArray.length;i++){
                            String currentImageName = finalFileArray[i][0].substring(finalFileArray[i][0].lastIndexOf("/")+1);
                            if (syncedFiles.contains(currentImageName)){
                                finalFileArray[i][3] = "1";
                                syncFilesArray.remove(finalFileArray[i][0]);
                            }
                            if(finalFileArray[i][1]!=null){
                                String secondImageName = finalFileArray[i][1].substring(finalFileArray[i][1].lastIndexOf("/")+1);
                                if (syncedFiles.contains(secondImageName)){
                                    finalFileArray[i][4] = "1";
                                    syncFilesArray.remove(finalFileArray[i][1]);
                                }
                            }
                        }
                        adapter.setData(finalFileArray);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }
    private ArrayList<File> prepareData(){
        long current_time = Calendar.getInstance().getTimeInMillis();

        ArrayList<File> files = new ArrayList<>();
        File f = new File(path);

        File[] fileOrdering = f.listFiles();
        File[] file = new File[fileOrdering.length];
        ArrayList<String> fileOrderingNames = new ArrayList<>();
        for (File a : fileOrdering){
            fileOrderingNames.add(a.getName());
        }
        Collections.sort(fileOrderingNames);
        Collections.reverse(fileOrderingNames);
        int l=0;
        for (String names:fileOrderingNames){
            String filepath = path + names;
            file[l] = new File(filepath);
            l++;
        }
        syncFilesArray = new ArrayList<>();
        fileArray = new String[file.length][5];
        for (int i=0; i < file.length; i++)
        {
            syncFilesArray.add(file[i].getPath());
            File file1 = new File(file[i].getPath());
            files.add(file1);
            long total_time = Calendar.getInstance().getTimeInMillis() - file1.lastModified();
            int hours = (int)total_time/3600000;
            if(!datesMap.containsKey(hours)){
                datesMap.put(hours,false);
            }
        }
        for (int i=0;i<files.size();i++){
            File currentFile = files.get(i);
            long total_time = current_time - currentFile.lastModified();
            int Firsthours = (int)total_time/3600000;
            fileArray[i][0] = currentFile.getPath();
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
        for (String[] strings : fileArray) {
            if (strings[0] != null) {
                length++;

            }
        }
        finalFileArray = new String[length][5];
        int j=0;
        for (String[] strings : fileArray) {
            if (strings[0] != null) {
                finalFileArray[j][0] = strings[0];
                finalFileArray[j][1] = strings[1];
                if (strings[2] != null) {
                    if (!datesMap.get(Integer.parseInt(strings[2]))) {
                        datesMap.put(Integer.parseInt(strings[2]), true);
                        finalFileArray[j][2] = strings[2];
                    }
                }
                System.out.println(finalFileArray[j][0] + " " + finalFileArray[j][1] + " " + finalFileArray[j][2]);
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
        if (isPictureFullScreen){
            recyclerView.setVisibility(View.VISIBLE);
            fullImageView.setVisibility(View.GONE);
            isPictureFullScreen = false;
        }else{
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            System.out.println(syncFilesArray.size());
            if (isNetworkAvailable()){
                if (syncFilesArray.size()==0){
                    Toast.makeText(GalleryActivity.this,ALL_IMAGES_UPLOADED_TOAST,Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(GalleryActivity.this, ImageSyncService.class);
                    intent.putExtra("files list", syncFilesArray);
                    if(fullSyncedList==null){
                        fullSyncedList = new ArrayList<>();
                    }
                    intent.putExtra("fullList",fullSyncedList);
                    startService(intent);
                }
            }else{
                Toast.makeText(GalleryActivity.this,NO_INTERNET_TOAST,Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

private boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager
            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
}
}
