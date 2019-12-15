package com.example.CustomCameraApp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class ImageSyncService extends JobIntentService {

    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference spaceRef;
    private DatabaseReference mDatabase;
    ArrayList<String> fullSyncedList;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    private final String FULL_LIST_EXTRA = "fullList";
    private final String FILES_LIST_EXTRA = "files list";
    private final int NOTIFICATION_ID = 100;
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        notificationManager = NotificationManagerCompat.from(this);
        builder = new NotificationCompat.Builder(this, "Custom Camera App");
        builder.setContentTitle("Image Sync")
                .setContentText("Sync in progress")
                .setSmallIcon(R.drawable.ic_sync_successful)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageRef = storage.getReference();
        fullSyncedList = intent.getStringArrayListExtra(FULL_LIST_EXTRA);
        for(String thumbnailPath:intent.getStringArrayListExtra(FILES_LIST_EXTRA)) {
            String imageName = thumbnailPath.substring(thumbnailPath.lastIndexOf("/")+1);
            String path = Environment.getExternalStorageDirectory() + "/CustomImage/";
            String imagePath = path + imageName;
            uploadImage(thumbnailPath,imagePath);
        }
        Collections.sort(fullSyncedList);
        mDatabase.child("images").setValue(fullSyncedList);
        builder.setContentText("Images have been uploaded");
        notificationManager.cancelAll();
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
                    fullSyncedList.add(thumbnailPath.substring(thumbnailPath.lastIndexOf("/")+1));
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    builder.setProgress((int)taskSnapshot.getTotalByteCount(), (int)taskSnapshot.getBytesTransferred(), false);
                    notificationManager.notify(100, builder.build());
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
                }
            });
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
