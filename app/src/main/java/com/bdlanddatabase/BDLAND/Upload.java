package com.bdlanddatabase.BDLAND;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Upload extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 102;
    public static final int CAMERA_REQUEST_CODE = 101;
    public static final String TAG = "TAG";
    public static final int GALLERY_REQUEST_CODE = 105;
    public static final int STORAGE_READ_REQUEST = 115;
    public static final int STORAGE_WRITE_REQUEST = 201;
    private ImageView imageView;

    private ArrayList<File> camera_fileArrayList = new ArrayList<>();
    private ArrayList<Uri> camera_uriArrayList = new ArrayList<>();
    private ArrayList<Uri> gallery_uriArrayList = new ArrayList<>();
    private ArrayList<String> downloadImageUri = new ArrayList<String>();

    private Button next_btn;
    private EditText Number_of_rooms;
    private EditText flat_owner_name;
    private EditText flat_owner_phone_number;
    private EditText flat_rent;
    private EditText flat_holding_number;
    private EditText flat_description;
    private EditText flat_location;
    private EditText Sector_location;

    private Button camera_cap_btn;
    private Button gallery_cap_btn;

    private StorageReference mStorageRef;

    private String currentPhotoPath;
    private boolean flag = true;
    private int count = 0;

    private String House_Area = "NAZIPUR";

    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_download);
        flat_owner_name = findViewById(R.id.house_owner_name);
        flat_owner_phone_number = findViewById(R.id.house_owner_phone_number);
        Sector_location = findViewById(R.id.sector_location);

        imageView = findViewById(R.id.imageView);
        camera_cap_btn = findViewById(R.id.take_photo);
        gallery_cap_btn = findViewById(R.id.from_galary);

        Number_of_rooms = findViewById(R.id.Number_of_rooms);
        flat_rent = findViewById(R.id.flat_rent);
        flat_holding_number = findViewById(R.id.House_Number);
        flat_location = findViewById(R.id.location_extract);
        flat_description = findViewById(R.id.flat_description);

        next_btn = findViewById(R.id.upload_to_database);


        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();


        camera_cap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Log.d(TAG, "its working sector camera button");
                askCameraPermission();

            }
        });

        gallery_cap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(String.valueOf(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Images"), GALLERY_REQUEST_CODE);
                flag = false;

//              startActivityForResult((new Intent(Intent.EXTRA_ALLOW_MULTIPLE,
//                       MediaStore.Images.Media.EXTERNAL_CONTENT_URI)), GALLERY_REQUEST_CODE);
            }
        });


        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flat_holding_number.getText().toString().isEmpty()) {
                    flat_holding_number.setError("Field is Empty");
                    return;
                }
                if (flat_location.getText().toString().isEmpty()) {
                    flat_location.setError("Field is Empty");
                    return;
                }
                if (flat_owner_name.getText().toString().isEmpty()) {
                    flat_owner_name.setError("Field is Empty");
                    return;
                }
                if (flat_owner_phone_number.getText().toString().isEmpty()) {
                    flat_owner_phone_number.setError("Field is Empty");
                    return;
                }
                if (Sector_location.getText().toString().isEmpty()) {
                    Sector_location.setError("Field is Empty");
                    return;
                }
                if (Number_of_rooms.getText().toString().isEmpty()) {
                    Number_of_rooms.setError("Field is Empty");
                    return;
                }
                if (flat_rent.getText().toString().isEmpty()) {
                    flat_rent.setError("Field is Empty");
                    return;
                }

                if (flag) {
                    try {
                        uploadImageToFirebase(camera_fileArrayList.get(0).getName(), camera_uriArrayList.get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(Upload.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    for (int i = 1; i < camera_fileArrayList.size(); i++) {
                        try {
                            uploadImageToFirebase(camera_fileArrayList.get(i).getName(), camera_uriArrayList.get(i));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(Upload.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        Log.d(TAG, "onClick: for camera" + i);
                    }
                } else {
                    flag = true;
                    try {
                        uploadImageToFirebase("pic--->0", gallery_uriArrayList.get(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onClick: " + e.getMessage());
                        Toast.makeText(Upload.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    for (int i = 1; i < gallery_uriArrayList.size(); i++) {
                        String name = "pic-->";
                        try {
                            uploadImageToFirebase(name + i, gallery_uriArrayList.get(i));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(Upload.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                Log.d(TAG, "onClick: dataBase called");
                documentReference = firebaseFirestore.collection("HOUSE DETAILS").document(flat_holding_number.getText().toString());
                Map<String, Object> Client = new HashMap<>();

                Client.put("ROOMS AND BATH", Number_of_rooms.getText().toString());
                Client.put("RENT PER MONTH", Integer.parseInt(flat_rent.getText().toString()));
                Client.put("HOUSE NUMBER", flat_holding_number.getText().toString());
                Client.put("LOCATION", flat_location.getText().toString());
                Client.put("SECTOR LOCATION", Sector_location.getText().toString());
                Client.put("HOUSE OWNER NAME", flat_owner_name.getText().toString());
                Client.put("HOUSE OWNER PHONE NUMBER", flat_owner_phone_number.getText().toString());
                Client.put("FLAT DESCRIPTION", flat_description.getText().toString());

                documentReference.set(Client).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (aVoid == null || aVoid != null) {
                            Number_of_rooms.setText("");
                            flat_rent.setText("");
                            flat_holding_number.setText("");
                            flat_location.setText("");
                            Sector_location.setText("");
                            flat_owner_name.setText("");
                            flat_owner_phone_number.setText("");
                            flat_description.setText("");
                            Log.d(TAG, "onSuccess: Information stored successfully");
                            Toast.makeText(Upload.this, "Information stored successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(Upload.this, "Error!", Toast.LENGTH_SHORT).show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Upload.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                camera_fileArrayList.clear();
                camera_uriArrayList.clear();
                gallery_uriArrayList.clear();


            }
        });

        findViewById(R.id.back_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//


                startActivity(new Intent(Upload.this, MainActivity.class));
                finish();
            }
        });

    }

    private void uploadImageToFirebase(String name, Uri uri) throws IOException {
        final StorageReference image = mStorageRef.child(House_Area).child(flat_location.getText().toString()).
                child(Sector_location.getText().toString()).child(flat_holding_number.getText().toString()).child(name);

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();

//        image.putBytes(imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//
//                if (task.isSuccessful()){
//
//                }
//
//            }
//        });

        image.putBytes(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: uploaded "+ taskSnapshot.getUploadSessionUri().toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Upload.this, "Upload Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "its working sector---->permission not granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUEST);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST);
        } else {
            Log.d(TAG, "its working sector---->permission granted");
            dispatchTakePictureIntent();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUEST);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST);
            Log.d(TAG, "its working sector 4");
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUEST);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST);
            Toast.makeText(this, "Gallery permission is required", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Log.d(TAG, "its working sector-----> dispatchTakePictureIntent() called ");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d(TAG, "its working sector-----> device has camera");
            // Create the File where the photo should go
            File photoFile = null;
            try {
                Log.d(TAG, "its working sector----> calling createImageFile() method");
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(TAG, "its working sector-----> Error occurred while creating the File");
                // Error occurred while creating the File
                // ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d(TAG, "its working sector---> image has saved in photoFile --->File<--- object and photo has taken");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.bdlanddatabase.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        } else {
            androidStudioEmulatorCamera(true);// this method will be remove while building final apk
        }
    }

    private File createImageFile() throws IOException {
        Log.d(TAG, "its working sector------> createImageFile() method called to create image Uri and format");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void androidStudioEmulatorCamera(boolean flag) {
        if (flag) {

            Log.d(TAG, "its working sector----> physical device is not connect, so it called androidStudioEmulatorCamera() to use camera");
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Log.d(TAG, "androidStudioEmulatorCamera: -->" + takePictureIntent);
            // Create the File where the photo should go
            File photoFile = null;
            try {
                Log.d(TAG, "its working sector----> calling  createImageFile()");
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(TAG, "its working sector-----> Error occurred while creating the File");
                // Error occurred while creating the File
                // ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {


                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.bdlanddatabase.android.fileprovider",
                        photoFile);
                Log.d(TAG, "its working sector---> emulator--> image has saved in photoFile --->File<--- object and photo has taken " +
                        "and photoUri-->" + photoURI.toString());

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }

        } else {
            File photoFile = null;
            try {
                Log.d(TAG, "its working sector----> calling  createImageFile()");
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(TAG, "its working sector-----> Error occurred while creating the File");
                // Error occurred while creating the File
                // ...
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: intent data--->" + data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "its working sector 11");
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "its working sector 12");
            Log.d(TAG, "its working sector 13====>currentPath==>" + currentPhotoPath);
            File photoFile = new File(currentPhotoPath);
            camera_fileArrayList.add(count, photoFile);
            camera_uriArrayList.add(count, Uri.fromFile(photoFile));
            Log.d(TAG, "Camera Result: " + Uri.fromFile(photoFile));
            imageView.setImageURI(Uri.fromFile(photoFile));
            count++;
            galleryAddPic();


        }
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {
                int countClipData = data.getClipData().getItemCount();
                for (int i = 0; i < countClipData; i++) {
                    Log.d(TAG, "onActivityResult: doing: " + i);
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    gallery_uriArrayList.add(i, imageUri);

                }
                Log.d(TAG, "onActivityResult: done");
            }


        }
    }

    private void galleryAddPic() {
        Log.d(TAG, "galleryAddPic: ===>Called" + currentPhotoPath);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        Log.d(TAG, "galleryAddPic: ====>" + contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


}

