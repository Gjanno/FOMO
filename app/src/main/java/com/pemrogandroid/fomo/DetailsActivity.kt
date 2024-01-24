package com.pemrogandroid.fomo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Retrieve details from Intent extras
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val imageUrl = intent.getStringExtra("imageUrl")
        val locationName = intent.getStringExtra("locationName")
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val dateAndTime = intent.getStringExtra("dateAndTime")
        val userName = intent.getStringExtra("userName")




    }
}