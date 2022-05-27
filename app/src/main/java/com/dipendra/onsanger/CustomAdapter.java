package com.dipendra.onsanger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList name, _id,immg;

    CustomAdapter(Activity activity, Context context, ArrayList name, ArrayList _id,ArrayList immg) {
        this.activity = activity;
        this.context = context;
        this.name = name;
        this._id = _id;
        this.immg=immg;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.name.setText(String.valueOf(name.get(position)));
holder.image.setImageBitmap((Bitmap) immg.get(position));
holder.cardView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

    }
});

    }

    @Override
    public int getItemCount() {
        return _id.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
ImageView image;
        TextView name;
        CardView cardView;
        LinearLayout mainLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);

            image = itemView.findViewById(R.id.imageView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            cardView = itemView.findViewById(R.id.cardView);
            //Animate Recyclerview
            // }
        }
    }
}
