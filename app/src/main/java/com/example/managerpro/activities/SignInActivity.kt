package com.example.managerpro.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.managerpro.R
import com.example.managerpro.databinding.ActivitySignInBinding
import com.example.managerpro.firebase.FireStoreClass
import com.example.managerpro.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInActivity(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    BaseActivity() {
    private var binding: ActivitySignInBinding? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = Firebase.auth


        val email = binding?.signInEmailET
        val password = binding?.signInPasswordET

        email?.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding?.signInEmailETLayout?.error = null
            }
        }
        password?.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding?.signInPasswordETLayout?.error = null
            }
        }

        binding?.signInActivityBTN?.setOnClickListener {
            lifecycleScope.launch {
                signInUser()
            }
        }

        setSupportActionBar(binding?.signInToolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.signInToolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun signInSuccessfully(user: User) {

        hideProgressDialog()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        Toast.makeText(this, "Signed In successfully", Toast.LENGTH_SHORT).show()
    }

    private suspend fun signInUser() {

        val email = binding?.signInEmailET?.text
        val password = binding?.signInPasswordET?.text
        val stringEmail: String = email.toString().trim { it <= ' ' }
        val stringPassword: String = password.toString().trim { it <= ' ' }

        when {
            stringEmail.isEmpty() -> {
                binding?.signInEmailETLayout?.error = getString(R.string.Email_error)
            }
            stringPassword.isEmpty() -> {
                binding?.signInPasswordETLayout?.error = getString(R.string.Password_error)
            }
            else -> {
                showProgressDialog(resources.getString(R.string.pleas_wait))
                withContext(ioDispatcher) {
                    auth.signInWithEmailAndPassword(stringEmail, stringPassword)
                        .addOnCompleteListener(this@SignInActivity) { task ->
                            if (task.isSuccessful) {
                                lifecycleScope.launch {
                                    FireStoreClass().loadUserData(this@SignInActivity)
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
        binding = null
    }
}