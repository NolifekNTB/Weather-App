package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpCookie

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            binding.progressBar.isVisible = true
            val response = try {
                RetrofitInstance.myApi.getApi()
            } catch (e: IOException) {
                Log.d(TAG, "internet connection issue")
                binding.progressBar.isVisible = false
                return@launch
            } catch (e: HttpException) {
                Log.d(TAG, "unexcepted response")
                binding.progressBar.isVisible = false
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                Log.d(TAG, "Dane: $data")
                val temperature = data.main.temp.toInt()
                binding.TVcelsius.text = "$temperature°C"
            } else {
                Log.d(TAG, "Reponse not successful")
            }
            binding.progressBar.isVisible = false

        }
    }

    override fun onResume() {
        super.onResume()
    }
}