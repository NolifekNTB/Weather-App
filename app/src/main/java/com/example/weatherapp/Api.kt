package com.example.weatherapp

import com.example.weatherapp.third.third
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("data/2.5/weather?lat=51&lon=19&appid=14efda8337fb20cda4a865dbee112ae8&units=metric")
    suspend fun getApi(): Response<third>

    //without key works (response not successful)
}