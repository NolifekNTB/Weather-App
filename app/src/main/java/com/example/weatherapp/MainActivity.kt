package com.example.weatherapp

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import java.time.ZoneId

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var TAG = "MainActivity"
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fetchLocation()
    }

    private fun fetchLocation() {
       val task = fusedLocationProviderClient.lastLocation

        //Permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
        }
        task.addOnSuccessListener {
            if (it != null) {
                latitude = it.latitude
                longitude = it.longitude
                Log.d("CIPA", "$latitude, lon $longitude")

                performRetrofitRequest()
            }
        }
    }

    private fun performRetrofitRequest() {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.myApi.getApi(latitude, longitude)
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














