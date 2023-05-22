package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EventitemfavAdapter extends RecyclerView.Adapter<EventitemfavAdapter.MyViewHolder> {

    ArrayList<String> eventNames;
    ArrayList<String> eventIds;
    ArrayList<String> eventImage;
    ArrayList<String> eventVenue;
    ArrayList<String> eventGenre;
    ArrayList<String> eventDate;
    ArrayList<String> eventTime;
    FavFragment ctx;
    private SharedPreferences mSharedPref;
    private ArrayList<EventShared> mEventSharedList;
    private FavFragment mFavFragment;
//    private SharedPreferences mSharedPref;


    public EventitemfavAdapter(ArrayList<String> eventNames, ArrayList<String> eventIds, ArrayList<String> eventImage, ArrayList<String> eventVenue, ArrayList<String> eventGenre, ArrayList<String> eventDate, ArrayList<String> eventTime, SharedPreferences mSharedPref, FavFragment ctx) {
        this.eventNames = eventNames;
        this.eventIds = eventIds;
        this.eventImage = eventImage;
        this.eventVenue = eventVenue;
        this.eventGenre = eventGenre;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.mSharedPref = mSharedPref;
        this.ctx = ctx;

//        Log.d("eventnamessss", String.valueOf(eventNames));
//        mSharedPref.registerOnSharedPreferenceChangeListener(this);

//        mSharedPref = PreferenceManager.getDefaultSharedPreferences(FavFragment);
//        mSharedPref.registerOnSharedPreferenceChangeListener(this);
    }


    @NonNull
    @Override
    public EventitemfavAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
//        MyViewHolder vh = new MyViewHolder(v);
//
//        return vh;
        return new EventitemfavAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(eventNames.get(position));
        holder.venue.setText(eventVenue.get(position));
        holder.genre.setText(eventGenre.get(position));
        holder.date.setText(eventDate.get(position));
        holder.time.setText(eventTime.get(position));
        String imageUrl = eventImage.get(position);
        String storedJson = mSharedPref.getString("userList", null);
//        Gson gson = new Gson();
//        Log.d("userlist fav", String.valueOf(storedJson));
        ArrayList<EventShared> userList;
        JSONArray favstorage = null;

        if (storedJson != null){
            try {
                favstorage = new JSONArray(storedJson);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < favstorage.length(); i++) {
                String getfavid = null;
                String getid = eventIds.get(position);
                try {
                    getfavid = favstorage.getJSONObject(i).getString("eventId");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (getfavid.equals(getid)){
                    holder.fav.setImageResource(R.drawable.heart_filled);
                    holder.fav.setTag(R.drawable.heart_filled);
                    break;
                }else{
                    holder.fav.setImageResource(R.drawable.heart_outline);
                    holder.fav.setTag(R.drawable.heart_outline);
                }
            }
        }else{
            holder.fav.setImageResource(R.drawable.heart_outline);
        }
//        Log.d("userlistfav", String.valueOf(storedJson));

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
                JSONArray favstorage = null;
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

                for (int i = 0; i < favstorage.length(); i++) {
                    String getfavid = "";
                    try {
                        getfavid = favstorage.getJSONObject(i).getString("eventId");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    if (getfavid.equals(getid)) {
                        FavFragment.createSnackbarremove(eventNames.get(position));
                        userList.remove(i);
                        checkfav = true;
                        break;
                    }
                }

                if (!checkfav) {
                    userList.add(new EventShared(eventNames.get(position), eventVenue.get(position), eventDate.get(position), eventTime.get(position), eventGenre.get(position), eventIds.get(position), eventImage.get(position)));
                }

                String json = gson.toJson(userList);
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putString("userList", json);
                editor.apply();

                if (checkfav) {
                    holder.fav.setImageResource(R.drawable.heart_outline);
                } else {
                    holder.fav.setImageResource(R.drawable.heart_filled);
                }

//                Log.d("checkuserlist", String.valueOf(favstorage));
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
        }
    }
}
