package com.example.myapplication;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SearchformFragment extends Fragment {
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String urlInfo = "https://ipinfo.io/json?token=8ef77191ded844";

    private JSONObject ipInfo;
    private String log;
    private String lat;
    private String getKeyword;
    private String getDistance;
    private String getLocation;
    private String category;
    private int radius;
    private static View rootView;
    private static Context context;
    private Bundle args = new Bundle();
//    private List<String> suggestions = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_searchform, container, false);
                Spinner spinner = rootView.findViewById(R.id.inputCategory);
        spinner.setSelection(0);
        ProgressBar progressBar = rootView.findViewById(R.id.ProgressBar1);
        progressBar.setVisibility(View.GONE);
        context = getContext();
//        args = getArguments();
        if(getArguments() != null){
            Log.d("not null args", String.valueOf(getArguments()));
            String ipinfoCheck = null;
            args = getArguments();
            getKeyword = args.getString("getKeyword");
            getDistance = args.getString("getDistance");
            getLocation = args.getString("getLocation");
            ipinfoCheck = args.getString("ipinfoCheck");
            category = args.getString("category");
            Log.d("not null args", String.valueOf(getKeyword));
            TextView keyword = rootView.findViewById(R.id.inputKeyword);
            keyword.setText(getKeyword);
            TextView distance = rootView.findViewById(R.id.inputDistance);
            distance.setText(getDistance);
            TextView location = rootView.findViewById(R.id.inputLocation);
            location.setText(getLocation);
            Switch autoDetect = rootView.findViewById(R.id.switch1);
            autoDetect.setChecked(Boolean.parseBoolean(ipinfoCheck));
            boolean isChecked = autoDetect.isChecked();
            if (isChecked) {
                location.setVisibility(View.GONE);
                autoDetect.setTrackTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.trackc)));
                autoDetect.setThumbTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.search_green)));
            } else {
                location.setVisibility(View.VISIBLE);
                autoDetect.setTrackTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.track)));
                autoDetect.setThumbTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.trackd)));
            }

        }
        Log.d("args", String.valueOf(getArguments()));


        AutoCompleteTextView autoCompleteTextView = rootView.findViewById(R.id.inputKeyword);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.suggestions, new ArrayList<String>());
        adapter.setNotifyOnChange(true);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                List<String> matchingItems = getMatchingItems(s.toString());
//                Log.d("s", s.toString());
                if(s.length() ==0){
                    progressBar.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                }


            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.d("sugg", String.valueOf(suggestions));
                if(s.length() >1){
                    mRequestQueue = Volley.newRequestQueue(getActivity());
                    String url = "https://hw8-ashish.uc.r.appspot.com/ticketmastersuggest?value=" + s.toString();
                    List<String> suggestions = new ArrayList<>();
                    mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                        Log.d("s res", String.valueOf(response));
                            try {
                                JSONObject resObj = new JSONObject(response);
                                if(resObj.has("_embedded")){
                                    JSONObject embedded = resObj.getJSONObject("_embedded");
                                    JSONArray attractions = embedded.getJSONArray("attractions");
                                    for (int i=0;i<attractions.length();++i){
                                        JSONObject attObj = attractions.getJSONObject(i);
                                        String getName = attObj.getString("name");
                                        suggestions.add(getName);
                                    }
                                    Log.d("suggestions", String.valueOf(suggestions));

                                }


                                autoCompleteTextView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                adapter.clear();
                                adapter.addAll(suggestions);

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

                    mRequestQueue.add(mStringRequest);
                }
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected value
                progressBar.setVisibility(View.GONE);
                String selectedValue = adapter.getItem(position);
                Log.d("selected", selectedValue);
                getKeyword = selectedValue;
                // Perform any necessary actions
                // ...
            }
        });
