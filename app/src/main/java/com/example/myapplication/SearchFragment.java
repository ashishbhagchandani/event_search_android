package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SearchFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        Fragment fragment = new SearchformFragment();

        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.commit();

//                getParentFragmentManager().beginTransaction()
//                        .replace(R.id.cardView, eventsFragment)
//                        .addToBackStack(null)
//                        .commit();
//        Spinner spinner = rootView.findViewById(R.id.inputCategory);
//        spinner.setSelection(0);
//
//
//
//        View keyword = rootView.findViewById(R.id.inputKeyword);
//        View distance = rootView.findViewById(R.id.inputDistance);
//        View location = rootView.findViewById(R.id.inputLocation);
//        Switch autoDetect = rootView.findViewById(R.id.switch1);
//        boolean isChecked = autoDetect.isChecked();
//
//        Button searchSubmit = rootView.findViewById(R.id.searchButton);
//
//        searchSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(getActivity(), events.class);
////                startActivity(intent);
//                EventsFragment eventsFragment = new EventsFragment();
//                getParentFragmentManager().beginTransaction()
//                        .replace(R.id.cardView, eventsFragment)
//                        .addToBackStack(null)
//                        .commit();
//                Log.d("frag", String.valueOf(eventsFragment));
//                getKeyword = ((EditText) keyword).getText().toString();
//                getDistance = ((EditText) distance).getText().toString();
//                getLocation = ((EditText) location).getText().toString();
//                category = spinner.getSelectedItem().toString();
//                radius = Integer.parseInt(getDistance);
//                Log.d("keyword", getKeyword + radius + category + isChecked + getLocation);
//                boolean ipinfoCheck = ((Switch) rootView.findViewById(R.id.switch1)).isChecked();
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
//                    String localUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + getLocation + "&key=";
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
//
//                Log.d("Queue", String.valueOf(mStringRequest));
//                mRequestQueue.add(mStringRequest);
//            }
//        });

        return rootView;
    }


    public void searchEvents(){
        String k = null;
        Log.d("data", lat+log);
        try {
            k =  URLEncoder.encode("USC Trojans Football", StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
        }

        String url = "https://.uc.r.appspot.com/ticketmastersearch?keyword=" + k + "&segmentId=&lat=" + lat + "&lng=" + log + "&radius=" + radius;

StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
        new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Handle the response here
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
}
