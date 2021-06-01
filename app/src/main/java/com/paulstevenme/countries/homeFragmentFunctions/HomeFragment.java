package com.paulstevenme.countries.homeFragmentFunctions;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
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
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.paulstevenme.countries.APIClient;
import com.paulstevenme.countries.APIResponse;
import com.paulstevenme.countries.DatabaseClient;
import com.paulstevenme.countries.R;
import com.paulstevenme.countries.database.entity.Note;
import com.paulstevenme.countries.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.ContextCompat.getSystemService;

public class HomeFragment extends Fragment {

    private View view;
    LinearLayout home_details_loading_layout, network_error_layout;
    RelativeLayout home_data_items;
    TextView  network_error_text;
    Button btnRetry;
    RecyclerView rv_country_list;
    public static final String MY_PREFS_NAME = "CountryOfflineStore";
    SharedPreferences countryOfflineStoreSP;
    SharedPreferences.Editor countryOfflineStoreSPEditor;
    TextInputEditText fh_country_search_et;
    Boolean network_check = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ViewGroup placeHolder = (ViewGroup) view;

        home_details_loading_layout = view.findViewById(R.id.home_details_loading_layout);
        network_error_layout = view.findViewById(R.id.network_error);
        home_data_items = view.findViewById(R.id.home_data_items);
        network_error_text = view.findViewById(R.id.network_error_text);
        fh_country_search_et = view.findViewById(R.id.fh_country_search_et);
        btnRetry = view.findViewById(R.id.btn_retry);
        rv_country_list = view.findViewById(R.id.rv_country_list);

        countryOfflineStoreSP = this.getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        countryOfflineStoreSPEditor = countryOfflineStoreSP.edit();
        try {
            getHomeFragmentItems();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return placeHolder;
    }

    private void getHomeFragmentItems() throws IOException, InterruptedException {

        Helpers helpers = new Helpers(getContext());
        network_check = helpers.isInternetConnected();


        startProcess(network_check);

        btnRetry.setOnClickListener(view -> {

            try {
                network_check = helpers.isInternetConnected();
                if(network_check){
                    clearAppData();
                    network_error_layout.setVisibility(View.GONE);
                    home_details_loading_layout.setVisibility(View.VISIBLE);
                    startProcess(network_check);
                }

            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

        });
    }

    private void clearAppData() {
        try {
            Runtime.getRuntime().exec("pm clear " + getActivity().getApplicationContext().getPackageName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startProcess(Boolean network_check) {
        boolean DBFlag= countryOfflineStoreSP.getBoolean("DBFlag", false);
        if(DBFlag){
            getAllCountryNamesAndFlags();
        }
        else{
            countryOfflineStoreSPEditor.putString("fav_country_list_str", "").apply();
            if(network_check){
                getDataFromURL();
            }
            else {
                home_details_loading_layout.setVisibility(View.GONE);
                network_error_layout.setVisibility(View.VISIBLE);
            }

        }
    }

    private void getDataFromURL() {
        Call<List<APIResponse>> call = APIClient.getUserService().getAllCountryDetails();

        call.enqueue(new Callback<List<APIResponse>>() {
            @Override
            public void onResponse(Call<List<APIResponse>> call, Response<List<APIResponse>> response) {
                if(response.isSuccessful()){
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
                            String area;
                            try{
                                int area_int = country_array.getInt("area");
                                area = String.valueOf(area_int);

                            }
                            catch (Exception e){
                                area = "-";
                            }

                            String currencies;
                            try{
                                JSONArray currenciesList  = (JSONArray) country_array.get("currencies");
                                JSONObject currenciesListJson = (JSONObject) currenciesList.get(0);
                                currencies  = currenciesListJson.getString("code");
                            }
                            catch (Exception e){
                                currencies = "-";
                            }

                            String flag = country_array.getString("flag");

                            String region = country_array.getString("region");
                            int population = country_array.getInt("population");

                            JSONArray topLevelDomainList  = (JSONArray) country_array.get("topLevelDomain");
                            String topLevelDomain = (String) topLevelDomainList.get(0);

                            String languages = "";
                            try{
                                JSONArray languagesList  = (JSONArray) country_array.get("languages");

                                for(int j=0;j<languagesList.length();j++){
                                    JSONObject languagesListJson = (JSONObject) languagesList.get(j);
                                    if(j>0){
                                        languages = languages +" , "+ languagesListJson.getString("name");
                                    }
                                    else{
                                        languages = languagesListJson.getString("name");
                                    }

                                }

                            }
                            catch (Exception e){
                                languages = "-";
                            }

                            Note newNote = new Note();
                            newNote.setName(name);
                            newNote.setCallingCodes(callingCodes);
                            newNote.setCapital(capital);
                            newNote.setCurrencies(currencies);
                            newNote.setFlag(flag);
                            newNote.setRegion(region);
                            newNote.setPopulation(population);
                            newNote.setArea(area+ " km²");
                            newNote.setTopLevelDomain(topLevelDomain);
                            newNote.setLanguages(languages);
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
                Toast.makeText(getActivity().getApplicationContext(), "Server Down!!! Please Retry after some time",Toast.LENGTH_LONG).show();
                home_details_loading_layout.setVisibility(View.GONE);
                network_error_layout.setVisibility(View.VISIBLE);
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
                HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(getActivity(), tasks);
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
}