//
//
//
        TextView keyword = rootView.findViewById(R.id.inputKeyword);
        View distance = rootView.findViewById(R.id.inputDistance);
        View location = rootView.findViewById(R.id.inputLocation);
        Switch autoDetect = rootView.findViewById(R.id.switch1);
        boolean isChecked = autoDetect.isChecked();
        autoDetect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    location.setVisibility(View.GONE);
                    autoDetect.setTrackTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.trackc)));
                    autoDetect.setThumbTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.search_green)));
                } else {
                    location.setVisibility(View.VISIBLE);
                    autoDetect.setTrackTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.track)));
                    autoDetect.setThumbTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.trackd)));
                }
            }
        });

        Button searchSubmit = rootView.findViewById(R.id.searchButton);

        searchSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDistance = ((EditText) distance).getText().toString();
                getLocation = ((EditText) location).getText().toString();
                category = spinner.getSelectedItem().toString();
                radius = Integer.parseInt(getDistance);
                Log.d("keyword", getKeyword + radius + category + isChecked + getLocation);
                boolean ipinfoCheck = ((Switch) rootView.findViewById(R.id.switch1)).isChecked();
                if(getLocation == null  ||  getKeyword == null || getKeyword == "" || getKeyword.isEmpty()){
                    createSnackbaradd();
                    return;
                }
                if (getKeyword == null || getKeyword == "" || getKeyword.isEmpty()) {
                    keyword.setError("This field is required.");
                }


                args.putString("getKeyword", getKeyword);
                args.putString("getDistance", getDistance);
                args.putString("getLocation", getLocation);
                args.putString("ipinfoCheck", String.valueOf(ipinfoCheck));
                args.putString("category", category);
//                mRequestQueue = Volley.newRequestQueue(getActivity());
//
//                // String Request initialized
//                if(ipinfoCheck){
//                    mStringRequest = new StringRequest(Request.Method.GET, urlInfo, new Response.Listener<String>() {
//                        String[] points;
//                        @Override
//                        public void onResponse(String response) {
//                            Log.d("hereeeee", log+lat+ipinfoCheck);
//                            try {
//                                ipInfo = new JSONObject(response);
//                                points = ipInfo.getString("loc").split(",");
//                            } catch (JSONException e) {
//                                throw new RuntimeException(e);
//                            }
//                            log = points[0];
//                            lat = points[1];
//                            Log.d("points", log+lat);
//                            searchEvents();
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
////                            Log.i(TAG, "Error :" + error.toString());
//                        }
//                    });
//                }else{
//                    Log.d("hereeeee", log+lat+ipinfoCheck);
//                    String localUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + getLocation + "&key=AIzaSyABArMjOk0wFHqa1HwslBVFDxG5x7NPeTQ";
//                    mStringRequest = new StringRequest(Request.Method.GET, localUrl, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                ipInfo = new JSONObject(response);
//                                lat = ipInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
//                                log = ipInfo.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");
//                                searchEvents();
//                            } catch (JSONException e) {
//                                throw new RuntimeException(e);
//                            }
//
//                        }
//
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
////                            Log.i(TAG, "Error :" + error.toString());
//                        }
//                    });
//                }

//                Log.d("Queue", String.valueOf(mStringRequest));
//                mRequestQueue.add(mStringRequest);
                changeView();


            }
        });

        return rootView;
    }

    public static void createSnackbaradd(){
        String str = "Please fill all fields";
        Snackbar snackbar = Snackbar.make(rootView, str, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.BLACK);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar));
        snackbar.show();
    }

    public void changeView(){
        EventsFragment fragment = new EventsFragment();
        fragment.setArguments(args);
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left,0,0,0);
        transaction.replace(R.id.contentFragment, fragment);
        transaction.commit();
    }
    public void searchEvents(){
//        String k = null;
//        Log.d("data", lat+log);
//        try {
//            k =  URLEncoder.encode("USC Trojans Football", StandardCharsets.UTF_8.toString());
//        } catch (UnsupportedEncodingException e) {
//        }
//
//        String url = "https://hw8-ashish.uc.r.appspot.com/ticketmastersearch?keyword=" + k + "&segmentId=&lat=" + lat + "&lng=" + log + "&radius=" + radius;
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Handle the response here
//                        Log.d("ticketmaster", response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("ticketmaster error", String.valueOf(error));
//                    }
//                }) {
//        };
//        RequestQueue queue = Volley.newRequestQueue(getActivity());
//        queue.add(stringRequest);
//        Log.d("que", String.valueOf(queue));
    }
}