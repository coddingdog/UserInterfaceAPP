package com.echo.echofarm.Service.Impl;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.echo.echofarm.Data.Dto.GetPostDto;
import com.echo.echofarm.Interface.GetImgUrlListener;
import com.echo.echofarm.Interface.StoreImgListener;
import com.echo.echofarm.Service.StoreService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class StoreServiceImpl implements StoreService {
    private static final String TAG = "StoreService";
    FirebaseStorage storage;

    public StoreServiceImpl(){ storage = FirebaseStorage.getInstance(); }

    public void storeImage(Uri imageUri, String postId, String photoName, StoreImgListener storeImgListener){
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("photo/" + postId + "/" + photoName + ".png");
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG, "Failed Store Image", exception);
                storeImgListener.onFailed();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.w(TAG, "Success Store Image: " + imageUri);
                storeImgListener.onSuccess(postId);
            }
        });
    }

    public void getAllImageUrl(String postId, GetPostDto getPostDto, GetImgUrlListener getImgUrlListener){
        StorageReference storageRef = storage.getReference();
        storageRef.child("photo/" + postId).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            getImageUrl(postId, item.getName(), getPostDto, getImgUrlListener);
                        }
                        Log.w(TAG, "Success get All Image: " + getPostDto);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Failed get All Image: ", e);
                    }
                });
    }

    public void getImageUrl(String postId, String photoName, GetPostDto getPostDto, GetImgUrlListener getImgUrlListener){
        StorageReference storageRef = storage.getReference();

        storageRef.child("photo/" + postId + "/" + photoName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                getPostDto.getImgSrc().add(uri);
                getImgUrlListener.onSuccess(getPostDto);
                Log.w(TAG, "Success get Image: " + getPostDto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG, "Failed get Image: " + exception);

                System.out.println("photo/" + postId + "/" + photoName + ".png" + storageRef.child("photo/" + postId + "/" + photoName + ".png"));
                getImgUrlListener.onFailed();
            }
        });
    }
}
