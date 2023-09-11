package com.example.weatherapp

import com.example.weatherapp.third.third
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//Use interface to define function, but not start yet
interface Api {
    //Use suspend to use in coroutines, to do operation beyond main UI thread
    //@GET (annotations), to add additional information about code, (information abou end point to interface)
    @GET("data/2.5/weather?appid=14efda8337fb20cda4a865dbee112ae8")
    suspend fun getApi(@Query("lat") lat: Double,
                       @Query("lon",) lon: Double,
                       @Query("units") unit: String): Response<third>

}