package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import java.time.ZoneId

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.myApi.getApi()
            } catch (e: IOException) {
                Log.d(TAG, "internet connection issue")
                return@launch
            } catch (e: HttpException) {
                Log.d(TAG, "unexcepted response")
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                Log.d(TAG, "Dane: $data")
                val temperature = data.main.temp.toInt()
                val wind = data.wind.speed
                val sunrise = data.sys.sunrise
                val unixTime = Instant.ofEpochSecond(sunrise.toLong()).atZone(ZoneId.of("Europe/Warsaw")).toLocalTime()

                binding.TVcelsius.text = "$temperature°C"
                binding.tvWind.text = "$wind km/h"
                binding.tvSunrise.text = unixTime.toString()
            } else {
                Log.d(TAG, "Reponse not successful")
            }
        }
    }
}