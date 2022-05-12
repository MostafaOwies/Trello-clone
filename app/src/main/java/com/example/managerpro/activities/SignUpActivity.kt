package com.example.managerpro.activities


import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.managerpro.R
import com.example.managerpro.databinding.ActivitySignUpBinding
import com.example.managerpro.firebase.FireStoreClass
import com.example.managerpro.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity(private val ioDispatcher: CoroutineDispatcher =Dispatchers.IO ) : BaseActivity() {
private var binding:ActivitySignUpBinding?=null
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth

        setSupportActionBar(binding?.signUpToolbar)
        if (supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.signUpToolbar?.setNavigationOnClickListener {
                onBackPressed()
        }

        val name=binding?.signUpUsernameET
        val email=binding?.signUpEmailET
        val password=binding?.signUpPasswordET
        name?.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty() ){
                binding?.signUpUsernameTVLayout?.error=null
            }
        }
        email?.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty() ){
                binding?.signUpEmailETLayout?.error=null
            }
        }
        password?.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty() ){
                binding?.signUpPasswordETLayout?.error=null
            }
        }

        binding?.signupActivityBTN?.setOnClickListener {

            lifecycleScope.launch{
                registerUser()
            }

        }
    }
    fun userRegisteredSuccessfully() {
        Toast.makeText(
            this, "Registered successfully",
            Toast.LENGTH_SHORT
        ).show()
        auth.signOut()
        hideProgressDialog()
        finish()
    }
    private suspend fun registerUser(){
        val name=binding?.signUpUsernameET?.text
        val email=binding?.signUpEmailET?.text
        val password=binding?.signUpPasswordET?.text
        val stringName :String=name.toString().trim{it<=' '}
        val stringEmail :String=email.toString().trim{it<=' '}
        val stringPassword :String=password.toString().trim{it<=' '}
        when{
           stringName.isEmpty()->{
                binding?.signUpUsernameTVLayout?.error=getString(R.string.Username_error)
            }
            stringEmail.isEmpty()->{
                binding?.signUpEmailETLayout?.error=getString(R.string.Email_error)
            }
            stringPassword.isEmpty()->{
                binding?.signUpPasswordETLayout?.error=getString(R.string.Password_error)
            }else->{
            withContext(ioDispatcher){
                runOnUiThread { showProgressDialog(resources.getString(R.string.pleas_wait)) }
                auth
                    .createUserWithEmailAndPassword(stringEmail,stringPassword).addOnCompleteListener(this@SignUpActivity) { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val registeredEmail = firebaseUser.email!!
                            val user = User(id=firebaseUser.uid, name=stringName,email=registeredEmail)
                            lifecycleScope.launch {
                                FireStoreClass().registerUserToFireStore(this@SignUpActivity,user)
                            }
                        } else {
                            runOnUiThread {
                                showErrorSnackBar(task.exception!!.message.toString())
                                hideProgressDialog()
                            }
                        }
                    }
            }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}