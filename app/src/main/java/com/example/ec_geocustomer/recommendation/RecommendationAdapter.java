package com.example.ec_geocustomer.recommendation;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ec_geocustomer.R;
import com.example.ec_geocustomer.SearchViewFragment;

import java.util.ArrayList;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.Holder> {

    ArrayList<RecommendationData> recommendationDataArrayList;
    Context context;

    public RecommendationAdapter(ArrayList<RecommendationData> recommendationDataArrayList,Context context) {
        this.recommendationDataArrayList = recommendationDataArrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recommended_item, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Log.d(TAG,"adapter list : "+recommendationDataArrayList.get(position).getProductName());
        Glide
                .with(context)
                .load(recommendationDataArrayList.get(position).getImageurl())
                .centerCrop()
                .into(holder.imgView);
        holder.productname.setText(recommendationDataArrayList.get(position).getProductName());
        final int p=position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"itemView clicked "+p);

            }
        });

    }

    @Override
    public int getItemCount() {
        return recommendationDataArrayList.size();
    }




    public class Holder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView productname;
        View itemView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imgView=itemView.findViewById(R.id.product_image);
            productname=itemView.findViewById(R.id.product_name);
            this.itemView=itemView;
        }


    }
}
