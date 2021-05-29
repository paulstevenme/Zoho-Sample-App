package com.paulstevenme.countries.homeFragmentFunctions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.paulstevenme.countries.CountryDetailsActivity;
import com.paulstevenme.countries.R;
import com.paulstevenme.countries.database.entity.Note;
import java.util.List;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.NoteViewholder> {


    private Context mCtx;
    private List<Note> notes;
    private final HomeFragment homeFragment;
    public HomeRecyclerAdapter(Context mCtx, List<Note> notes, HomeFragment homeFragment) {
        System.out.println("Entered Here PRice");
        this.mCtx = mCtx;
        this.notes = notes;
        this.homeFragment = homeFragment;
    }

    @Override
    public NoteViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.rc_home_item, parent, false);
        return new NoteViewholder(view);
    }
    @Override
    public void onBindViewHolder(NoteViewholder holder, int position) {
        Note dbData = notes.get(position);
        holder.country_name.setText(dbData.getName());
        String flag_url = dbData.getFlag();

        GlideToVectorYou
                .init()
                .with(mCtx)
                .withListener(new GlideToVectorYouListener() {
                    @Override
                    public void onLoadFailed() {
                        Toast.makeText(mCtx.getApplicationContext(), "Load failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResourceReady() {
//                        Toast.makeText(mCtx.getApplicationContext(), "Image ready", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPlaceHolder(R.mipmap.ic_launcher, R.mipmap.ic_launcher)
                .load(Uri.parse(flag_url), holder.flag_iv);

        holder.home_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent countryDetailsIntent  = new Intent(mCtx, CountryDetailsActivity.class);
                countryDetailsIntent.putExtra("country_name",dbData.getName());
                mCtx.startActivity(countryDetailsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
    class NoteViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int uid;
        ImageView flag_iv;
        TextView country_name;
        CardView home_card_view;

        public NoteViewholder(View itemView) {
            super(itemView);
            flag_iv = itemView.findViewById(R.id.flag_iv);
            country_name = itemView.findViewById(R.id.country_name);
            home_card_view = itemView.findViewById(R.id.home_card_view);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
