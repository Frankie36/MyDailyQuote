package com.mystique.acme.rest;

import com.mystique.acme.model.FortuneResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("/fortune")
    Call<FortuneResponse> getData();
}
