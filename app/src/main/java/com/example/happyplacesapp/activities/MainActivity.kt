package com.example.happyplacesapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplacesapp.adapters.HappyPlacesAdapter
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.databinding.ActivityMainBinding
import com.example.happyplacesapp.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent,ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlacesListFromLocalDb()
    }

    private fun setUpHappyPlacesRecyclerView(happyPlacesList:ArrayList<HappyPlaceModel>){
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.reHappyPlacesList?.layoutManager = layoutManager
        binding?.reHappyPlacesList?.setHasFixedSize(true)

        val adapter = HappyPlacesAdapter(this,happyPlacesList)
        binding?.reHappyPlacesList?.adapter = adapter

        adapter.setOnItemClickListener(object : HappyPlacesAdapter.onItemClickListener{
            override fun onItemClick(position: Int,model: HappyPlaceModel) {
                val newPosition = position+1;
                Toast.makeText(this@MainActivity,"You Clicked On Item no. $newPosition",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity,HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAIL,model)
                startActivity(intent)
            }
        })
    }

    private fun getHappyPlacesListFromLocalDb(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList = dbHandler.getHappyPlacesList()

        if(getHappyPlaceList.size > 0){
            binding?.reHappyPlacesList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
            setUpHappyPlacesRecyclerView(getHappyPlaceList)
        }else{
            binding?.reHappyPlacesList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDb()
            }else{
                Log.e("Activity","cancelled or back pressed")
            }
        }
    }



    companion object {
        val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        val EXTRA_PLACE_DETAIL = "extra_place_details"
    }
}