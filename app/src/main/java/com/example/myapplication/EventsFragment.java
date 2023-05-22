package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class EventsFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private String urlInfo = "https://ipinfo.io/json?token=8ef77191ded844";
    private StringRequest mStringRequest;
    private JSONObject ipInfo;
    private JSONObject ticketmasterEvents = null;
    private String log;
    private String lat;

    private String getKeyword;
    private String getDistance;
    private String getLocation;
    private String ipinfoCheck;
    private String category;

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
    private static View view;
    private static View snackbarview;
    private static Context context;
    private Bundle args = new Bundle();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_events, container, false);
        snackbarview = inflater.inflate(R.layout.snackbar, container, false);
        context = getContext();
        progressBar = view.findViewById(R.id.progress_bar);
        noeventsCard = view.findViewById(R.id.noeventscard);
        noeventsCard.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ImageView backSubmit = view.findViewById(R.id.back_button);
        backSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchformFragment fragment = new SearchformFragment();
                fragment.setArguments(args);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left,0,0,0);
                transaction.replace(R.id.contentFragment, fragment);
                transaction.commit();
            }
        });

        args = getArguments();
        getKeyword = args.getString("getKeyword");
        getDistance = args.getString("getDistance");
        getLocation = args.getString("getLocation");
        ipinfoCheck = args.getString("ipinfoCheck");
        category = args.getString("category");

//        Log.d("inevents", getKeyword);
//        SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
//        Gson gson = new Gson();
//        String json = prefs.getString("userList", null);
//        Type type = new TypeToken<ArrayList<EventShared>>() {}.getType();
//        ArrayList<EventShared> userList = gson.fromJson(json, type);
//        Log.d("get sp", String.valueOf(userList));

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
//
                // String Request initialized
                if(ipinfoCheck == "true"){
                    mStringRequest = new StringRequest(Request.Method.GET, "https://ipinfo.io/json?token=8ef77191ded844", new Response.Listener<String>() {
                        String[] points;
                        @Override
                        public void onResponse(String response) {
                            Log.d("hereeeee", log+lat+ipinfoCheck);
                            try {
                                ipInfo = new JSONObject(response);
                                points = ipInfo.getString("loc").split(",");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            log = points[1];
                            lat = points[0];
                            Log.d("points", log+lat);
                            searchEvents();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Log.i(TAG, "Error :" + error.toString());
                        }
                    });
                }else{
                    Log.d("hereeeee", log+lat+ipinfoCheck);
                    String localUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + getLocation + "&key=AIzaSyABArMjOk0wFHqa1HwslBVFDxG5x7NPeTQ";
                    mStringRequest = new StringRequest(Request.Method.GET, localUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                ipInfo = new JSONObject(response);
                                lat = ipInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
                                log = ipInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");
                                searchEvents();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Log.i(TAG, "Error :" + error.toString());
                        }
                    });
                }
                Log.d("TAG", "Data received from API: " + ticketmasterEvents);
                Log.d("Queue", String.valueOf(mStringRequest));
                mRequestQueue.add(mStringRequest);


        return view;
    }

    public void searchEvents(){
        String k = null;
        Log.d("data", lat+log);
        try {
            k =  URLEncoder.encode(getKeyword, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
        }

        String url = "https://hw8-ashish.uc.r.appspot.com/ticketmastersearch?keyword=" + k + "&segmentId=&lat=" + lat + "&lng=" + log + "&radius=" + getDistance;
        Log.d("urll", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response here
                        try {
                            ticketmasterEvents = new JSONObject(response);
                            Log.d("ticketmasterEvents", String.valueOf(ticketmasterEvents));
                            if(ticketmasterEvents != null && ticketmasterEvents.has("_embedded")) {
                                progressBar.setVisibility(View.GONE);
                                JSONObject embedded = ticketmasterEvents.getJSONObject("_embedded");
                                JSONArray events = embedded.getJSONArray("events");
                                Log.d("eventname", String.valueOf(events));
                                for (int i = 0; i < events.length(); i++) {
                                    JSONObject eventDetail = events.getJSONObject(i);
                                    eventNames.add(eventDetail.getString("name"));
                                    eventIds.add(eventDetail.getString("id"));

                                    JSONArray eventImg = eventDetail.getJSONArray("images");
                                    JSONObject getimg = eventImg.getJSONObject(0);
                                    eventImage.add(getimg.getString("url"));

                                    JSONObject venueembedded = eventDetail.getJSONObject("_embedded");
                                    JSONArray getvenues = venueembedded.getJSONArray("venues");
                                    JSONObject getvenuesname = getvenues.getJSONObject(0);
                                    eventVenue.add(getvenuesname.getString("name"));

                                    JSONArray classification = eventDetail.getJSONArray("classifications");
                                    JSONObject getclassification = classification.getJSONObject(0);
                                    JSONObject segment = getclassification.getJSONObject("segment");
                                    eventGenre.add(segment.getString("name"));

                                    JSONObject getdates = eventDetail.getJSONObject("dates");
                                    JSONObject getstart = getdates.getJSONObject("start");
                                    LocalDate date = LocalDate.parse(getstart.getString("localDate"));
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                                    String formattedDate = date.format(formatter);
                                    eventDate.add(formattedDate);

                                    if (getstart.has("localTime")) {
                                        SimpleDateFormat inputDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                                        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("h:mm a", Locale.US);
                                        Date datetime = null;
                                        String formattedTime = null;
                                        try {
                                            datetime = inputDateFormat.parse(getstart.getString("localTime"));
                                            formattedTime = outputTimeFormat.format(datetime);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        eventTime.add(String.valueOf(formattedTime));
                                    } else {
                                        eventTime.add("");
                                    }

                                }
                                SharedPreferences sharedPref = getActivity().getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);

                                EventitemAdapter eventitemAdapter = new EventitemAdapter(eventNames, eventIds, eventImage, eventVenue, eventGenre, eventDate, eventTime, sharedPref, EventsFragment.this);
                                recyclerView.setAdapter(eventitemAdapter);
                            }else{
                                progressBar.setVisibility(View.GONE);
                                noeventsCard.setVisibility(View.VISIBLE);
                            }


//                            Log.d("eventssss", String.valueOf(events));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d("ticketmaster", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ticketmaster error", String.valueOf(error));
                    }
                }) {
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
        Log.d("que", String.valueOf(queue));
    }

    public static void createSnackbaradd(String eventName){
        String str = eventName + " added to favorites";
        Snackbar snackbar = Snackbar.make(view, str, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.BLACK);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar));
        snackbar.show();
    }

    public static void createSnackbarremove(String eventName){
        String str = eventName + " removed from favorites";
        Snackbar snackbar = Snackbar.make(view, str, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.BLACK);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar));
        snackbar.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventitemfavAdapter.registerPref(getActivity(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventitemfavAdapter.unregisterPref(getActivity(), this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.d("actual key", s);
        if(s.equals("userList")){
            Log.d("actual clicked", String.valueOf(eventNames));
            SharedPreferences sharedPref = getActivity().getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
            EventitemAdapter eventitemAdapter = new EventitemAdapter(eventNames, eventIds, eventImage, eventVenue, eventGenre, eventDate, eventTime, sharedPref, EventsFragment.this);
            recyclerView.setAdapter(eventitemAdapter);
        }
    }
}