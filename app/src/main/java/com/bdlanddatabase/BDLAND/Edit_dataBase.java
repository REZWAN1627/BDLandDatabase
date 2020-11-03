package com.bdlanddatabase.BDLAND;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class Edit_dataBase extends AppCompatActivity {
    public static final String TAG = "TAG";
    private Button search, submit;
    private EditText Number_of_rooms;
    private EditText flat_owner_name;
    private EditText flat_owner_phone_number;
    private EditText flat_rent;
    private EditText flat_holding_number, getFlat_holding_number;
    private EditText flat_description;
    private EditText flat_location;
    private EditText Sector_location;
    private FirebaseFirestore firebaseFirestore;
    private TextView textView;
    private LinearLayout linearLayout;
    private String holding_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data_base);

        flat_owner_name = findViewById(R.id.house_owner_name_edit);
        flat_owner_phone_number = findViewById(R.id.house_owner_phone_number_edit);
        Sector_location = findViewById(R.id.sector_location_edit);

        Number_of_rooms = findViewById(R.id.Number_of_rooms_edit);
        flat_rent = findViewById(R.id.flat_rent_edit);
        flat_holding_number = findViewById(R.id.House_Number_edit);
        flat_location = findViewById(R.id.location_extract_edit);
        flat_description = findViewById(R.id.flat_description_edit);

        search = findViewById(R.id.edit_search);
        submit = findViewById(R.id.submit);
        getFlat_holding_number = findViewById(R.id.edit_search_houseNumber);
        linearLayout = findViewById(R.id.edit_layout1);
        textView = findViewById(R.id.edit_search_houseNumber_tv);


        firebaseFirestore = FirebaseFirestore.getInstance();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if (getFlat_holding_number.getText().toString().isEmpty()) {
                    getFlat_holding_number.setError("Field is Empty!");
                    return;
                }
                holding_number = getFlat_holding_number.getText().toString();
                getFlat_holding_number.setVisibility(View.GONE);
                textView.setText(holding_number);
                textView.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                setData();


            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);


                DocumentReference documentReference = firebaseFirestore.collection("HOUSE DETAILS").document(holding_number);
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
                        Log.d(TAG, "onSuccess: " + aVoid);
                        if (aVoid == null) {
                            Number_of_rooms.setText("");
                            flat_rent.setText("");
                            flat_holding_number.setText("");
                            flat_location.setText("");
                            Sector_location.setText("");
                            flat_owner_name.setText("");
                            flat_owner_phone_number.setText("");
                            flat_description.setText("");
                            Log.d(TAG, "onSuccess: Information stored successfully");
                            Toast.makeText(Edit_dataBase.this, "Information stored successfully", Toast.LENGTH_SHORT).show();
                            linearLayout.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                            getFlat_holding_number.setText("");
                            getFlat_holding_number.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(Edit_dataBase.this, "Error Data not found! ", Toast.LENGTH_SHORT).show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Edit_dataBase.this, "Error Data not found! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setData() {
        DocumentReference documentReference = firebaseFirestore.collection("HOUSE DETAILS").document(holding_number);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                flat_owner_name.setText(value.getString("HOUSE OWNER NAME"));
                flat_owner_phone_number.setText(value.getString("HOUSE OWNER PHONE NUMBER"));
                flat_location.setText(value.getString("LOCATION"));
                Number_of_rooms.setText(value.getString("ROOMS AND BATH"));
                int rent = value.getLong("RENT PER MONTH").intValue();
                flat_rent.setText(String.valueOf(rent));
                Log.d(TAG, "onEvent: " + value.getLong("RENT PER MONTH").intValue());
                Sector_location.setText(value.getString("SECTOR LOCATION"));
                flat_description.setText(value.getString("FLAT DESCRIPTION"));
                flat_holding_number.setText(value.getString("HOUSE NUMBER"));

                if (!value.exists()) {
                    Toast.makeText(Edit_dataBase.this, "ERROR!" + error, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}