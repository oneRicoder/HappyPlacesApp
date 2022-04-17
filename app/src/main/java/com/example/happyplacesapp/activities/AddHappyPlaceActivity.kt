package com.example.happyplacesapp.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplacesapp.R
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplacesapp.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class AddHappyPlaceActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, View.OnClickListener {
    var binding: ActivityAddHappyPlaceBinding? = null

    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var mHappyPlaceDetail: HappyPlaceModel? = null

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

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)){
            mHappyPlaceDetail = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL) as HappyPlaceModel?
        }
        if (mHappyPlaceDetail != null){
            supportActionBar?.title = "Edit Happy Place"

            binding?.etTitle?.setText(mHappyPlaceDetail?.title)
            binding?.etDescription?.setText(mHappyPlaceDetail?.description)
            binding?.etDate?.setText(mHappyPlaceDetail?.date)
            binding?.etLocation?.setText(mHappyPlaceDetail?.location)
            mLatitude = mHappyPlaceDetail!!.latitude
            mLongitude = mHappyPlaceDetail!!.longitude

            saveImageToInternalStorage = Uri.parse(mHappyPlaceDetail!!.image)
            binding?.ivPlaceImage?.setImageURI(saveImageToInternalStorage)
            binding?.btnSave?.text = "Update"
        }


        pickDate()

        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
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
                        0 -> choosePhotoFromGallery()
                        1 -> {
                            //Toast.makeText(this,"coming soon",Toast.LENGTH_SHORT).show()
                            picFromCamera()
                        }
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save -> {
                when{
                    binding?.etTitle?.text.isNullOrEmpty() -> {
                        Toast.makeText(this,"Please Enter the Title",Toast.LENGTH_SHORT).show()
                    }
                    binding?.etDescription?.text.isNullOrEmpty() -> {
                        Toast.makeText(this,"Please Enter the Description",Toast.LENGTH_SHORT).show()
                    }
                    binding?.etDate?.text.isNullOrEmpty() -> {
                        Toast.makeText(this,"Please Enter the Date",Toast.LENGTH_SHORT).show()
                    }
                    binding?.etLocation?.text.isNullOrEmpty() -> {
                        Toast.makeText(this,"Please Enter the Location",Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this,"Please Select an Image",Toast.LENGTH_SHORT).show()
                    }else -> {
                        val happyPlaceModel = HappyPlaceModel(
                            if(mHappyPlaceDetail == null) 0 else mHappyPlaceDetail!!.id,
                            binding?.etTitle?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding?.etDescription?.text.toString(),
                            binding?.etDate?.text.toString(),
                            binding?.etLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                    if (mHappyPlaceDetail == null){
                        val dbHandler = DatabaseHandler(this)
                        val addHappyPlaceResult = dbHandler.addHappyPlace(happyPlaceModel)
                        if (addHappyPlaceResult > 0){
                            Toast.makeText(this,"Data Inserted Successfully!",Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }else{
                        val dbHandler = DatabaseHandler(this)
                        val updateHappyPlaceResult = dbHandler.updateHappyPlace(happyPlaceModel)
                        if (updateHappyPlaceResult > 0){
                            Toast.makeText(this,"Data Updated Successfully!",Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }

                    }
                }
            }
        }
    }

//    private fun picFromCamera(){
//        Toast.makeText(this@AddHappyPlaceActivity,"chal to raha h",Toast.LENGTH_SHORT).show()
//        Dexter.withContext(this)
//            .withPermission(Manifest.permission.CAMERA)
//            .withListener(object : PermissionListener {
//                override fun onPermissionGranted(response: PermissionGrantedResponse) {
//                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(cameraIntent, CAMERA)
//                }
//
//                override fun onPermissionDenied(response: PermissionDeniedResponse) {
//                    Toast.makeText(this@AddHappyPlaceActivity,"Camera Permission not allowed",Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    permission: PermissionRequest,
//                    token: PermissionToken
//                ) { /* ... */
//                }
//            }).onSameThread().check()
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY){
                if (data!=null){
                    val contentUri = data.data
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentUri)

                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved image: ","Path :: $saveImageToInternalStorage ")

                        binding?.ivPlaceImage?.setImageBitmap(selectedImageBitmap)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (requestCode == CAMERA){
                var pic = data?.getParcelableExtra<Bitmap>("data") as Bitmap

                saveImageToInternalStorage = saveImageToInternalStorage(pic)
                Log.e("Saved image: ","Path :: $saveImageToInternalStorage ")

                binding?.ivPlaceImage?.setImageBitmap(pic)
            }
        }
    }

    private fun picFromCamera(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport )
            {
                if (report.areAllPermissionsGranted()){
                    Toast.makeText(this@AddHappyPlaceActivity,"Camera permission granted",Toast.LENGTH_SHORT).show()

                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA)


                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest> , token: PermissionToken )
            {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun choosePhotoFromGallery(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport )
            {
                if (report.areAllPermissionsGranted()){
                    Toast.makeText(this@AddHappyPlaceActivity,"R/W permissions granted",Toast.LENGTH_SHORT).show()

                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)


                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest> , token: PermissionToken )
            {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }
    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It looks like are you have turned off the permission required for this feature")
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

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "Happy Images"
    }
}