package com.paulstevenme.countries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.paulstevenme.countries.database.entity.Note;

import java.util.List;

public class CountryDetailsActivity extends AppCompatActivity {

    String country_name = "";
    TextView cd_tv_country_name;
    ImageView cd_flag_iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_details);
        country_name = getIntent().getStringExtra("country_name");

        Toolbar country_detail_toolbar = findViewById(R.id.country_detail_toolbar);
        cd_tv_country_name = findViewById(R.id.cd_tv_country_name);
        cd_flag_iv = findViewById(R.id.cd_flag_iv);
        cd_tv_country_name.setText(country_name);

        country_detail_toolbar.setNavigationOnClickListener(view -> onBackPressed());
        getCountryDetailsFromDB(country_name);

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