package com.paulstevenme.countries.homeFragmentFunctions;

import android.app.AlertDialog;
import android.content.Context;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.paulstevenme.countries.R;
import com.paulstevenme.countries.database.entity.Note;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
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
                        Toast.makeText(mCtx.getApplicationContext(), "Image ready", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPlaceHolder(R.mipmap.ic_launcher, R.mipmap.ic_launcher)
                .load(Uri.parse(flag_url), holder.flag_iv);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
    class NoteViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int uid;
        ImageView flag_iv;
        TextView country_name;

        public NoteViewholder(View itemView) {
            super(itemView);
            flag_iv = itemView.findViewById(R.id.flag_iv);
            country_name = itemView.findViewById(R.id.country_name);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
