package com.example.weatherapp

import com.example.weatherapp.third.third
import retrofit2.Response
import retrofit2.http.GET

//Use interface to define function, but not start yet
interface Api {
    //Use suspend to use in coroutines, to do operation beyond main UI thread
    //@GET (annotations), to add additional information about code, (information abou end point to interface)
    @GET("data/2.5/weather?lat=51&lon=19&appid=14efda8337fb20cda4a865dbee112ae8&units=metric")
    suspend fun getApi(): Response<third>
}