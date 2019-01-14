package com.bestoffers.elnaqeltask.connection;

import com.bestoffers.elnaqeltask.models.Movie;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by SaraKhater on 1/12/19.
 */

public interface ApiInterface {

//    @GET("?t={searchKeyword}&apikey=123456")
//    Call<Movie> getMovies(@Path("searchKeyword") String searchKeyword);


    @GET("/")
    Call<Movie> getMovies(@Query("t") String searchKeyword ,@Query("apikey") String apiKey );
}
