package com.example.myapplication;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EventDetails extends AppCompatActivity implements DetailsFragment.SendMessage {

    private TabLayout tabLayout;
    private static ViewPager viewPager;
    private String eventId;
    private String eventName;
    private String eventVenue;
    private String eventDate;
    private String eventTime;
    private String eventGenre;
    private String eventImage;
    private boolean isfav;
    private static Context context;
    private TABAdapter tabAdapter = new TABAdapter( getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent intent = getIntent();
        context = getApplicationContext();
        eventName = intent.getStringExtra("eventName");
        eventVenue = intent.getStringExtra("eventVenue");
        eventDate = intent.getStringExtra("eventDate");
        eventTime = intent.getStringExtra("eventTime");
        eventGenre = intent.getStringExtra("eventGenre");
        eventImage = intent.getStringExtra("eventImage");
        eventId = intent.getStringExtra("eventId");
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(Html.fromHtml("<font color='#57A333'>"+eventName+"</font>"));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle(Html.fromHtml("<font color='#57A333'>"+eventName+"</font>")); // set the title text color
        ImageView facebook = findViewById(R.id.facebook);
        Context context = getApplicationContext();
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsFragment.callSocialfacebook(context);
            }
        });
        ImageView twitter = findViewById(R.id.twitter);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsFragment.callSocialtwitter(context);
            }
        });
        TextView title = findViewById(R.id.eventTitle);
        title.setText(eventName);
//        title.setTextColor(Color.parseColor("#57A333"));
        title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        title.setSingleLine(true);
        title.setSelected(true);
        checkfav();

//        Bundle bundle = new Bundle();
//        bundle.putString("eventId", intent.getStringExtra("eventId"));
//        DetailsFragment fragment = new DetailsFragment();
//        fragment.setArguments(bundle);

//
//        actionBar.setCustomView(title);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.show();
        Drawable backArrow = getResources().getDrawable(R.drawable.green_back_btn);
        actionBar.setHomeAsUpIndicator(backArrow);
        actionBar.setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.tabLayout1);
        viewPager = findViewById(R.id.viewpager1);

        tabLayout.setupWithViewPager(viewPager);

//        TABAdapter tabAdapter = new TABAdapter( getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tabAdapter.addFragment(new DetailsFragment(), "Details");
        tabAdapter.addFragment(new ArtistFragment(), "Artist(s)");
        tabAdapter.addFragment(new VenueFragment(), "Venue");
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#57A333"));
        Bundle bundle = new Bundle();
        bundle.putString("eventId", intent.getStringExtra("eventId"));
        bundle.putString("eventName", intent.getStringExtra("eventName"));
        DetailsFragment fragment = (DetailsFragment) tabAdapter.getItem(0);
        fragment.setArguments(bundle);
        viewPager.setAdapter(tabAdapter);

        TabLayout.Tab tab1 = tabLayout.getTabAt(0);

        Drawable icon = ContextCompat.getDrawable(this, R.drawable.info_icon);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.search_green), PorterDuff.Mode.SRC_IN);
        tab1.setIcon(icon);

        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
        Drawable icon2 = ContextCompat.getDrawable(this, R.drawable.artist_icon);
        icon2.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        tab2.setIcon(icon2);

        TabLayout.Tab tab3 = tabLayout.getTabAt(2);
        Drawable icon3 = ContextCompat.getDrawable(this, R.drawable.venue_icon);
        icon3.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        tab3.setIcon(icon3);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                switch(position){
                    case 0:
                        tab1.getIcon().setColorFilter(getResources().getColor(R.color.search_green), PorterDuff.Mode.SRC_IN);
                        tab2.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        tab3.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        break;
                    case 1:
                        tab2.getIcon().setColorFilter(getResources().getColor(R.color.search_green), PorterDuff.Mode.SRC_IN);
                        tab1.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        tab3.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        break;
                    case 2:
                        tab3.getIcon().setColorFilter(getResources().getColor(R.color.search_green), PorterDuff.Mode.SRC_IN);
                        tab1.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        tab2.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public void callSocial(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void onFavClick(View view) {
        ImageView favheart = findViewById(R.id.favTool);
        Log.d("clicked",eventId);
        createSnackbarremove(eventName);
        if(isfav){
            SharedPreferences sharedPref = getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
            String storedJson = sharedPref.getString("userList", null);
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

            String getid = eventId;
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
                    userList.remove(i);
                    checkfav = true;
                    break;
                }
            }

            if (!checkfav) {
                userList.add(new EventShared(eventName, eventVenue, eventDate, eventTime, eventGenre, eventId, eventImage));
            }

            String json = gson.toJson(userList);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userList", json);
            editor.apply();

            Log.d("applied", json);

            if (checkfav) {
                favheart.setImageResource(R.drawable.heart_outline);
            } else {
                favheart.setImageResource(R.drawable.heart_filled);
            }
            String storedJsoncheck = sharedPref.getString("userList", null);

            try {
                Log.d("applied1", String.valueOf(new JSONArray(storedJsoncheck)));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void checkfav(){
        ImageView favheart = findViewById(R.id.favTool);
        SharedPreferences sharedPref = getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
        String storedJson = sharedPref.getString("userList", null);

        JSONArray favstorage = null;
        if (storedJson != null){
            try {
                favstorage = new JSONArray(storedJson);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < favstorage.length(); i++) {
                String getfavid = null;
                String getid = eventId;
                try {
                    getfavid = favstorage.getJSONObject(i).getString("eventId");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (getfavid.equals(getid)){
                    isfav = true;
                    favheart.setImageResource(R.drawable.heart_filled);
                    favheart.setTag(R.drawable.heart_filled);
                    break;
                }else{
                    isfav = false;
                    favheart.setImageResource(R.drawable.heart_outline);
                    favheart.setTag(R.drawable.heart_outline);
                }
            }
        }else{
            isfav = false;
            favheart.setImageResource(R.drawable.heart_outline);
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendData(ArrayList message, String venue) {
        ArtistFragment fragment = (ArtistFragment) tabAdapter.getItem(1);
        fragment.displayReceivedData(message);

        VenueFragment venueFragment = (VenueFragment) tabAdapter.getItem(2);
    }

    public static void createSnackbarremove(String eventName){
        String str = eventName + " removed from favorites";
        Snackbar snackbar = Snackbar.make(viewPager, str, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.BLACK);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar));
        snackbar.show();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
}