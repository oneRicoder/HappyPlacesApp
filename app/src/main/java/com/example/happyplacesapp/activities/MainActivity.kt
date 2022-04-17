package com.example.happyplacesapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplacesapp.adapters.HappyPlacesAdapter
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.databinding.ActivityMainBinding
import com.example.happyplacesapp.models.HappyPlaceModel
import com.example.happyplacesapp.utils.SwipeToEditCallback
import com.happyplaces.utils.SwipeToDeleteCallback

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
        binding?.reHappyPlacesList?.setItemViewCacheSize(20)

        val adapter = HappyPlacesAdapter(this,happyPlacesList)
        adapter.setHasStableIds(true)
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

        val editSwipeHandler = object : SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.reHappyPlacesList?.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(this@MainActivity,viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding?.reHappyPlacesList)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.reHappyPlacesList?.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getHappyPlacesListFromLocalDb()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding?.reHappyPlacesList)
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