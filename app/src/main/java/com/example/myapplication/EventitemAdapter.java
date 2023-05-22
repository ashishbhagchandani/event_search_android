package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class EventitemAdapter extends RecyclerView.Adapter<EventitemAdapter.MyViewHolder> {

    ArrayList<String> eventNames;
    ArrayList<String> eventIds;
    ArrayList<String> eventImage;
    ArrayList<String> eventVenue;
    ArrayList<String> eventGenre;
    ArrayList<String> eventDate;
    ArrayList<String> eventTime;
    EventsFragment ctx;
    private SharedPreferences mSharedPref;
    private Context context;



    public EventitemAdapter(ArrayList<String> eventNames, ArrayList<String> eventIds, ArrayList<String> eventImage, ArrayList<String> eventVenue, ArrayList<String> eventGenre, ArrayList<String> eventDate, ArrayList<String> eventTime, SharedPreferences mSharedPref, EventsFragment ctx) {
        this.eventNames = eventNames;
        this.eventIds = eventIds;
        this.eventImage = eventImage;
        this.eventVenue = eventVenue;
        this.eventGenre = eventGenre;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.mSharedPref = mSharedPref;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public EventitemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
//        MyViewHolder vh = new MyViewHolder(v);
//
//        return vh;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventitemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        Log.d("onbind", String.valueOf(eventNames));
//        Log.d("onbindid", String.valueOf(position));
        holder.name.setText(eventNames.get(position));
        holder.venue.setText(eventVenue.get(position));
        holder.genre.setText(eventGenre.get(position));
        holder.date.setText(eventDate.get(position));
        holder.time.setText(eventTime.get(position));
        String imageUrl = eventImage.get(position);
        String storedJson = mSharedPref.getString("userList", null);
//        Gson gson = new Gson();
//        Log.d("userlist", String.valueOf(storedJson));
        ArrayList<EventShared> userList;
        JSONArray favstorage = null;

        if (storedJson != null){
            try {
                favstorage = new JSONArray(storedJson);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
//            Log.d("eventlistfav", String.valueOf(favstorage));
            for (int i = 0; i < favstorage.length(); i++) {
                String getfavid = null;
                String getid = eventIds.get(position);
                try {
                    getfavid = favstorage.getJSONObject(i).getString("eventId");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (getfavid.equals(getid)){
                    Log.d("yesssss", getfavid + getid);
                    holder.fav.setImageResource(R.drawable.heart_filled);
                    holder.fav.setTag(R.drawable.heart_filled);
                    break;
                }else{
                    Log.d("else", getfavid + eventIds.get(position));
                    holder.fav.setImageResource(R.drawable.heart_outline);
                    holder.fav.setTag(R.drawable.heart_outline);
                }
            }
        }else{
            holder.fav.setImageResource(R.drawable.heart_outline);
        }
//        Log.d("userlist", String.valueOf(storedJson));

        Picasso.get().load(imageUrl).into(holder.img);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EventDetails.class);
                intent.putExtra("eventName", eventNames.get(position));
                intent.putExtra("eventId", eventIds.get(position));
                intent.putExtra("eventVenue", eventVenue.get(position));
                intent.putExtra("eventDate", eventDate.get(position));
                intent.putExtra("eventTime", eventTime.get(position));
                intent.putExtra("eventImage", eventImage.get(position));
                intent.putExtra("eventGenre", eventGenre.get(position));
                view.getContext().startActivity(intent);
            }
        });
        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String storedJson = mSharedPref.getString("userList", null);
                Gson gson = new Gson();
                ArrayList<EventShared> userList;
                JSONArray favstorage = new JSONArray();
                if (storedJson == null) {
                    userList = new ArrayList<>();
                } else {
                    try {
                        favstorage = new JSONArray(storedJson);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Type type = new TypeToken<ArrayList<EventShared>>() {}.getType();
                    userList = gson.fromJson(storedJson, type);
                }

                String getid = eventIds.get(position);
                boolean checkfav = false;
//                Log.d("checkuserlist", String.valueOf(userList));

                for (int i = 0; i < favstorage.length(); i++) {
                    String getfavid = "";
                    try {
                        getfavid = favstorage.getJSONObject(i).getString("eventId");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (getfavid.equals(getid)) {
                        EventsFragment.createSnackbarremove(eventNames.get(position));
                        userList.remove(i);
                        checkfav = true;
                        break;
                    }
                }

                if (!checkfav) {
                    EventsFragment.createSnackbaradd(eventNames.get(position));
                    userList.add(new EventShared(eventNames.get(position), eventVenue.get(position), eventDate.get(position), eventTime.get(position), eventGenre.get(position), eventIds.get(position), eventImage.get(position)));
                }

                String json = gson.toJson(userList);
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putString("userList", json);
                editor.apply();
//                mSharedPref.registerOnSharedPreferenceChangeListener(listener);
//                listener.onUserClicked(eventIds.get(position));
                Log.d("nullcheck", json);
//                ConstraintLayout cl = view.findViewById(R.id.cardevents);
//                Snackbar snackbar
//                        = Snackbar
//                        .make(cl, "Message is deleted", Snackbar.LENGTH_LONG);
//
//
//                snackbar.show();




                if (checkfav) {
                    holder.fav.setImageResource(R.drawable.heart_outline);
                } else {
                    holder.fav.setImageResource(R.drawable.heart_filled);
                }
                String storedJsoncheck = mSharedPref.getString("userList", null);

                try {
                    Log.d("checkuserlist", String.valueOf(new JSONArray(storedJsoncheck)));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public static void registerPref(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener){
        SharedPreferences pref = context.getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
        pref.registerOnSharedPreferenceChangeListener(listener);
    }
    public static void unregisterPref(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener){
        SharedPreferences pref = context.getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
        pref.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public int getItemCount() {
        return eventNames.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, id, venue, genre, date, time;
        ImageView img, fav;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventname);
            name.setSelected(true);
            img = itemView.findViewById(R.id.imageView);
            venue = itemView.findViewById(R.id.venue);
            venue.setSelected(true);
            genre = itemView.findViewById(R.id.genre);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            fav = itemView.findViewById(R.id.heartfilled);
//            id = itemView.findViewById(R.id.textView3);
        }


    }
}
