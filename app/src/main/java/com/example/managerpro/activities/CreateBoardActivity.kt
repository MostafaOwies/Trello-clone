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
import com.example.managerpro.R
import com.example.managerpro.databinding.ActivityCreateBoardBinding
import com.example.managerpro.firebase.FireStoreClass
import com.example.managerpro.models.Board
import com.example.managerpro.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateBoardActivity : BaseActivity() {
    private var binding:ActivityCreateBoardBinding?=null
    private lateinit var mUserName:String
    private var mBoardImageURL:String=""
    private var storage= Firebase.storage
    private var selectedImageUri: Uri?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.createBoardToolBar)
        if (supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.createBoardToolBar?.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(Constants.NAME)){
            mUserName=intent.getStringExtra(Constants.NAME)!!
        }

        binding?.boardImage?.setOnClickListener{
            permissionForUserImage()
        }

        binding?.createBoardBTN?.setOnClickListener {
            if (selectedImageUri!=null){
                updateImageToStorage()
            }
            else{
                showProgressDialog(resources.getString(R.string.pleas_wait))
                createBoard()
            }
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this@CreateBoardActivity,"Board created successfully",Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
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

    private fun createBoard(){
        val assignedUserList :ArrayList<String> = ArrayList()
        assignedUserList.add(getCurrentUserID())

        val boardString :String=binding?.boardNameET?.text.toString()
        if(boardString.isEmpty()){
            binding?.boardNameETLayout?.error=getString(R.string.baordnamerror)
        }else{
            lifecycleScope.launch {
                val board=Board(boardString,
                    mBoardImageURL,
                    mUserName,
                    assignedUserList)
                FireStoreClass().createBoard(this@CreateBoardActivity, board = board)
            }

        }
        hideProgressDialog()
    }

    private fun updateImageToStorage(){
            showProgressDialog(resources.getString(R.string.pleas_wait))
            val storageRef = storage.reference.child(
                "BOARD_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(this,selectedImageUri)
            )
            lifecycleScope.launch(Dispatchers.IO) {
                storageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener { snapshot ->
                        snapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                            mBoardImageURL = uri.toString()
                            createBoard()
                        }
                    }.addOnFailureListener{ E->
                        Toast.makeText(this@CreateBoardActivity,E.message,Toast.LENGTH_SHORT).show()
                        hideProgressDialog()
                    }
            }
        }


    private val boardImageStorage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            binding?.boardImage?.setImageURI(it)
            selectedImageUri=it
        }
    }

    private val storageResultLauncher : ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                boardImageStorage.launch("image/*")
            }else{
                Toast.makeText(this,"Permission Not granted",Toast.LENGTH_SHORT).show()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

}