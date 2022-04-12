package com.example.happyplacesapp.activities

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplacesapp.R
import com.example.happyplacesapp.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplacesapp.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    var binding: ActivityHappyPlaceDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var happyPlaceDetailModel: HappyPlaceModel? = null
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)){
            happyPlaceDetailModel = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL) as HappyPlaceModel?
        }
        if (happyPlaceDetailModel != null){
            setSupportActionBar(binding?.toolbarHappyPlaceDetail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            binding?.toolbarHappyPlaceDetail?.setNavigationOnClickListener {
                onBackPressed()
            }
            supportActionBar!!.title = happyPlaceDetailModel.title

            binding?.ivPlaceImage?.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            binding?.tvDescription?.text = happyPlaceDetailModel.description
            binding?.tvLocation?.text = happyPlaceDetailModel.location
        }
    }
}