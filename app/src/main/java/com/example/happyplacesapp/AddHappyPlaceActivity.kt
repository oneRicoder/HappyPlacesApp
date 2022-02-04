package com.example.happyplacesapp

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.audiofx.Equalizer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import com.example.happyplacesapp.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*


class AddHappyPlaceActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, View.OnClickListener {
    var binding: ActivityAddHappyPlaceBinding? = null
//     var cal = Calendar.getInstance();
//    lateinit var dateSetListner: DatePickerDialog.OnDateSetListener

    var day = 0;
    var month = 0;
    var year = 0;

    var savedDay = "";
    var savedMonth = "";
    var savedYear = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }
//        dateSetListner = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
//
//            cal.set(Calendar.YEAR,year)
//            cal.set(Calendar.MONTH,month)
//            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
//        }
        pickDate()

        binding?.tvAddImage?.setOnClickListener(this)
    }
    private fun getDateCalender(){
        val cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    private fun pickDate(){
        binding?.etDate?.setOnClickListener {
            getDateCalender()
            DatePickerDialog(this,this,year,month,day).show()
        }
    }


    // view , p1 = year, p2 = month, p3 = dayofmonth
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedYear = p1.toString()
        if(p2 +1 < 10){
            savedMonth = "0" + (p2+1).toString()
        }else{
            savedMonth = (p2+1).toString()
        }
//        savedMonth = (p2+1).toString()
        if(p3 < 10){
            savedDay = "0$p3"
        }else{
            savedDay = p3.toString()
        }


//        val myFormat = "dd.MM.yyyy"
//        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
//        binding?.etDate?.setText(sdf.format(savedDay+savedMonth+savedYear)).toString()


        binding?.etDate?.setText("$savedDay/$savedMonth/$savedYear")
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select From Gallery", "Take Picture")
                pictureDialog.setItems(pictureDialogItems){
                    _, which ->
                    when(which){
                        0 -> choosePhtoFromGallery()
                        1 -> Toast.makeText(this,"coming soon",Toast.LENGTH_SHORT).show()
                    }
                }
                pictureDialog.show()
            }
        }
    }
    private fun choosePhtoFromGallery(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport )
            {
                if (report.areAllPermissionsGranted()){
                    Toast.makeText(this@AddHappyPlaceActivity,"R/W permissions granted",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest> , token: PermissionToken )
            {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }
    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It looks like are you have turned off the permission required for this feaure")
            .setPositiveButton("Go to settings")
            {_,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}