package com.paulstevenme.countries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.paulstevenme.countries.database.entity.Note;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountryDetailsActivity extends AppCompatActivity {

    String country_name = "";
    TextView cd_tv_country_name;
    ImageView cd_flag_iv;
    ExtendedFloatingActionButton cd_fab_btn;
    SharedPreferences countryOfflineStoreSP;
    SharedPreferences.Editor countryOfflineStoreSPEditor;
    public static final String MY_PREFS_NAME = "CountryOfflineStore";
    String fav_country_list_str;
    List<String> fav_country_list = new ArrayList();
    Boolean favFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_details);



        country_name = getIntent().getStringExtra("country_name");

        countryOfflineStoreSP = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        countryOfflineStoreSPEditor = countryOfflineStoreSP.edit();
        fav_country_list_str = countryOfflineStoreSP.getString("fav_country_list_str","");
        System.out.println("fav_country_list_str "+fav_country_list_str);
        Log.e("CountryDetailsActivity", "cda");
        Log.e("fav_country_list_str",fav_country_list_str);
        if(!fav_country_list_str.equals("")){
            fav_country_list = new ArrayList<>(Arrays.asList(fav_country_list_str.split(",")));
//            Remove Spaces in List of Strings
            for (int i = 0; i < fav_country_list.size(); i++) {
                fav_country_list.set(i, fav_country_list.get(i).trim());
            }
            Log.e("fav_country_list", String.valueOf(fav_country_list));
        }
        Log.e("fav_country_list_check", String.valueOf(fav_country_list.contains(country_name)));



        Toolbar country_detail_toolbar = findViewById(R.id.country_detail_toolbar);
        cd_tv_country_name = findViewById(R.id.cd_tv_country_name);
        cd_flag_iv = findViewById(R.id.cd_flag_iv);
        cd_fab_btn = findViewById(R.id.cd_fab_btn);
        cd_tv_country_name.setText(country_name);

        country_detail_toolbar.setNavigationOnClickListener(view -> onBackPressed());
        getCountryDetailsFromDB(country_name);

//        Fab Button Color Set
        if(fav_country_list.contains(country_name)){
            favFlag=true;
            fabBtnColorSet(favFlag);
        }
        else{
            fabBtnColorSet(favFlag);
        }


        cd_fab_btn.setOnClickListener(view -> {
            String regex = "\\[|\\]";
            if(favFlag){

                fabBtnColorSet(false);
                fav_country_list.remove(country_name);
                countryOfflineStoreSPEditor.putString("fav_country_list_str", fav_country_list.toString().replaceAll(regex, "")).apply();
            }
            else{

                fabBtnColorSet(true);
                fav_country_list.add(country_name);
                countryOfflineStoreSPEditor.putString("fav_country_list_str", fav_country_list.toString().replaceAll(regex, "")).apply();
            }
        });

    }

    private void fabBtnColorSet(Boolean favFlag) {
        if(favFlag){
            cd_fab_btn.setText("Remove from Favorites");
            cd_fab_btn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
            cd_fab_btn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            cd_fab_btn.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
        }
        else{
            cd_fab_btn.setText("Add to Favorites");
            cd_fab_btn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.special_bg_color));
            cd_fab_btn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            cd_fab_btn.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.white)));

        }
    }

    private void getCountryDetailsFromDB(String country_name) {
        class GetData extends AsyncTask<Void, Void, Note> {

            @Override
            protected Note doInBackground(Void... voids) {
                return DatabaseClient
                        .getInstance(getBaseContext())
                        .getNoteDatabase()
                        .noteDao()
                        .getNoteByTitle(country_name);
            }

            @Override
            protected void onPostExecute(Note note) {
                super.onPostExecute(note);
                Log.e("note",note.getFlag());
                GlideToVectorYou
                        .init()
                        .with(getBaseContext())
                        .withListener(new GlideToVectorYouListener() {
                            @Override
                            public void onLoadFailed() {
                                Toast.makeText(getApplicationContext(), "Load failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResourceReady() {
//                        Toast.makeText(mCtx.getApplicationContext(), "Image ready", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPlaceHolder(R.mipmap.ic_launcher, R.mipmap.ic_launcher)
                        .load(Uri.parse(note.getFlag()), cd_flag_iv);
            }
        }
        GetData getData = new GetData();
        getData.execute();
    }
}