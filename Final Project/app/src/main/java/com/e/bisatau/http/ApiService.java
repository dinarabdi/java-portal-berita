package com.e.bisatau.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Fetch all notes
    @GET("wp/v2/posts")
    Observable<JsonArray> getNews(@Query("per_page") String page, @Query("order") String order);

    @GET("wp/v2/posts")
    Observable<JsonArray> getNewsLoadmore(@Query("per_page") String page, @Query("order") String order, @Query("page") Integer number);

    @GET("wp/v2/posts/{id}")
    Observable<JsonObject> getNewsDetail(@Path("id") String id);

    @GET("wp/v2/posts")
    Observable<JsonArray> getSearch(@Query("search") String search);
}
