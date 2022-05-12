package com.example.managerpro.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.bumptech.glide.Glide
import com.example.managerpro.R
import com.example.managerpro.adapters.BoardItemAdapter
import com.example.managerpro.databinding.ActivityMainBinding
import com.example.managerpro.firebase.FireStoreClass
import com.example.managerpro.models.Board
import com.example.managerpro.models.User
import com.example.managerpro.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private var binding :ActivityMainBinding?=null
    private lateinit var auth:FirebaseAuth
    private lateinit var  mUserName :String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth=Firebase.auth

        setSupportActionBar(binding?.mainToolBar)
        binding ?.mainToolBar?.setNavigationOnClickListener{
            binding?.drawerLayout?.open()
        }

        val startForResultProfile=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result: ActivityResult ->
            if (result.resultCode==Activity.RESULT_OK){
                lifecycleScope.launch{
                    FireStoreClass().loadUserData(this@MainActivity)
                }
            }
        }

        binding?.navigationView?.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.My_profile ->{
                    startForResultProfile.launch(Intent(this, UserProfile::class.java))
                    binding?.drawerLayout?.close()
                }
                R.id.signOut ->{
                    auth.signOut()
                    val intent= Intent(this,IntroActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
            false
        }

        val startForResultBoard=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result: ActivityResult ->
            if (result.resultCode==Activity.RESULT_OK){
                lifecycleScope.launch{FireStoreClass().getBoardList(this@MainActivity)}
            }
        }

        binding?.fabNewBoard?.setOnClickListener{
            val intent=Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startForResultBoard.launch(intent)
        }

        lifecycleScope.launch {
            FireStoreClass().loadUserData(this@MainActivity,true)
        }

    }

    fun updateNavigationUserDetails(user :User, readBoardList:Boolean) {
        val userImage=findViewById<ImageView>(R.id.userImageNavIV)
        val userName=findViewById<TextView>(R.id.userNameNaveTV)

        mUserName=user.name!!
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(userImage)
        userName.text=user.name

        if (readBoardList){
            showProgressDialog(resources.getString(R.string.pleas_wait))
           lifecycleScope.launch { FireStoreClass().getBoardList(this@MainActivity)}
        }
    }

    override fun onBackPressed() {
        val drawerOpen=binding?.drawerLayout!!
        if (drawerOpen.isOpen){
            binding?.drawerLayout?.close()
        }else{
            lifecycleScope.launch { doublePressToExit() }
        }
    }

    fun boardRecyclerView(boardList :ArrayList<Board>){
        binding?.boardListRV?.layoutManager=LinearLayoutManager(this)
        binding?.boardListRV?.setHasFixedSize(true)
        val adapter=BoardItemAdapter(boardList,this)
        binding?.boardListRV?.adapter=adapter

        adapter.setOnClickListener(object :BoardItemAdapter.OnClickListener{
            override fun onClick(position: Int, item: Board) {
                val intent=Intent(this@MainActivity,TaskListActivity::class.java)
                intent.putExtra(Constants.BOARD_ID,item.boardID)
                startActivity(intent)
            }
        })

        hideProgressDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }


}