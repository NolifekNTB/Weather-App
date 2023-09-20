package com.example.weatherapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Use object to create ONLY one instance (one network path), less resources
object RetrofitInstance {
    //lazy - start variable (myapi) when we run it, not when object (RetrofitInstance) was created
    val myApi: Api by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create()) //Use to mapping data from json to kotlin
            .build()
            .create(Api::class.java) //Add function from api (interface) to object
    }
}