package com.example.CustomCameraApp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ImageSyncService extends JobIntentService {

    private static final int JOB_ID = 102;
    private ArrayList<String> syncedImages;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference spaceRef;
    private DatabaseReference mDatabase;
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageRef = storage.getReference();
        syncedImages = new ArrayList<>();

        for(String thumbnailPath:intent.getStringArrayExtra("files list")) {
            String imageName = thumbnailPath.substring(thumbnailPath.lastIndexOf("/")+1);
            String path = Environment.getExternalStorageDirectory() + "/CustomImage/";
            String imagePath = path + imageName;
            uploadImage(thumbnailPath,imagePath);
        }
        Collections.sort(syncedImages);
        mDatabase.child("images").setValue(syncedImages);
        stopSelf();
    }
    private void uploadImage(String thumbnailPath,String imagePath) {

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(thumbnailPath)));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            spaceRef = storageRef.child("thumbnails/"+thumbnailPath.substring(thumbnailPath.lastIndexOf("/")+1));
            spaceRef.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("Thumbnail Upload", "onSuccess: Thumbnail uploaded successfully");
                    syncedImages.add(thumbnailPath.substring(thumbnailPath.lastIndexOf("/")+1));
                }
            });
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imagePath)));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            spaceRef = storageRef.child("images/"+imagePath.substring(imagePath.lastIndexOf("/")+1));
            spaceRef.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i("Image Upload", "onSuccess: Image uploaded successfully");
                }
            });
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
