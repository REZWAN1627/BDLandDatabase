package com.bdlanddatabase.BDLAND;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Search extends AppCompatActivity {


    public static final String TAG = "TAG";
    private String holding, location, sector;

    private TextView owner_name, owner_phone, owner_location, flat_details, flat_rent, owner_sector_location, flat_description;
    private FirebaseFirestore firebaseFirestore;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        owner_name = findViewById(R.id.owner_name);
        owner_phone = findViewById(R.id.owner_Phone);
        owner_location = findViewById(R.id.owner_Location);
        flat_details = findViewById(R.id.owner_flat_details);
        flat_rent = findViewById(R.id.owner_flat_rent);
        owner_sector_location = findViewById(R.id.owner_sector_Location);
        flat_description = findViewById(R.id.search_description);

        final ArrayList<String> imageList = new ArrayList<>();

        final RecyclerView recyclerView = findViewById(R.id.RecyclerView);

        final ImageAdapter adapter = new ImageAdapter(imageList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));



        firebaseFirestore = FirebaseFirestore.getInstance();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("NAZIPUR").child("Al hera para").child("graduate apu basha").child("BO-16");

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for (StorageReference fileRef : listResult.getItems()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageList.add(uri.toString());
                            Log.d(TAG, "onSuccess: -->" + uri.toString());

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            }
        });

        DocumentReference documentReference = firebaseFirestore.collection("HOUSE DETAILS").document("BO-16");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                owner_name.setText("Owner Name: " + value.getString("HOUSE OWNER NAME"));
                owner_phone.setText("Owner Phone Number: " + value.getString("HOUSE OWNER PHONE NUMBER"));
                owner_location.setText("Flat Location: " + value.getString("LOCATION"));
                flat_details.setText("Flat Details: " + value.getString("ROOMS AND BATH"));

                owner_sector_location.setText("Exact Location: " + value.getString("SECTOR LOCATION"));
                flat_description.setText("Description: " + value.getString("FLAT DESCRIPTION"));

                if (!value.exists()) {
                    Toast.makeText(Search.this, "ERROR!" + error, Toast.LENGTH_SHORT).show();
                }

            }

        });


    }
}

//Task<Uri> riversRef = mStorageRef.child("NAZIPUR").child("Harirampur").
//                child("khacha-Bazar er pichone").child("bb-10")