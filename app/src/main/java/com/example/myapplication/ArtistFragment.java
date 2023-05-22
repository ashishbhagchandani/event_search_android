package com.example.myapplication;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ArtistFragment extends Fragment {
    private StringRequest mStringRequest;
    private ArrayList<String> dps = new ArrayList<>();
    private ArrayList<String> artistNames = new ArrayList<>();
    private ArrayList<String> followers = new ArrayList<>();
    private ArrayList<String> popularity = new ArrayList<>();
    private ArrayList<String> checkout = new ArrayList<>();
    private ArrayList<String> album1 = new ArrayList<>();
    private ArrayList<String> album2 = new ArrayList<>();
    private ArrayList<String> album3 = new ArrayList<>();
    private RecyclerView recyclerView;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_artist, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewartist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    protected void displayReceivedData(ArrayList message)
    {   String artiststring  = "";
        for(int i= 0; i < message.size(); ++i){
            try {
                if(i==0){
                    artiststring += URLEncoder.encode((String) message.get(i), StandardCharsets.UTF_8.toString());
                }else{
                    artiststring += "," + URLEncoder.encode((String) message.get(i), StandardCharsets.UTF_8.toString());
                }
            } catch (UnsupportedEncodingException e) {
            }
        }
        Log.d("received", String.valueOf(artiststring));
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://hw8-ashish.uc.r.appspot.com/spotifysearchartist?artistlist=" + artiststring;
        Log.d("artist url", String.valueOf(url));
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    CardView noartistscard = view.findViewById(R.id.noartistscard);
                    noartistscard.setVisibility(View.GONE);
                    JSONArray res = new JSONArray(response);

//                    Log.d("artist res", String.valueOf(res));
                    for(int i = 0; i < res.length(); ++i){
                        JSONObject res1 = res.getJSONObject(i);
                        JSONObject responsejsonObject = res1.getJSONObject("artist");

                        JSONArray dparray = responsejsonObject.getJSONArray("images");

                        dps.add(dparray.getJSONObject(0).getString("url"));
                        artistNames.add(responsejsonObject.getString("name"));

                        JSONObject followersObject = responsejsonObject.getJSONObject("followers");
                        int num = Integer.parseInt(followersObject.getString("total"));
                        String resfollower = "";
                        if (num >= 1000000) {
                            resfollower = String.format("%dM", num / 1000000);
                        } else if (num >= 1000) {
                            resfollower = String.format("%dK", num / 1000);
                        } else {
                            resfollower = String.valueOf(num);
                        }
//                            String resfollower = String.format("%.0f%s", formattedNum, suffix);

                        followers.add(resfollower);

                        popularity.add(responsejsonObject.getString("popularity"));
                        checkout.add(responsejsonObject.getString("href"));


                        JSONArray albumArray = res1.getJSONObject("album").getJSONArray("items");
                        JSONObject albumObject1 = albumArray.getJSONObject(0);
                        JSONArray images1 = albumObject1.getJSONArray("images");

                        JSONObject albumObject2 = albumArray.getJSONObject(1);
                        JSONArray images2 = albumObject2.getJSONArray("images");

                        JSONObject albumObject3 = albumArray.getJSONObject(2);
                        JSONArray images3 = albumObject3.getJSONArray("images");

                        album1.add(images1.getJSONObject(0).getString("url"));
                        album2.add(images2.getJSONObject(0).getString("url"));
                        album3.add(images3.getJSONObject(0).getString("url"));

                    }

                    ArtistAdapter artistAdapter = new ArtistAdapter(dps, artistNames, followers, popularity, checkout, album1, album2, album3, ArtistFragment.this);
                    recyclerView.setAdapter(artistAdapter);
                } catch (JSONException e) {
                    CardView noartistscard = getView().findViewById(R.id.noartistscard);
                    noartistscard.setVisibility(View.VISIBLE);
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