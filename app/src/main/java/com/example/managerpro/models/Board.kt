package com.example.managerpro.models

import android.os.Parcel
import android.os.Parcelable

data class Board(
    val name :String?="",
    val image :String?="",
    val createdBy :String?="",
    val assignedTo :ArrayList<String>? = ArrayList(),
    var boardID :String?="",
    var taskList :ArrayList<Task> = ArrayList()
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.createTypedArrayList(Task.CREATOR)!!
    )
    override fun describeContents()=0
    override fun writeToParcel(p0: Parcel, p1: Int) = with(p0){
        writeString(name)
        writeString(image)
        writeString(createdBy)
        writeStringList(assignedTo)
        writeString(boardID)
        writeTypedList(taskList)
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }

}
