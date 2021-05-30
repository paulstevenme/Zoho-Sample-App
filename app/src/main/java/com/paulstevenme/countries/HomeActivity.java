package com.paulstevenme.countries;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.paulstevenme.countries.favoriteFragmentFunctions.FavoriteFragment;
import com.paulstevenme.countries.homeFragmentFunctions.HomeFragment;

import java.util.List;
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Resume","called");
        refreshSecFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences countryOfflineStoreSP = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        DBFlag = countryOfflineStoreSP.getBoolean("DBFlag",false);


        sideDrawerLayout  = findViewById(R.id.sideDrawerLayout);
        home_toolbar = findViewById(R.id.home_toolbar);
        navigationView = findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,sideDrawerLayout,home_toolbar, R.string.navigation_open,R.string.navigation_close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        sideDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(item -> {
            final int buttonView = item.getItemId();
            // Handle navigation view item clicks here.
            if (buttonView == R.id.m_about) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("About");
                builder.setMessage("Countries App is a handy App to Know about all Country Details.");
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
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
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
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();

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
}