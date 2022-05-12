package com.example.managerpro.activities

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.managerpro.R
import com.example.managerpro.databinding.ActivityUserProfileBinding
import com.example.managerpro.firebase.FireStoreClass
import com.example.managerpro.models.User
import com.example.managerpro.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch



class UserProfile : BaseActivity() {
    private var binding :ActivityUserProfileBinding?=null
    private var storage=Firebase.storage
    private var mProfileImageURL: String = ""
    private var selectedImageUri: Uri?=null
    private lateinit var userDetails :User

    private val storageResultLauncher :ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                storageContent.launch("image/*")
            }else{
                Toast.makeText(this,"Permission Not granted",Toast.LENGTH_SHORT).show()
            }
        }

    private  val storageContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null)
        {
            binding?.userImageProfile?.setImageURI(it)
            selectedImageUri=it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.userImageProfile?.setOnClickListener{
            permissionForUserImage()
        }

        binding?.updateUserProfileBTN?.setOnClickListener {
            showProgressDialog(resources.getString(R.string.pleas_wait))
            if (selectedImageUri!=null){
                test()
            }
            else{

                updateProfileData()
            }
        }

        setSupportActionBar(binding?.userProfileToolbar)
        if (supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.userProfileToolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
        lifecycleScope.launch {FireStoreClass().loadUserData(this@UserProfile)  }

    }

    private fun permissionForUserImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            shouldShowRequestPermissionRationale
                (Manifest.permission.READ_EXTERNAL_STORAGE)){
            showErrorSnackBar("Permission required to access photos")
        }
        else{
            storageResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

        }
    }

    fun getUserDataInViews(user: User) {
        userDetails=user
        Glide
            .with(this@UserProfile)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding?.userImageProfile!!)
        binding?.usernameProfile?.setText(user.name)
        binding?.emailProfile?.setText(user.email)
        binding?.mobileProfile?.setText(user.mobile.toString())
    }

    private fun test(){
        if (selectedImageUri!=null) {
            val storageRef = storage.reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(
                    this,
                    selectedImageUri
                )
            )
            storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { snapshot ->
                    snapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        mProfileImageURL = uri.toString()
                        updateProfileData()
                    }
                    //Toast.makeText(this@UserProfile,"Photo Updated successfully",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { E ->
                    Toast.makeText(this@UserProfile, E.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
        }else{
            profileNoteUpdated()
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(this@UserProfile,"profile updated successfully",Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun profileNoteUpdated(){
        hideProgressDialog()
        Toast.makeText(this@UserProfile,"No updates detected",Toast.LENGTH_SHORT).show()
    }

    private fun updateProfileData(){
        val userHashMap = HashMap<String,Any>()
         var changesMade = false
        if (mProfileImageURL.isNotEmpty() && mProfileImageURL!=userDetails.image){
            userHashMap[Constants.IMAGE]=mProfileImageURL
            changesMade=true
        }
        if (binding?.usernameProfile?.text.toString()!=userDetails.name){
            userHashMap[Constants.NAME]=binding?.usernameProfile?.text.toString()
            changesMade=true
        }
        if (binding?.mobileProfile?.text.toString()!=userDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=binding?.mobileProfile?.text.toString().toLong()
            changesMade=true
        }
        if (changesMade){
            lifecycleScope.launch { FireStoreClass().updateUserProfile(this@UserProfile,userHashMap) }
        }else{
            profileNoteUpdated()
        }
        hideProgressDialog()
    }





    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}


    /*
    private fun showImageChooser(){
      Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
      startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
  }
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
          super.onActivityResult(requestCode, resultCode, data)
          if (resultCode==Activity.RESULT_OK &&
              requestCode== PICK_IMAGE_REQUEST_CODE &&
                  data!!.data!=null){
              selectedImageUri=data.data

              try {
                  Glide
                  .with(this@UserProfile)
                      .load(selectedImageUri)
                      .centerCrop()
                      .placeholder(R.drawable.ic_user_place_holder)
                      .into(binding?.userImageProfile!!)
              }catch (e:IOException){
                      e.printStackTrace()
              }
          }
      }
    private fun uploadUserImage(){

    if (selectedImageUri!=null){

        showProgressDialog(resources.getString(R.string.pleas_wait))
            val storageRef=storage.reference.child("USER_IMAGE"+System.currentTimeMillis()+"."+getFileExtension(selectedImageUri))
        lifecycleScope.launch(Dispatchers.IO){
            storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener { snapshot ->
                snapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    mProfileImageURL = uri.toString()
                    hideProgressDialog()
                }
                Toast.makeText(this@UserProfile,"Success",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{ E->
                Toast.makeText(this@UserProfile,E.message,Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }


}*/
