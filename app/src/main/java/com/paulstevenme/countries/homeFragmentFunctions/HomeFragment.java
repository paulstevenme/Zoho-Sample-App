package com.paulstevenme.countries.homeFragmentFunctions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.paulstevenme.countries.APIClient;
import com.paulstevenme.countries.APIResponse;
import com.paulstevenme.countries.DatabaseClient;
import com.paulstevenme.countries.R;
import com.paulstevenme.countries.database.entity.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private View view;
    private ViewGroup placeHolder;
    LinearLayout home_details_loading_layout, network_error_layout;
    RelativeLayout home_data_items;
    TextView  network_error_text;
    Button btnRetry;
    RecyclerView rv_country_list;
    public static final String MY_PREFS_NAME = "CountryOfflineStore";
    SharedPreferences countryOfflineStoreSP;
    SharedPreferences.Editor countryOfflineStoreSPEditor;
    TextInputEditText fh_country_search_et;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        placeHolder = (ViewGroup) view;

        home_details_loading_layout = view.findViewById(R.id.home_details_loading_layout);
        network_error_layout = view.findViewById(R.id.network_error);
        home_data_items = view.findViewById(R.id.home_data_items);
        network_error_text = view.findViewById(R.id.network_error_text);
        fh_country_search_et = view.findViewById(R.id.fh_country_search_et);

        countryOfflineStoreSP = this.getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        countryOfflineStoreSPEditor = countryOfflineStoreSP.edit();
        getHomeFragmentItems(inflater, container);

        return placeHolder;
    }

    private void getHomeFragmentItems(LayoutInflater inflater, ViewGroup container) {

        Boolean network_check = haveNetworkConnection(view);
        network_error_layout = view.findViewById(R.id.network_error);
        btnRetry = view.findViewById(R.id.btn_retry);
        rv_country_list = view.findViewById(R.id.rv_country_list);

        if(network_check){
            boolean DBFlag= countryOfflineStoreSP.getBoolean("DBFlag", false);
            Log.e("DBFlag", String.valueOf(DBFlag));

            if(DBFlag){
                getAllCountryNamesAndFlags();
            }
            else{
                getDataFromURL();
            }
        }
        else{
            home_details_loading_layout.setVisibility(View.GONE);
            network_error_layout.setVisibility(View.VISIBLE);
        }

    }

    private void getDataFromURL() {
        Call<List<APIResponse>> call = APIClient.getUserService().getAllCountryDetails();

        call.enqueue(new Callback<List<APIResponse>>() {
            @Override
            public void onResponse(Call<List<APIResponse>> call, Response<List<APIResponse>> response) {
                if(response.isSuccessful()){
                    Log.e("success",  new Gson().toJson(response.body()));
                    String response_data = new Gson().toJson(response.body());
                    JSONArray response_data_array_json = null;
                    try {
                        response_data_array_json = new JSONArray(response_data);
                        for (int i = 0; i < response_data_array_json.length(); i++) {
                            JSONObject country_array = response_data_array_json.getJSONObject(i);

                            String name = country_array.getString("name");
                            JSONArray callingCodesList  = (JSONArray) country_array.get("callingCodes");
                            String callingCodes = (String) callingCodesList.get(0);
                            String capital = country_array.getString("capital");
                            String currencies = "";
                            try{
                                JSONArray currenciesList  = (JSONArray) country_array.get("currencies");
                                JSONObject currenciesListJson = (JSONObject) currenciesList.get(0);
                                currencies  = currenciesListJson.getString("code");
                            }
                            catch (Exception e){
                                currencies = "";
                            }

                            String flag = country_array.getString("flag");

                            String region = country_array.getString("region");
                            int population = country_array.getInt("population");
                            Note newNote = new Note();
                            newNote.setName(name);
                            newNote.setCallingCodes(callingCodes);
                            newNote.setCapital(capital);
                            newNote.setCurrencies(currencies);
                            newNote.setFlag(flag);
                            newNote.setRegion(region);
                            newNote.setPopulation(population);
                            addItemtoDB(newNote);



                        }
                        getAllCountryNamesAndFlags();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



//
                }
                else
                    Log.e("unSuccess", new Gson().toJson(response.errorBody()));
            }

            @Override
            public void onFailure(Call<List<APIResponse>> call, Throwable t) {
                Log.e("failure", t.getLocalizedMessage());
//                progressBarVisiblefun(false);
                Toast.makeText(getActivity().getApplicationContext(), "Server Down!!! Please Retry after some time",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getAllCountryNamesAndFlags() {

        class GetAllCountryNamesAndFlags extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return DatabaseClient
                        .getInstance(getContext())
                        .getNoteDatabase()
                        .noteDao()
                        .getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> tasks) {
                super.onPostExecute(tasks);
                countryOfflineStoreSPEditor.putBoolean("DBFlag", true).apply();
                home_details_loading_layout.setVisibility(View.GONE);
                home_data_items.setVisibility(View.VISIBLE);
                Log.e("tasks",tasks.toString());
                HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(getActivity(), tasks, HomeFragment.this);
                rv_country_list.setAdapter(adapter);
                fh_country_search_et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,             int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        adapter.getFilter().filter(s.toString());
                        filter(s.toString(),tasks, adapter);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


            }
        }

        GetAllCountryNamesAndFlags gNF = new GetAllCountryNamesAndFlags();
        gNF.execute();
    }

    private void filter(String text, List<Note> tasks, HomeRecyclerAdapter adapter) {
        List<Note> temp = new ArrayList();

        for(Note d: tasks){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    private void addItemtoDB(Note newNote) {
        class AddTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getActivity().getApplicationContext()).getNoteDatabase()
                            .noteDao()
                            .insert(newNote);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


            }
        }
        AddTask dt = new AddTask();
        dt.execute();
    }

    private Boolean haveNetworkConnection(View v) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
