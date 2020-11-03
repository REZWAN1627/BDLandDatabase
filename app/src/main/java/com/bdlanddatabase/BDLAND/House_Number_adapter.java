package com.bdlanddatabase.BDLAND;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class House_Number_adapter extends RecyclerView.Adapter<House_Number_adapter.ViewHolder> {
    private ArrayList<String> rent = new ArrayList<>();
    private Context mContext;
    private ArrayList<String> location = new ArrayList<>();
    private ArrayList<String> holding_number = new ArrayList<>();
    private ArrayList<String> sector_location = new ArrayList<>();
    private Boolean flag;


    public House_Number_adapter(ArrayList<String> rent, Context mContext, ArrayList<String> location, ArrayList<String> holding_number, ArrayList<String> sector_location, Boolean flag) {
        this.rent = rent;
        this.mContext = mContext;
        this.location = location;
        this.holding_number = holding_number;
        this.sector_location = sector_location;
        this.flag = flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item2, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if (flag) {
            holder.holding.setText((CharSequence) holding_number.get(position));
            holder.location.setText((CharSequence) location.get(position));
            holder.rent.setText((CharSequence) rent.get(position));
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, Search.class);
                    i.putExtra("Holding", holding_number.get(position));
                    i.putExtra("Location", location.get(position));
                    i.putExtra("Sector", sector_location.get(position));
                    mContext.startActivity(i);
                    holder.cardView.removeAllViews();

                }
            });

        } else {
            Toast.makeText(mContext, "Clearing", Toast.LENGTH_SHORT).show();
            holder.cardView.removeAllViews();

        }


    }

    @Override
    public int getItemCount() {
        return rent.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView holding, location, rent;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            holding = itemView.findViewById(R.id.item_house_holding);
            location = itemView.findViewById(R.id.item_location);
            rent = itemView.findViewById(R.id.item_price);
            cardView = itemView.findViewById(R.id.card_layout);
        }
    }


}
