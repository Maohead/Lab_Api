package com.example.lab_api

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import android.Manifest
import android.annotation.SuppressLint

class MainActivity : AppCompatActivity() {
    var api_key ="6ece6d897dfca2387c554075c618bbea"

    private lateinit var btVar1: Button
    private lateinit var textView: TextView

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textView = findViewById(R.id.tv1)
        btVar1 = findViewById(R.id.button_weather)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        btVar1.setOnClickListener {
            checkForPermission()
        }

    }

    private fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            obtainLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                obtainLocation()
            } else {
                Toast.makeText(this, "Разрешение отклонено", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtainLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val weatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&units=metric&appid=${api_key}"
                getTemp(weatherUrl)
            } else {
                Toast.makeText(this, "Не удалось получить местоположение", Toast.LENGTH_SHORT).show()
            }
        }

            .addOnFailureListener { exception ->
                Toast.makeText(this, "Location Persmission not granted", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getTemp(url: String) {
        val queue = Volley.newRequestQueue(this)
        val stringReq = StringRequest (
            Request.Method.GET, url, { response ->
                val obj = JSONObject(response)
                val main: JSONObject = obj.getJSONObject("main")
                val temperature = main.getString("temp")
                println(temperature)
                val humidity = main.getString(("humidity"))
                println(humidity)
                val city = obj.getString("name")
                println(city)
                textView.text="${temperature} Градусов по цельсию в ${city}. Влажность ${humidity}%."
                System.out.println(obj.toString())
            },
            {textView.text = "Ошибка"}
        )
        queue.add(stringReq)
    }
}