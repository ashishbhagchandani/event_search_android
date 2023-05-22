package com.example.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.support.annotation.NonNull;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class DetailsFragment extends Fragment {
    private String eventId;
    private StringRequest mStringRequest;
    SendMessage SM;
    private static String eventName;
    private static String geturl;
    private static Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        Bundle bundle = getArguments();
        context = getContext();
        eventId = bundle.getString("eventId");
        eventName = bundle.getString("eventName");

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://hw8-ashish.uc.r.appspot.com/ticketmastereventdetail?id=" + eventId;
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("event details", response);
                try {
                    JSONObject responsejsonObject = new JSONObject(response);
                    JSONObject embeddedjsonObject = responsejsonObject.getJSONObject("_embedded");
                    JSONArray venuesArray = embeddedjsonObject.getJSONArray("venues");
                    String venue = venuesArray.getJSONObject(0).getString("name");
                    TextView venuename = view.findViewById(R.id.venuet);
                    venuename.setText(venue);
                    venuename.setSelected(true);
                    ImageView seatmap = view.findViewById(R.id.imageView3);

                    JSONObject seatmapObject = responsejsonObject.getJSONObject("seatmap");
                    String staticUrl = seatmapObject.getString("staticUrl");
                    Picasso.get().load(staticUrl).into(seatmap);

                    JSONObject datesjsonObject = responsejsonObject.getJSONObject("dates");
                    JSONObject startjsonObject = datesjsonObject.getJSONObject("start");
                    String date = startjsonObject.getString("localDate");
                    SimpleDateFormat inputDateF = new SimpleDateFormat("yyyy-MM-DD");
                    try{
                        Date inputDate = inputDateF.parse(date);

                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                        String outputDateStr = outputDateFormat.format(inputDate);
                        TextView getdate = view.findViewById(R.id.datet);
                        getdate.setText(outputDateStr);
                        System.out.println(outputDateStr);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }



                    JSONObject statusjsonObject = datesjsonObject.getJSONObject("status");
                    String status = statusjsonObject.getString("code");
                    CardView statusView = view.findViewById(R.id.status);
                    TextView ticketstatust = view.findViewById(R.id.ticketstatust);
//                    Log.d("sttaudss", status);
                    if (status.equals("onsale")) {
//                        Log.d("sttaus", status);
                        statusView.setCardBackgroundColor(Color.rgb(59, 133, 36));
                        ticketstatust.setText("On Sale");
                    } else if (status.equals("offsale")) {
                        statusView.setCardBackgroundColor(Color.rgb(210, 46, 32));
                        ticketstatust.setText("Off Sale");
                    } else if (status.equals("rescheduled")) {
                        statusView.setCardBackgroundColor(Color.rgb(222, 164, 57));
                        ticketstatust.setText("Rescheduled");
                    } else if (status.equals("Postponed")) {
                        statusView.setCardBackgroundColor(Color.rgb(222, 164, 57));
                        ticketstatust.setText("postponed");
                    } else if (status.equals("cancelled")) {
                        statusView.setCardBackgroundColor(Color.rgb(0, 0, 0));
                        ticketstatust.setText("Cancelled");
                    }

                    if (startjsonObject.has("localTime")) {
                        SimpleDateFormat inputDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
                        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("h:mm a", Locale.US);
                        Date datetime = null;
                        String formattedTime = null;
                        try {
                            datetime = inputDateFormat.parse(startjsonObject.getString("localTime"));
                            formattedTime = outputTimeFormat.format(datetime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        TextView time = view.findViewById(R.id.timet);
                        time.setText(String.valueOf(formattedTime));
                    } else {
                        TextView time = view.findViewById(R.id.time);
                        time.setVisibility((View.GONE));
                        TextView timet = view.findViewById(R.id.timet);
                        timet.setVisibility((View.GONE));
                    }

                    geturl = responsejsonObject.getString("url");
                    showurl(view, geturl);
                    TextView ticketsat = view.findViewById(R.id.ticketsat);
                    ticketsat.setText(geturl);
                    ticketsat.setSelected(true);

                    TextView priceranget = view.findViewById(R.id.priceranget);
                    if (responsejsonObject.has("priceRanges")) {
                        JSONArray getpriceRanges = responsejsonObject.getJSONArray("priceRanges");
                        if (getpriceRanges.getJSONObject(0).has("min") && getpriceRanges.getJSONObject(0).has("max")) {
                            String getpricerange = getpriceRanges.getJSONObject(0).getInt("min") + " - " + getpriceRanges.getJSONObject(0).getInt("max") + " (USD)";
                            priceranget.setText(getpricerange);
                        } else if (!getpriceRanges.getJSONObject(0).has("min") && getpriceRanges.getJSONObject(0).has("max")) {
                            String getpricerange = getpriceRanges.getJSONObject(0).getInt("max") + " - " + getpriceRanges.getJSONObject(0).getInt("max") + " (USD)";
                            priceranget.setText(getpricerange);
                        } else if (getpriceRanges.getJSONObject(0).has("min") && !getpriceRanges.getJSONObject(0).has("max")) {
                            String getpricerange = getpriceRanges.getJSONObject(0).getInt("min") + " - " + getpriceRanges.getJSONObject(0).getInt("min") + " (USD)";
                            priceranget.setText(getpricerange);
                        }
                    } else {
                        priceranget.setVisibility((View.GONE));
                        TextView pricerange = view.findViewById(R.id.pricerange);
                        pricerange.setVisibility((View.GONE));
                    }

                    JSONArray getattractions = embeddedjsonObject.getJSONArray("attractions");

                    ArrayList artistlist = new ArrayList<>();
                    String artistNames = "";
                    for (int i = 0; i < getattractions.length(); ++i){
                        JSONObject atristObj = getattractions.getJSONObject(i);
                        artistlist.add(atristObj.getString("name"));
                        if(i == 0) {
                            artistNames += atristObj.getString("name");
                        }else{
                            artistNames += " | " + atristObj.getString("name");
                        }
                    }

//                    ImageView facebook = view1.findViewById(R.id.facebook);
//                    facebook.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String url1 = "https://www.facebook.com/sharer/sharer.php?u="+url+"&amp;src=sdkpreparse";
//                            callSocial(url1);
//                        }
//                    });

                    SM.sendData(artistlist, venue);
                    VenueModel venueModel = new ViewModelProvider(requireActivity()).get(VenueModel.class);
                    venueModel.setData(venue);


                    TextView artistteamst = view.findViewById(R.id.artistteamst);
                    artistteamst.setText(artistNames);
                    artistteamst.setSelected(true);
                    JSONArray classificationjsonObject = responsejsonObject.getJSONArray("classifications");
                    JSONObject genrejsonObject = classificationjsonObject.getJSONObject(0);
                    JSONObject genredjsonObject = genrejsonObject.getJSONObject("genre");
                    String genrename = genredjsonObject.getString("name");

                    JSONObject sgenrejsonObject = genrejsonObject.getJSONObject("subGenre");
                    String sgenrename = sgenrejsonObject.getString("name");

                    JSONObject segmentjsonObject = genrejsonObject.getJSONObject("segment");
                    String segmentname = segmentjsonObject.getString("name");

                    String getgenre = genrename + " | " + sgenrename + " | " + segmentname;

                    TextView genretextview = view.findViewById(R.id.genrest);
                    genretextview.setText(getgenre);
                    genretextview.setSelected(true);
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
        return view;
    }

    interface SendMessage {
        void sendData(ArrayList message, String venue);
    }
//    https://www.digitalocean.com/community/tutorials/android-passing-data-between-fragments
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

    public static void callSocialfacebook(Context context){
        String url1 = "https://www.facebook.com/sharer/sharer.php?u="+geturl+"&amp;src=sdkpreparse";
        Uri uri = Uri.parse(url1);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void callSocialtwitter(Context context){
        String url1 = "https://twitter.com/intent/tweet?text="+ "Check"+ eventName + "on Ticketmaster." + geturl + "&hashtags=hashtag1,hashtag2";
        Uri uri = Uri.parse(url1);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void showurl(View view, String url){
        TextView ticketsat = view.findViewById(R.id.ticketsat);
        ticketsat.setClickable(true);
        ticketsat.setAutoLinkMask(Linkify.WEB_URLS);
        ticketsat.setTextColor(getResources().getColor(R.color.search_green));
        ticketsat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}

