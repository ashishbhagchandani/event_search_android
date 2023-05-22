package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class VenueFragment extends Fragment {
    private MapView mapView;
    private StringRequest mStringRequest;
    private View view;
    private double Latitude;
    private double Longitude;
    private String longitude;
    private String latitude;
    private RequestQueue mRequestQueue;
    private GoogleMap mMap;
    private LatLng location;
    ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_venue, container, false);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        getvenuedetails();

        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                location = new LatLng(Latitude, Longitude);
                Log.d("location1", String.valueOf(location));
                mMap.addMarker(new MarkerOptions().position(location));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            }
        });

        return view;
    }

    public void getvenuedetails(){
        VenueModel venueModel = new ViewModelProvider(requireActivity()).get(VenueModel.class);
        venueModel.getData().observe(getViewLifecycleOwner(), data -> {
            Log.d("viewmodel", data);

            String getvenue = "";
            try{
                getvenue = URLEncoder.encode(data, StandardCharsets.UTF_8.toString());
            }catch(UnsupportedEncodingException e){
            }
            String url = "https://hw8-ashish.uc.r.appspot.com/ticketmastervenue?venue=" + getvenue;
            mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("venue res", String.valueOf(response));
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        JSONArray venuesArr = responseObj.getJSONObject("_embedded").getJSONArray("venues");
                        JSONObject venuesObj = venuesArr.getJSONObject(0);
                        String getvenue = venuesObj.getString("name");
                        JSONObject getaddobj = venuesObj.getJSONObject("address");
                        String getadd = getaddobj.getString("line1");

                        JSONObject locationobj = venuesObj.getJSONObject("location");

                        longitude = locationobj.getString("longitude");
                        latitude = locationobj.getString("latitude");
                        Latitude = Double.parseDouble(latitude);
                        Longitude = Double.parseDouble(longitude);
                        location = new LatLng(Latitude, Longitude);
                        mMap.addMarker(new MarkerOptions().position(location));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
                        String phone = "";
                        JSONObject getcityobj = venuesObj.getJSONObject("city");
                        String getcity = getcityobj.getString("name");
                        if(venuesObj.has("boxOfficeInfo")){
                            JSONObject boxobj = venuesObj.getJSONObject("boxOfficeInfo");

                            if(boxobj.has("phoneNumberDetail")){
                                phone = boxobj.getString("phoneNumberDetail");
                                TextView contactt = view.findViewById(R.id.contactt);
                                contactt.setText(phone);
                                contactt.setSelected(true);
                            }else{
                                TextView contact = view.findViewById(R.id.contact);
                                contact.setVisibility(view.GONE);
                                TextView contactt = view.findViewById(R.id.contactt);
                                contactt.setVisibility(view.GONE);
                            }
                            String openHr = boxobj.getString("openHoursDetail");


                            JSONObject generalInfo = venuesObj.getJSONObject("generalInfo");
                            String generalRule = generalInfo.getString("generalRule");
                            String childRule = generalInfo.getString("childRule");

                            TextView generalrulest = view.findViewById(R.id.generalrulest);
                            generalrulest.setText(generalRule);
                            expandLines(generalrulest);
                            TextView childrulest = view.findViewById(R.id.childrulest);
                            childrulest.setText(childRule);
                            expandLines(childrulest);
                            TextView openhourst = view.findViewById(R.id.openhourst);
                            openhourst.setText(openHr);
                            expandLines(openhourst);
                        }else{
                            CardView cardview = view.findViewById(R.id.details);
                            cardview.setVisibility(View.GONE);
                            TextView contact = view.findViewById(R.id.contact);
                            contact.setVisibility(view.GONE);
                            TextView contactt = view.findViewById(R.id.contactt);
                            contactt.setVisibility(view.GONE);
                        }


                        JSONObject getstateobj = venuesObj.getJSONObject("state");
                        String getstate = getstateobj.getString("name");
                        String citystate = getcity + ", " + getstate;
                        TextView venuenamet = view.findViewById(R.id.venuenamet);
                        venuenamet.setText(getvenue);
                        venuenamet.setSelected(true);
                        TextView addresst = view.findViewById(R.id.addresst);
                        addresst.setText(getadd);
                        addresst.setSelected(true);
                        TextView citystatet = view.findViewById(R.id.citystatet);
                        citystatet.setText(citystate);
                        citystatet.setSelected(true);



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
        });
    }

    public void expandLines(TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            boolean check = false;

            @Override
            public void onClick(View v) {
                if (check) {
                    textView.setMaxLines(2);
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                } else {
                    textView.setMaxLines(Integer.MAX_VALUE);
                    textView.setEllipsize(null);
                }
                check = !check;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Log.d("location", String.valueOf(location));
        if (location != null && mMap != null) {
            Log.d("location", String.valueOf(location));
            mMap.addMarker(new MarkerOptions().position(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}