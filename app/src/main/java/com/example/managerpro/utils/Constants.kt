package com.example.managerpro.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constants {
    const val USERS :String = "users"
    const val BOARDS :String="boards"
    const val IMAGE :String = "image"
    const val NAME :String = "name"
    const val MOBILE :String = "mobile"
    const val ASSIGNED_TO :String="assignedTo"
    const val BOARD_ID:String="boardID"
    const val TASK_LIST:String = "taskList"
    const val taskTitle:String="title"

     fun getFileExtension(activity:Activity, uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(
           activity.contentResolver.getType(uri!!))
    }
}