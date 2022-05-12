package com.example.managerpro.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.managerpro.R
import com.example.managerpro.databinding.CustomDialogBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class BaseActivity : AppCompatActivity() {
    private var doublePressToExitBackOnce= false
    private lateinit var progressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
    fun showProgressDialog(text:String){
        progressDialog= Dialog(this)
        val dialogBinding= CustomDialogBinding.inflate(layoutInflater)
        progressDialog.setContentView(dialogBinding.root)
        progressDialog.setCanceledOnTouchOutside(false)
        dialogBinding.progressDialogTV.text=text
        progressDialog.show()
    }
    fun hideProgressDialog(){
        progressDialog.dismiss()
    }

    fun getCurrentUserID():String{
        //Get an instance of the FirebaseAuth Ovject with .getinstance
        //check if there is logged in users and then gets their UID
        return  Firebase.auth.currentUser!!.uid
    }

    fun doublePressToExit(){
        if (doublePressToExitBackOnce){
            super.onBackPressed()
        }
        this.doublePressToExitBackOnce=true
        Toast.makeText(this,"Click once more to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
            doublePressToExitBackOnce=false
        },3000)
    }

    fun showErrorSnackBar(message :String){
        Snackbar.make(findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG).show()

    }
}