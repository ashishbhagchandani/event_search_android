package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyViewHolder> {

    ArrayList<String> dps;
    ArrayList<String> artistNames;
    ArrayList<String> followers;
    ArrayList<String> popularity;
    ArrayList<String> checkout;
    ArrayList<String> album1;
    ArrayList<String> album2;
    ArrayList<String> album3;
    ArtistFragment ctx;
    private Context context;



    public ArtistAdapter(ArrayList<String> dps, ArrayList<String> artistNames, ArrayList<String> followers, ArrayList<String> popularity, ArrayList<String> checkout, ArrayList<String> album1, ArrayList<String> album2, ArrayList<String> album3, ArtistFragment ctx) {
        this.dps = dps;
        this.artistNames = artistNames;
        this.followers = followers;
        this.popularity = popularity;
        this.checkout = checkout;
        this.album1 = album1;
        this.album2 = album2;
        this.album3 = album3;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        return new ArtistAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(dps.get(position)).into(holder.dp);
        holder.artistname.setText(artistNames.get(position));
        holder.followers.setText(followers.get(position));
        holder.popularity.setText(popularity.get(position));
        holder.progressBar.setProgress(Integer.parseInt(popularity.get(position)));
        holder.checkout.setClickable(true);
        holder.checkout.setAutoLinkMask(Linkify.WEB_URLS);
        holder.checkout.setPaintFlags(holder.checkout.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        holder.checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(checkout.get(position));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        Picasso.get().load(album1.get(position)).into(holder.album1);
        Picasso.get().load(album2.get(position)).into(holder.album2);
        Picasso.get().load(album3.get(position)).into(holder.album3);
    }



    @Override
    public int getItemCount() {
        return artistNames.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView dp, album1, album2, album3;
        TextView artistname, followers, checkout, popularity;
        ProgressBar progressBar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.imageView2);
            artistname = itemView.findViewById(R.id.artistname);
            followers = itemView.findViewById(R.id.followers);
            checkout = itemView.findViewById(R.id.checkout);
            popularity = itemView.findViewById(R.id.textView3);
            album1 = itemView.findViewById(R.id.album1);
            album2 = itemView.findViewById(R.id.album2);
            album3 = itemView.findViewById(R.id.album3);
            progressBar = itemView.findViewById(R.id.ProgressBar);
        }


    }
}
