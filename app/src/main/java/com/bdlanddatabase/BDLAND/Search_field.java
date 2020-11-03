package com.bdlanddatabase.BDLAND;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Search_field extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText filter;

    private Button next;
    private int rent_value;
    private FirebaseFirestore firebaseFirestore;
    private boolean flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_field);
        filter = findViewById(R.id.filter_amount);
        next = findViewById(R.id.button33);

        final ArrayList<String> rent = new ArrayList<>();
        final ArrayList<String> location = new ArrayList<>();
        final ArrayList<String> holding_number = new ArrayList<>();
        final ArrayList<String> sector_location = new ArrayList<>();

        final RecyclerView recyclerView = findViewById(R.id.RecyclerView2);

        final House_Number_adapter adapter = new House_Number_adapter(rent, this, location, holding_number, sector_location, flag);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseFirestore = FirebaseFirestore.getInstance();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Search_field.this,Search.class));
            }
        });


}


    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume: ==> started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag = true;
        Log.d(TAG, "onResume: ==> started");
    }

    @Override
    protected void onPause() {
        super.onPause();
        flag = false;

        Log.d(TAG, "onPause: ===> started");
    }


}