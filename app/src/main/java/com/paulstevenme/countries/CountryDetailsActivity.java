package com.paulstevenme.countries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.utils.Utils;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paulstevenme.countries.database.entity.Note;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CountryDetailsActivity extends AppCompatActivity {

    String country_name = "";
    TextView cd_tv_country_name;
    ImageView cd_flag_iv;
    SharedPreferences countryOfflineStoreSP;
    SharedPreferences.Editor countryOfflineStoreSPEditor;
    public static final String MY_PREFS_NAME = "CountryOfflineStore";
    String fav_country_list_str, country_names_str,president_names_str, pm_names_str;
    List<String> fav_country_list = new ArrayList();
    List<String> country_names_list = new ArrayList();
    List<String> president_names_list = new ArrayList();
    List<String> pm_names_list = new ArrayList();
    Boolean favFlag = false;
    RecyclerView cd_rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_details);
        
        country_name = getIntent().getStringExtra("country_name");

        countryOfflineStoreSP = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        countryOfflineStoreSPEditor = countryOfflineStoreSP.edit();
        fav_country_list_str = countryOfflineStoreSP.getString("fav_country_list_str","");
        Gson gson = new Gson();
        String set1 = countryOfflineStoreSP.getString("countryNames", null);
        country_names_list =gson.fromJson(set1, new TypeToken<List<String>>(){}.getType());
        String set2 = countryOfflineStoreSP.getString("presidentNames", null);
        president_names_list =gson.fromJson(set2, new TypeToken<List<String>>(){}.getType());
        String set3 = countryOfflineStoreSP.getString("pmNames", null);
        pm_names_list =gson.fromJson(set3, new TypeToken<List<String>>(){}.getType());

        if(!fav_country_list_str.equals("")){
            fav_country_list = new ArrayList<>(Arrays.asList(fav_country_list_str.split(",")));
//            Remove Spaces in List of Strings
            for (int i = 0; i < fav_country_list.size(); i++) {
                fav_country_list.set(i, fav_country_list.get(i).trim());
            }
        }




        Toolbar country_detail_toolbar = findViewById(R.id.country_detail_toolbar);
        setSupportActionBar(country_detail_toolbar);
        cd_tv_country_name = findViewById(R.id.cd_tv_country_name);
        cd_flag_iv = findViewById(R.id.cd_flag_iv);

        cd_rv = findViewById(R.id.cd_rv);
        cd_tv_country_name.setText(country_name);

        country_detail_toolbar.setNavigationOnClickListener(view -> onBackPressed());
        getCountryDetailsFromDB(country_name);

//        Fab Button Color Set
        favFlag= fav_country_list.contains(country_name);

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
//                                Toast.makeText(getApplicationContext(), "Load failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResourceReady() {
//                        Toast.makeText(mCtx.getApplicationContext(), "Image ready", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPlaceHolder(R.mipmap.ic_launcher, R.mipmap.ic_launcher)
                        .load(Uri.parse(note.getFlag()), cd_flag_iv);

                String president_name ="", pm_name ="";
                try{
                    Log.e("index", String.valueOf(country_names_list.indexOf(note.getName())));
                    int pmPreindex = country_names_list.indexOf(note.getName());
                    president_name = president_names_list.get(pmPreindex);
                    pm_name = pm_names_list.get(pmPreindex);
                }
                catch (Exception e){
                    president_name="-";
                    pm_name="-";
                    Log.e("Excep", String.valueOf(e));
                }
                List<String>picList = new ArrayList<>(Arrays.asList("cd_ico_pre","cd_ico_pm","cd_ico_reg", "cd_ico_cap","cd_ico_pop","cd_ico_area","cd_ico_cur","cd_ico_cal"));
                String population_count = NumberFormat.getNumberInstance(Locale.getDefault()).format(note.getPopulation());
                List<String> detailsList = Arrays.asList(president_name,pm_name,note.getRegion(),note.getCapital(),population_count,note.getArea(),note.getCurrencies(),note.getCallingCodes());
                List<String> detailsHeadList  = Arrays.asList("President","Prime Minister","Region","Capital","Population","Area","Currency","Calling Code");
                ArrayList rv_list = new ArrayList<>();
                for (int i = 0; i < detailsList.size(); i++) {
                    rv_list.add(new CountryItem(getResources().getIdentifier(picList.get(i), "drawable", getPackageName()), detailsHeadList.get(i), detailsList.get(i)));
                }
                CountryRecyclerAdapter mAdapter = new CountryRecyclerAdapter(rv_list);
                cd_rv.setAdapter(mAdapter);
                cd_rv.setItemAnimator(new DefaultItemAnimator());



            }
        }
        GetData getData = new GetData();
        getData.execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.country_app_bar_menu, menu);
        Menu optionsMenu = menu;
        if(favFlag){
            menuIconColor(optionsMenu.getItem(0), Color.parseColor("#e1306c"));
        }
        return true;
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.like_btn) {
            String regex = "\\[|\\]";
            if(favFlag){
                menuIconColor(item, Color.parseColor("#ffffff"));
                favFlag =false;
                fav_country_list.remove(country_name);
            }
            else{
                menuIconColor(item, Color.parseColor("#e1306c"));
                favFlag=true;
                fav_country_list.add(country_name);
            }
            countryOfflineStoreSPEditor.putString("fav_country_list_str", fav_country_list.toString().replaceAll(regex, "")).apply();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void menuIconColor(MenuItem menuItem, int color) {
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }
}