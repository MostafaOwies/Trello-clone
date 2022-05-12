package com.example.managerpro.activities

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.managerpro.databinding.ActivityIntroBinding
import com.example.managerpro.firebase.FireStoreClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IntroActivity : BaseActivity() {
    private var binding:ActivityIntroBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.signupBTN?.setOnClickListener {
            val intent= Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding?.SignInBTN?.setOnClickListener {
            val intent= Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch(Dispatchers.IO){
            val currentUserID = FireStoreClass().getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                finish()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

}