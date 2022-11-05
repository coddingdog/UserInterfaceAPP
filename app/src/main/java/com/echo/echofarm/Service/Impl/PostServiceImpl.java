package com.echo.echofarm.Service.Impl;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.echo.echofarm.Data.Dto.GetPostDto;
import com.echo.echofarm.Data.Dto.SendPostDto;
import com.echo.echofarm.Data.Entity.Post;
import com.echo.echofarm.Interface.GetImgUrlListener;
import com.echo.echofarm.Interface.StoreImgListener;
import com.echo.echofarm.Service.PostService;
import com.echo.echofarm.Service.StoreService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PostServiceImpl implements PostService {
    private static final String TAG = "PostService";
    private FirebaseFirestore db;
    private StoreService storeService;

    public PostServiceImpl(){
        db = FirebaseFirestore.getInstance();
        storeService = new StoreServiceImpl();
    }

    //중간 인터페이스 만들어서 서비스 연결시켜주기
    public void sendPostDto(SendPostDto sendPostDto, StoreImgListener storeImgListener){
        Post post = new Post();

        post.setUid(sendPostDto.getUid());
        post.setTitle(sendPostDto.getTitle());
        post.setContents(sendPostDto.getContents());
        post.setOwnProduct(sendPostDto.getOwnProduct());
        post.setOwnTag(sendPostDto.getOwnTag());
        post.setWantProduct(sendPostDto.getWantProduct());
        post.setWantTag(sendPostDto.getWantTag());
        post.setAllowOther(sendPostDto.isAllowOther());

        db.collection("post").add(post)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                    sendImgUri(documentReference.getId(), sendPostDto.getImgSrc(), storeImgListener);
                }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void sendImgUri(String docID, List<Uri> uriList, StoreImgListener storeImgListener){
        int i=0;
        for(Uri uri : uriList){
            System.out.println(uri+ docID+ Integer.toString(i));

            storeService.storeImage(uri, docID, Integer.toString(i), storeImgListener);
            i++;
        }
    }

    public void getPostDto(String postId, GetImgUrlListener getImgUrlListener){
        DocumentReference docRef = db.collection("post").document(postId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                GetPostDto getPostDto = new GetPostDto();
                Post post = documentSnapshot.toObject(Post.class);

                getPostDto.setUid(post.getUid());
                getPostDto.setTitle(post.getTitle());
                getPostDto.setContents(post.getContents());
                getPostDto.setOwnProduct(post.getOwnProduct());
                getPostDto.setOwnTag(post.getOwnTag());
                getPostDto.setWantProduct(post.getWantProduct());
                getPostDto.setWantTag(post.getWantTag());
                getPostDto.setAllowOther(post.isAllowOther());

                getPostDto.setImgSrc(new ArrayList<>());
                storeService.getAllImageUrl(postId, getPostDto, getImgUrlListener);

                Log.w(TAG, "Success getPostDto: " + getPostDto);
            }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Success getPostDto:", e);
                    }
                });;
    }
}
