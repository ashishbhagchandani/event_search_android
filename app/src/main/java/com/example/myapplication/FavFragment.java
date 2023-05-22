package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ProgressBar progressBar;
    private ArrayList<String> eventNames = new ArrayList<>();
    private ArrayList<String> eventIds = new ArrayList<>();
    private ArrayList<String> eventImage = new ArrayList<>();
    private ArrayList<String> eventVenue = new ArrayList<>();
    private ArrayList<String> eventGenre = new ArrayList<>();
    private ArrayList<String> eventDate = new ArrayList<>();
    private ArrayList<String> eventTime = new ArrayList<>();
    private RecyclerView recyclerView;
    private CardView noeventsCard;
    private String storedJson = "";
    private EventitemfavAdapter eventitemAdapter;
    private SharedPreferences sharedPref;
    private static View view;
    private static View snackbarview;
    private static Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a reference to the shared preferences
        sharedPref = getActivity().getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        Log.d("storejson favcergv", "storedJson");
        view =  inflater.inflate(R.layout.fragment_fav, container, false);
        snackbarview = inflater.inflate(R.layout.snackbar, container, false);
        context = getContext();
        progressBar = view.findViewById(R.id.progress_barfav);
        noeventsCard = view.findViewById(R.id.nofavscard);
        noeventsCard.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.recyclerViewfav);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        sharedPref = getActivity().getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
//        Log.d("storejson fav", storedJson);
        if(storedJson == ""){
            storedJson = sharedPref.getString("userList", null);
        }
        updateUI(storedJson);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        EventitemAdapter.registerPref(getActivity(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventitemAdapter.unregisterPref(getActivity(), this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//        Log.d("actual key", s);
        if(s.equals("userList")){
            SharedPreferences sharedPref = getActivity().getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
            String storedJson = sharedPref.getString("userList", null);
//            Log.d("actual fav", storedJson);
            updateUI(storedJson);
        }
    }

    public static void createSnackbarremove(String eventName){
        String str = eventName + " removed from favorites";
        Snackbar snackbar = Snackbar.make(view, str, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.BLACK);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar));
        snackbar.show();
    }

    private void updateUI(String storedJson){
        JSONArray favstorage = null;
//        Log.d("updateui favstorage", String.valueOf(storedJson));
        if (storedJson != null) {
//            Log.d("updateui inside", String.valueOf(String.valueOf(storedJson) !="[]"));
            try {
                favstorage = new JSONArray(storedJson);
                if(favstorage.length() == 0){
                    noeventsCard.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    eventIds.clear();
                    eventNames.clear();
                    eventGenre.clear();
                    eventVenue.clear();
                    eventDate.clear();
                    eventTime.clear();
                    eventImage.clear();
                    eventitemAdapter = new EventitemfavAdapter(eventNames, eventIds, eventImage, eventVenue, eventGenre, eventDate, eventTime, sharedPref, FavFragment.this);
                    recyclerView.setAdapter(eventitemAdapter);
                }else{
                    eventIds.clear();
                    eventNames.clear();
                    eventGenre.clear();
                    eventVenue.clear();
                    eventDate.clear();
                    eventTime.clear();
                    eventImage.clear();
                    for (int i = 0; i < favstorage.length(); i++) {
                        JSONObject jsonObject = favstorage.getJSONObject(i);
                        eventIds.add(jsonObject.getString("eventId"));
                        eventNames.add(jsonObject.getString("eventName"));
                        eventGenre.add(jsonObject.getString("eventGenre"));
                        eventVenue.add(jsonObject.getString("eventVenue"));
                        eventDate.add(jsonObject.getString("eventDate"));
                        eventTime.add(jsonObject.getString("eventTime"));
                        eventImage.add(jsonObject.getString("eventImage"));
                    }
//                SharedPreferences sharedPref = getActivity().getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
                    eventitemAdapter = new EventitemfavAdapter(eventNames, eventIds, eventImage, eventVenue, eventGenre, eventDate, eventTime, sharedPref, FavFragment.this);
                    recyclerView.setAdapter(eventitemAdapter);
                    progressBar.setVisibility(View.GONE);
                    noeventsCard.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }else{
            noeventsCard.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

}