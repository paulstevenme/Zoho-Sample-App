package com.paulstevenme.countries;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit getRetrofit(){

        String api_base_url = "https://restcountries.eu/rest/v2/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(api_base_url).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit;
    }

    public static UserService getUserService(){
        return getRetrofit().create(UserService.class);
    }
}
