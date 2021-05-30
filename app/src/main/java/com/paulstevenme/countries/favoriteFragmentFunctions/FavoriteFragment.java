package com.paulstevenme.countries.favoriteFragmentFunctions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.paulstevenme.countries.DatabaseClient;
import com.paulstevenme.countries.R;
import com.paulstevenme.countries.database.entity.Note;
import com.paulstevenme.countries.homeFragmentFunctions.HomeRecyclerAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteFragment extends Fragment {
    LinearLayout fav_no_favorites_linear_layout, fav_data_linear_layout;
    RecyclerView fav_rv_country_list;
    public static final String MY_PREFS_NAME = "CountryOfflineStore";
    SharedPreferences countryOfflineStoreSP;
    String fav_country_list_str;
    List<String> fav_country_list = new ArrayList();

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        ViewGroup placeHolder = (ViewGroup) view;

        fav_no_favorites_linear_layout = view.findViewById(R.id.fav_no_favorites_linear_layout);
        fav_data_linear_layout = view.findViewById(R.id.fav_data_linear_layout);
        fav_rv_country_list = view.findViewById(R.id.fav_rv_country_list);


        countryOfflineStoreSP = this.getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
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
            getFavFragmentItems();
        }
        else{
            fav_no_favorites_linear_layout.setVisibility(View.VISIBLE);
            fav_data_linear_layout.setVisibility(View.GONE);
        }


        return placeHolder;
    }

    private void getFavFragmentItems() {
        getFavCountryNamesAndFlags();
    }

    private void getFavCountryNamesAndFlags() {

        class GetFavCountryNamesAndFlags extends AsyncTask<Void, Void, List<Note>> {
            

            @Override
            protected List<Note> doInBackground(Void... voids) {
                List<Note> fav_notes = new ArrayList<Note>();
                for (int i=0;i<fav_country_list.size();i++){
                    String s = fav_country_list.get(i);
                    Note note  = DatabaseClient.getInstance(getContext()).getNoteDatabase().noteDao().getNoteByTitle(s);
                    fav_notes.add(note);
                }
                return fav_notes;

            }

            @Override
            protected void onPostExecute(List<Note> tasks) {
                super.onPostExecute(tasks);
                fav_no_favorites_linear_layout.setVisibility(View.GONE);
                fav_data_linear_layout.setVisibility(View.VISIBLE);
                Log.e("tasks",tasks.toString());
                HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(getActivity(), tasks);
                fav_rv_country_list.setAdapter(adapter);

            }
        }

        GetFavCountryNamesAndFlags gFNF = new GetFavCountryNamesAndFlags();
        gFNF.execute();
    }
    public void updateContent(){
        countryOfflineStoreSP = this.getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        fav_country_list_str = countryOfflineStoreSP.getString("fav_country_list_str","");
        if(!fav_country_list_str.equals("")){
            fav_country_list = new ArrayList<>(Arrays.asList(fav_country_list_str.split(",")));
//            Remove Spaces in List of Strings
            for (int i = 0; i < fav_country_list.size(); i++) {
                fav_country_list.set(i, fav_country_list.get(i).trim());
            }
            Log.e("fav_country_list", String.valueOf(fav_country_list));
            getFavFragmentItems();
        }
        else{
            fav_no_favorites_linear_layout.setVisibility(View.VISIBLE);
            fav_data_linear_layout.setVisibility(View.GONE);
        }
    }
}
