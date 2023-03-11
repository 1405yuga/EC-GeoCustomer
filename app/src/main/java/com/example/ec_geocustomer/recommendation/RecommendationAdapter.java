package com.example.ec_geocustomer.recommendation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ec_geocustomer.R;

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
        Glide
                .with(context)
                .load(recommendationDataArrayList.get(position).getImageurl())
                .centerCrop()
                .into(holder.imgView);
        holder.productname.setText(recommendationDataArrayList.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return recommendationDataArrayList.size();
    }


    public class Holder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView productname;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imgView=itemView.findViewById(R.id.product_image);
            productname=itemView.findViewById(R.id.product_name);
        }
    }
}
