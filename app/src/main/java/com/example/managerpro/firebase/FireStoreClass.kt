package com.example.managerpro.firebase


import android.widget.Toast
import com.example.managerpro.activities.*
import com.example.managerpro.models.Board
import com.example.managerpro.models.Task
import com.example.managerpro.models.User
import com.example.managerpro.utils.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FireStoreClass(private val ioDispatcher: CoroutineDispatcher=Dispatchers.IO) {

    private val mFireStore=Firebase.firestore
    //suspend the activity from the main thread
   suspend fun registerUserToFireStore(activity: SignUpActivity,userInfo:User){
        //Create a collection "users" in the FireStore
       //run the coroutine on the IO thread
        try {
            withContext(ioDispatcher){
                mFireStore.collection(Constants.USERS).document(getCurrentUserID())
                    .set(userInfo, SetOptions.merge())
                    .addOnSuccessListener {
                        activity.userRegisteredSuccessfully()
                    }
            }
        }catch (e :Exception){
            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            activity.hideProgressDialog()
        }
    }

    //retrieve the user from the FireStore and do a Certain action based on the current activity
    suspend fun loadUserData(activity: BaseActivity,readBoardList:Boolean=false){
        try {
            withContext(ioDispatcher) {
                mFireStore.collection(Constants.USERS).document(getCurrentUserID())
                    .get()
                    .addOnSuccessListener {
                        val loggedInUser = it.toObject(User::class.java)!!
                        when (activity) {
                            is SignInActivity -> {
                                activity.signInSuccessfully(loggedInUser)
                            }
                            is MainActivity -> {
                                activity.updateNavigationUserDetails(loggedInUser, readBoardList)
                            }
                            is UserProfile -> {
                                activity.getUserDataInViews(loggedInUser)
                            }
                        }
                    }
            }
        }catch (e :Exception){
            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            activity.hideProgressDialog()
        }
    }

    //updates the users DATA in the FireStore
    suspend fun updateUserProfile(activity: UserProfile,userHashMap:HashMap<String,Any>){
        try {
            withContext(ioDispatcher) {
                mFireStore.collection(Constants.USERS).document(getCurrentUserID())
                    .update(userHashMap)
                    .addOnSuccessListener {
                        activity.profileUpdateSuccess()
                    }
            }
        }catch (e :Exception){
            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            activity.hideProgressDialog()
        }
    }

    //create a document named Board in the FireStore
    suspend fun createBoard(activity: CreateBoardActivity, board:Board){
        try {
            withContext(ioDispatcher){
                mFireStore.collection(Constants.BOARDS).document()
                    .set(board, SetOptions.merge())
                    .addOnSuccessListener {
                        activity.boardCreatedSuccessfully()
                    }
            }
        }catch (e : Exception){
            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            activity.hideProgressDialog()
        }

    }

    //retrieve board list from the FireStore based on the user ID who created it
    //and display it for the user in the main activity
   suspend fun getBoardList(activity:MainActivity){
        try {
            withContext(ioDispatcher){
                mFireStore.collection(Constants.BOARDS)
                    .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserID())
                    .get().addOnSuccessListener {
                        val boardList: ArrayList<Board> = ArrayList()
                        for (i in it.documents){
                            val board=i.toObject(Board::class.java)!!
                            board.boardID=i.id
                            boardList.add(board)
                        }
                        activity.boardRecyclerView(boardList)
                    }
            }
        }catch (e : Exception){
            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            activity.hideProgressDialog()
        }
    }

    //retrieve single board details from the FireStore
    //so we can assign different tasks for each board
   suspend fun getBoardDetails(activity: TaskListActivity,boardID:String){
        try {
            withContext(ioDispatcher){
                mFireStore.collection(Constants.BOARDS)
                    .document(boardID)
                    .get()
                    .addOnSuccessListener {
                        val board=it.toObject(Board::class.java)!!
                        board.boardID=it.id
                        activity.boardDetails(board)
                    }
            }
        }catch (e : Exception){
            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            activity.hideProgressDialog()
        }
    }

  suspend fun addUpdateTaskList(activity: TaskListActivity, board: Board) {

        try {
            withContext(ioDispatcher){
                val taskListHashMap = HashMap<String, Any>()
                taskListHashMap[Constants.TASK_LIST] = board.taskList
                mFireStore.collection(Constants.BOARDS)
                    .document(board.boardID!!)
                    .update(taskListHashMap)
                    .addOnSuccessListener {
                        activity.addUpdateTaskListSuccess()
                    }
            }
        }catch (e : Exception){
            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            activity.hideProgressDialog()
        }

    }

  /*  suspend fun updateTaskList(activity: TaskListActivity, task: Task) {

        try {
            withContext(ioDispatcher){
                val board=Board()
                val taskListHashMap = HashMap<String, Any>()
                taskListHashMap[Constants.taskTitle]=task.title!!
                mFireStore.collection(Constants.BOARDS)
                    .document(board.boardID!!)
                    .update(taskListHashMap)
                    .addOnSuccessListener {
                        activity.addUpdateTaskListSuccess()
                    }
            }
        }catch (e : Exception){
            Toast.makeText(activity,e.message,Toast.LENGTH_SHORT).show()
            activity.hideProgressDialog()
        }

    }*/

     fun getCurrentUserID():String{
        val currentUser =Firebase.auth.currentUser
        var currentUserID=""
        if (currentUser!=null){
            currentUserID=currentUser.uid
        }
        return currentUserID
    }
}