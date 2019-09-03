package com.mystique.mdq.rest;

import com.mystique.mdq.model.FortuneResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("/fortune")
    Call<FortuneResponse> getData();
}
