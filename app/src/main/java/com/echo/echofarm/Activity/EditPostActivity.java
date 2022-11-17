package com.echo.echofarm.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.opengl.GLES30;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.echofarm.Interface.UploadPhotoClickListener;
import com.echo.echofarm.R;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditPostActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] tags = {"IT / 가전", "패션의류", "패션잡화", "식품", "스포츠 / 레저", "애완용품", "기타"};
    private String userSelectedTag = tags[0];
    private ImageButton firstUploadBtn, additionalUploadBtn, moreWantedProductBtn;
    private Button postUploadBtn;
    private EditText titleEditText, contentsEditText;
    private CheckBox disallowOtherTags;
    private TextView photoCheck, titleCheck;
    private boolean cameraPermission;
    private boolean fileReadPermission;
    private boolean fileWritePermission;
    private ArrayList<UploadedPhotoData> PhotoDataList;
    private Uri photoURI;
    private String currentPhotoPath;

    private ArrayList<String> wantedProductsList;
    private ArrayList<Integer> wantedTagsIdxList;
    private RecyclerView recyclerView;

    private final int RESULT_TAKE_PHOTO = 0;
    private final int RESULT_SELECT_PHOTO = 1;
    private final int RESULT_FROM_UPLOADED_PHOTOS_ACTIVITY = 2;

    public void checkNeededPermission() {
        if (ContextCompat.checkSelfPermission(EditPostActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            cameraPermission = true;
            Log.i("my", "camera permission granted", null);
        } else {
            // camera only this time 처리
            cameraPermission = false;
        }
        if (ContextCompat.checkSelfPermission(EditPostActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileWritePermission = true;
            Log.i("my", "write permission granted", null);
        }
        if (ContextCompat.checkSelfPermission(EditPostActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileReadPermission = true;
            Log.i("my", "read permission granted", null);
        }

        // 권한요청
        if (!cameraPermission || !fileWritePermission || !fileReadPermission) {
            Log.i("my", "request", null);
            ActivityCompat.requestPermissions(EditPostActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ,Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("my", "request result called", null);
        if(requestCode == 100 && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("my", "camera permission true", null);
                cameraPermission = true;
            }
            if(grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i("my", "write permission true", null);
                fileWritePermission = true;
            }
            if(grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Log.i("my", "read permission true", null);
                fileReadPermission = true;
            }
        }
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            Toast.makeText(EditPostActivity.this, "이 디바이스에서 카메라 기능을 지원하지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private void setFirstPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            firstUploadBtn.setImageBitmap(bitmap);
            firstUploadBtn.setPadding(0, 0, 0, 0);
        } else {
            Log.i("my", "bitmap is null", null);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            //after capture
            switch (requestCode) {
                case RESULT_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK && intent != null) {
                        // 처음 업로드한 사진 처리
                        if(PhotoDataList.size() == 0) {
                            additionalUploadBtn.setVisibility(View.VISIBLE);

                            File file = new File(currentPhotoPath);
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(getContentResolver(), Uri.fromFile(file));

                            ExifInterface ei = new ExifInterface(currentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);

                            setFirstPhoto(bitmap);
                        }

                        PhotoDataList.add(new UploadedPhotoData(photoURI));
                    }
                    break;
                }
                case RESULT_SELECT_PHOTO: {

                    if (intent != null) {

                        ClipData clipData = intent.getClipData();

                        if (clipData.getItemCount() + PhotoDataList.size() > 10) {   // 선택한 이미지가 11장 이상인 경우
                            Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                        } else {   // 선택한 이미지가 1장 이상 10장 이하인 경우

                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                                Log.e("", "" + imageUri.toString(), null);


                                // 첫 이미지 처리
                                if(PhotoDataList.size() == 0) {
                                    additionalUploadBtn.setVisibility(View.VISIBLE);
                                    Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri));
                                    setFirstPhoto(bitmap);
                                }

                                try {
                                    PhotoDataList.add(new UploadedPhotoData(imageUri));
                                    Log.i("my", ""+PhotoDataList.get(i).getPhotoUri().toString(), null);
                                } catch (Exception e) {
                                    Log.e("my", "File select error", e);
                                }
                            }
                        }
                    }
                    break;
                }
                case RESULT_FROM_UPLOADED_PHOTOS_ACTIVITY: {
                        PhotoDataList = intent.getBundleExtra("BUNDLE").getParcelableArrayList("URI_ARRAY");
                        if(PhotoDataList.size() == 0) {
                            firstUploadBtn.setVisibility(View.VISIBLE);
                            firstUploadBtn.setImageResource(R.drawable.camera);
                            firstUploadBtn.setPadding(50,50,50,50);
                            additionalUploadBtn.setVisibility(View.GONE);
                        } else {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(),
                                    PhotoDataList.get(0).getPhotoUri()));
                            setFirstPhoto(bitmap);
                        }
                }
                break;
            }
        } catch(Exception e){
                Log.e("my", "onActivityResult Error !", e);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.i("my", "create imagefile", null);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.echo.echofarm.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, RESULT_TAKE_PHOTO);
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        //uploadPhotoDialog.show();
        firstUploadBtn = findViewById(R.id.first_upload_photo_btn);
        additionalUploadBtn = findViewById(R.id.additional_upload_btn);
        postUploadBtn = findViewById(R.id.post_upload_btn);
        moreWantedProductBtn = findViewById(R.id.more_wanted_product_btn);
        titleEditText = findViewById(R.id.post_title_edittext);
        contentsEditText = findViewById(R.id.post_contents_edittext);
        disallowOtherTags = findViewById(R.id.other_tag_disallow_checkbox);
        photoCheck = findViewById(R.id.uploaded_check_text);
        titleCheck = findViewById(R.id.post_title_check_text);

        recyclerView = findViewById(R.id.wanted_product_recyclerview);

        firstUploadBtn.setOnClickListener(this);
        additionalUploadBtn.setOnClickListener(this);
        postUploadBtn.setOnClickListener(this);
        moreWantedProductBtn.setOnClickListener(this);

        // PhotoDataList 초기화
        PhotoDataList = new ArrayList<>();

        // 액션바 제목
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000'>게시물 작성</font>"));

        // 내 tag
        Spinner myTag = findViewById(R.id.my_tag);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, tags
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myTag.setAdapter(adapter);

        myTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userSelectedTag = tags[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 교환 tag
        wantedProductsList = new ArrayList<>();
        wantedTagsIdxList = new ArrayList<>();
        wantedProductsList.add("");
        wantedTagsIdxList.add(0);
        UserWantProductAdapter userWantProductAdapter =
                new UserWantProductAdapter(this, wantedProductsList, wantedTagsIdxList);
        recyclerView.setAdapter(userWantProductAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
    }

    // 사진업로드 버튼 처리
    @Override
    public void onClick(View view) {
        if(((view == firstUploadBtn) && (PhotoDataList.size() == 0)) || (view == additionalUploadBtn)) {

            // 권한 확인, 요청
            checkNeededPermission();
            UploadPhotoDialog uploadPhotoDialog = new UploadPhotoDialog(EditPostActivity.this,
                    new UploadPhotoClickListener() {
                        @Override
                        public void onTakePhotoClick() {
                            // 모든권한 획득, 사진찍기
                            if (cameraPermission && fileWritePermission) {
                                // 업로드사진 10장으로 제한
                                if(PhotoDataList.size() == 10) {
                                    Toast.makeText(EditPostActivity.this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                                else if (checkCameraHardware(EditPostActivity.this)) {
                                    dispatchTakePictureIntent();
                                }
                            }
                        }
                        @Override
                        public void onOpenGalleryClick() {
                            // 모든권한 획득, 갤러리 불러오기
                            if (fileReadPermission && fileWritePermission) {
                                // 업로드사진 10장으로 제한
                                if(PhotoDataList.size() == 10) {
                                    Toast.makeText(EditPostActivity.this, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // 다중 이미지를 가져올 수 있도록 세팅
                                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, RESULT_SELECT_PHOTO);
                                }
                            }
                        }
                    });
            uploadPhotoDialog.show();
        } else if(view == firstUploadBtn){

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.echo.echofarm", "com.echo.echofarm.Activity.UploadedPhotosActivity"));
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("URI_ARRAY", PhotoDataList);
            intent.putExtra("BUNDLE", bundle);
            startActivityForResult(intent, RESULT_FROM_UPLOADED_PHOTOS_ACTIVITY);

        }  else if(view == moreWantedProductBtn) {

            wantedProductsList.add("");
            wantedTagsIdxList.add(0);
            UserWantProductAdapter userWantProductAdapter =
                    new UserWantProductAdapter(this, wantedProductsList, wantedTagsIdxList);
            recyclerView.setAdapter(userWantProductAdapter);

        } else if(view == postUploadBtn) {
            boolean isPostable = true;

            if(PhotoDataList.size() == 0) {
                photoCheck.setVisibility(View.VISIBLE);
                isPostable = false;
            } else
                photoCheck.setVisibility(View.INVISIBLE);

            if(titleEditText.getText().toString().equals("")) {
                titleCheck.setVisibility(View.VISIBLE);
                isPostable = false;
            } else
                titleCheck.setVisibility(View.INVISIBLE);

            for(int i = 0; i < wantedTagsIdxList.size(); i++) {
                Log.i("my", wantedProductsList.get(i), null);
                Log.i("my", tags[wantedTagsIdxList.get(i)], null);
            }

            if(isPostable) {
                ArrayList<Uri> uriList = new ArrayList<>();
                for(UploadedPhotoData data : PhotoDataList)
                    uriList.add(data.getPhotoUri());

                String title = titleEditText.getText().toString();
                String contents = contentsEditText.getText().toString();
                Boolean isDisallowOtherTags = disallowOtherTags.isChecked();

            }
        }
    }
}