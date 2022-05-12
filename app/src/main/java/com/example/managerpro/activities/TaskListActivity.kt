package com.example.managerpro.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managerpro.R
import com.example.managerpro.adapters.TaskListItemAdapter
import com.example.managerpro.databinding.ActivityTaskListBinding
import com.example.managerpro.firebase.FireStoreClass
import com.example.managerpro.models.Board
import com.example.managerpro.models.Card
import com.example.managerpro.models.Task
import com.example.managerpro.utils.Constants
import kotlinx.coroutines.launch

class TaskListActivity : BaseActivity() {
    private var binding :ActivityTaskListBinding?=null
    private lateinit var mBoardDetails :Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        lifecycleScope.launch {
            var boardID=""
            if (intent.hasExtra(Constants.BOARD_ID)){
                boardID=intent.getStringExtra(Constants.BOARD_ID)!!
            }
            showProgressDialog(resources.getString(R.string.pleas_wait))
            FireStoreClass().getBoardDetails(this@TaskListActivity,boardID)
        }

    }

    private fun setupToolBar(){
        setSupportActionBar(binding?.taskListToolbar)
        if (supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title=mBoardDetails.name

        }
        binding?.taskListToolbar?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun boardDetails(board:Board){
        mBoardDetails=board
        hideProgressDialog()
        setupToolBar( )

     val addTaskList = Task()
        mBoardDetails.taskList.add(addTaskList)
        binding?.taskListRV?.layoutManager=  LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
        binding?.taskListRV?.setHasFixedSize(false)
        val adapter=TaskListItemAdapter(this@TaskListActivity,board.taskList)
        binding?.taskListRV?.adapter=adapter
    }

    fun createTaskList(taskListName :String){
        lifecycleScope.launch {
            val task=Task(taskListName,FireStoreClass().getCurrentUserID())
            mBoardDetails.taskList.add(0,task)
            mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1 )
            showProgressDialog(resources.getString(R.string.pleas_wait))
            FireStoreClass().addUpdateTaskList(this@TaskListActivity,mBoardDetails)
        }
    }

    fun addUpdateTaskListSuccess() {

        lifecycleScope.launch { FireStoreClass().getBoardDetails(this@TaskListActivity, mBoardDetails.boardID!!) }
    }
    fun editTask(position:Int,listName:String,item:Task){
        lifecycleScope.launch {
            showProgressDialog(resources.getString(R.string.pleas_wait))
            val task=Task(listName,item.createdBy)
            mBoardDetails.taskList[position]=task
            mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
            FireStoreClass().addUpdateTaskList(this@TaskListActivity,mBoardDetails)
        }
    }
  /*  fun updateTaskTitle(position:Int,listName:String,item:Task){
        lifecycleScope.launch {
            val task=Task(listName,item.createdBy)
            mBoardDetails.taskList[position]=task
            mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
            FireStoreClass().updateTaskList(this@TaskListActivity,task)
        }
    }*/

    fun deleteTask(position: Int){
        lifecycleScope.launch {
            showProgressDialog(resources.getString(R.string.pleas_wait))
            mBoardDetails.taskList.removeAt(position)
            mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
            FireStoreClass().addUpdateTaskList(this@TaskListActivity,mBoardDetails)
        }
    }
    fun addCardList(position: Int,cardName:String){
        lifecycleScope.launch {
            showProgressDialog(resources.getString(R.string.pleas_wait))

            val cardAssignedUsersList : ArrayList<String> = ArrayList()
            cardAssignedUsersList.add(FireStoreClass().getCurrentUserID())

            val card=Card(cardName,FireStoreClass().getCurrentUserID(),cardAssignedUsersList)

            val cardList=  mBoardDetails.taskList[position].cardList
            cardList.add(card)
            val task=Task(mBoardDetails.taskList[position].title,
                mBoardDetails.taskList[position].createdBy,
                cardList
            )
            mBoardDetails.taskList[position]=task
            mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
            FireStoreClass().addUpdateTaskList(this@TaskListActivity,mBoardDetails)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}