package com.paulstevenme.countries;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface UserService {

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("all")
    Call<List<APIResponse>> getAllCountryDetails();

}
