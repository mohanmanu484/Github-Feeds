package com.mohan.location.ydm.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/** 
 * Created by mohan on 2/10/16. 
 */ 
 
public interface NetworkCall { 

    @GET("/users/{user}/events")
    Call<String> getUserEvents(@Path("user") String user);

    @GET
    Call<String> getRepoEvents(@Url String url);

    @GET
    public Call<String> repoDetails(@Url String url);
} 