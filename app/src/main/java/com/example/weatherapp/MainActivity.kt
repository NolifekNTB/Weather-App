package com.example.weatherapp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.api.RetrofitInstance
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
    //Units (default metric)
    private var units = "metric"
    //Map city to latitude and longtitude
    val cityCoordinates = HashMap<String, Pair<Double, Double>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        //Spinner
        spinnerHandle()

        //Change Celcius to Fahrenheit (switch)
        binding.sCelFar?.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked) {
                units="imperial"
            } else {
                units="metric"
            }
        }

    }

    private fun spinnerHandle()     {
        //City use in adapter
        val cities = arrayOf("Current", "Warsaw", "New York", "Sydney")

        cityCoordinates["Current"] = Pair(0.0,0.0)
        cityCoordinates["Warsaw"] = Pair(52.237049, 21.017532)
        cityCoordinates["New York"] = Pair(40.730610, -73.935242)
        cityCoordinates["Sydney"] = Pair(-33.8698439,  151.2082848)

        //Assign xml spinner to variable
        val spinner: Spinner = findViewById(R.id.sCity)
        //Map cities (text) to xml layout
        //simple_spinner_item actual view
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        //Set look when user expand list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //When user choose something
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCity = cities[position]
                latitude = cityCoordinates[selectedCity]!!.first
                longitude = cityCoordinates[selectedCity]!!.second

                performRetrofitRequest()
            }

            //When  nothing is choosed
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }
    }
    private fun fetchLocation() {
            //last known location
            val task = fusedLocationProviderClient.lastLocation

            //Permissions
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                //Request for permission if we don't have them
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            }

            task.addOnSuccessListener {
                //If it isn't null, operation is successful
                if (it != null) {
                    latitude = it.latitude
                    longitude = it.longitude

                    //Update spinner current location
                    cityCoordinates["Current"] = Pair(latitude, longitude)
                    performRetrofitRequest()
                }
            }
        }
    private fun performRetrofitRequest() {
        lifecycleScope.launch {
            val response = try {
                RetrofitInstance.myApi.getApi(latitude, longitude, units)
            } catch (e: IOException) {
                Log.d(TAG, "internet connection issue")
                return@launch
            } catch (e: HttpException) {
                Log.d(TAG, "unexcepted response")
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                val temperature = data.main.temp.toInt()
                val wind = data.wind.speed
                val sunrise = data.sys.sunrise
                val unixTime = Instant.ofEpochSecond(sunrise.toLong()).atZone(ZoneId.of("Europe/Warsaw")).toLocalTime()

                if (binding.sCelFar!!.isChecked) {
                    binding.TVcelsius.text = "$temperature°F"
                    binding.tvWind.text = "$wind miles/h"
                    binding.tvSunrise.text = unixTime.toString()
                } else {
                    binding.TVcelsius.text = "$temperature°C"
                    binding.tvWind.text = "$wind meters/s"
                    binding.tvSunrise.text = unixTime.toString()
                }

            } else {
                Log.d(TAG, "Reponse not successful")
            }
        }
    }
}














