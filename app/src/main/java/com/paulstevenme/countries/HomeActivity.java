package com.paulstevenme.countries;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.github.twocoffeesoneteam.glidetovectoryou.BuildConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.paulstevenme.countries.favoriteFragmentFunctions.FavoriteFragment;
import com.paulstevenme.countries.homeFragmentFunctions.HomeFragment;
import com.paulstevenme.countries.utils.Helpers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class HomeActivity extends AppCompatActivity {

    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new FavoriteFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    DrawerLayout sideDrawerLayout;
    NavigationView navigationView;
    Toolbar home_toolbar;
    public static final String MY_PREFS_NAME = "CountryOfflineStore";

    Boolean DBFlag = false;
    SharedPreferences countryOfflineStoreSP;
    SharedPreferences.Editor countryOfflineStoreSPEditor;

    String pmAndPresidentURL = "https://en.m.wikipedia.org/wiki/List_of_current_heads_of_state_and_government";
    ArrayList<String> pmNames = new ArrayList<>();
    ArrayList<String> presidentNames = new ArrayList<>();
    ArrayList<String> countryNames = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        refreshSecFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        Initializing Shared Preferences
        countryOfflineStoreSP = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        DBFlag = countryOfflineStoreSP.getBoolean("DBFlag",false);
        countryOfflineStoreSPEditor = countryOfflineStoreSP.edit();


        sideDrawerLayout  = findViewById(R.id.sideDrawerLayout);
        home_toolbar = findViewById(R.id.home_toolbar);
        navigationView = findViewById(R.id.navigationView);
//        For Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,sideDrawerLayout,home_toolbar, R.string.navigation_open,R.string.navigation_close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        sideDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        Drawer Menu Item Click Functions
        navigationView.setNavigationItemSelectedListener(item -> {
            final int buttonView = item.getItemId();
            // Handle navigation view item clicks here.
            if (buttonView == R.id.m_about) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("About");
                builder.setMessage("★ Know the Details of the Country\n" +
                        "★ Search the Country Name and Get Instant Results\n" +
                        "★ Like your Country and make it available in Favorites Tab.\n" +
                        "★ Give a Try.\n" +
                        "★ Enjoy.");
                builder.setPositiveButton("OK", (dialogInterface, i) -> {

                });

                AlertDialog alert = builder.create();
                alert.show();
                Button oKButton = alert.getButton(alert.BUTTON_POSITIVE);
                oKButton.setBackgroundColor(ContextCompat.getColor(HomeActivity.this,R.color.special_bg_color));
                oKButton.setTextColor(ContextCompat.getColor(HomeActivity.this,R.color.white));
                oKButton.setOnClickListener(view -> alert.dismiss());
            }

            else if(buttonView == R.id.m_share) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Countries");
                    String shareMessage= "\nLet me recommend you this best Countries application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.LIBRARY_PACKAGE_NAME +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
            else if(buttonView == R.id.m_version) {
                String versionCode_Name = "";
                AlertDialog.Builder versionBuilder = new AlertDialog.Builder(HomeActivity.this);
                versionBuilder.setTitle("App Version");
                try {
                    PackageInfo pInfo = Objects.requireNonNull(getPackageManager().getPackageInfo(getPackageName(), 0));
                    String verCode = pInfo.versionName;
                    versionCode_Name = "Countries App Version: "+ verCode;

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                versionBuilder.setMessage(versionCode_Name);
                versionBuilder.setPositiveButton("OK", (dialogInterface, i) -> {

                });

                AlertDialog alert = versionBuilder.create();
                alert.show();
                Button oKButton = alert.getButton(alert.BUTTON_POSITIVE);
                oKButton.setBackgroundColor(ContextCompat.getColor(HomeActivity.this,R.color.special_bg_color));
                oKButton.setTextColor(ContextCompat.getColor(HomeActivity.this,R.color.white));
                oKButton.setOnClickListener(view -> alert.dismiss());

            }

            //close navigation drawer
            sideDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

//        Bottom Navigation Bar
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();

        Helpers helpers = new Helpers(this);
        try {

            Boolean internet_check = helpers.isInternetConnected();
            if(internet_check){

                try{
                    countryOfflineStoreSPEditor.putString("pmNames","").apply();
                    countryOfflineStoreSPEditor.putString("presidentNames","").apply();
                    countryOfflineStoreSPEditor.putString("countryNames","").apply();
                    new PMPresidentDataFetcher().execute();
                }
                catch (Exception e){
                    Log.e("Exception", String.valueOf(e));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private void refreshSecFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FavoriteFragment favoriteFragment= (FavoriteFragment) fragmentManager.findFragmentByTag("2");
        favoriteFragment.updateContent();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        final int buttonView = item.getItemId();
        // Handle navigation view item clicks here.
        if (buttonView == R.id.navigation_home) {
            fm.beginTransaction().hide(active).show(fragment1).commit();
            active = fragment1;
            return true;
        }
        else if(buttonView == R.id.navigation_favorite) {
            fm.beginTransaction().hide(active).show(fragment2).commit();
            active = fragment2;
            return true;
        }
        return false;
    };

    class PMPresidentDataFetcher extends AsyncTask<Void, ArrayList,ArrayList> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList doInBackground(Void... voids) {

            ArrayList<String> arrayList = new ArrayList<>();

            try {

                Document document;
                document = Jsoup.connect(pmAndPresidentURL).get();
                Element table = document.select("table").get(1); //select the first table.
                Elements rows = table.select("tr");
                for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                    Element row = rows.get(i);
                    Elements cols;
                    Elements cols_coun;
                    cols_coun = row.select("th");
                    cols = row.select("td");

                    try{
                        if(cols.get(1).text().contains("Prime Minister")){
                            String pm_name = cols.get(1).text().replace("Prime Minister – ", "").trim();
                            pmNames.add(pm_name);
                        }
                        else{
                            pmNames.add("-");
                        }
                    }
                    catch(Exception e){
                        pmNames.add("-");
                    }
                    try{
                        if(cols.get(0).text().contains("President")){
                            String president_name = cols.get(0).text().replace("President – ", "").replace("[δ]","").replace("[μ]","").trim();
                            presidentNames.add(president_name);
                        }
                        else{
                            presidentNames.add("-");
                        }
                    }
                    catch(Exception e){
                        presidentNames.add("-");
                    }
                    countryNames.add(cols_coun.text());
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                System.out.println("error on fetching");
                System.out.println(e);
            }
            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList ) {
            Gson gson = new Gson();
            String pmNamesSet = gson.toJson(pmNames);
            String presidentNamesSet = gson.toJson(presidentNames);
            String countryNamesSet = gson.toJson(countryNames);

            countryOfflineStoreSPEditor.putString("pmNames", pmNamesSet).apply();
            countryOfflineStoreSPEditor.putString("presidentNames", presidentNamesSet).apply();
            countryOfflineStoreSPEditor.putString("countryNames", countryNamesSet).apply();
            super.onPostExecute(arrayList);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}