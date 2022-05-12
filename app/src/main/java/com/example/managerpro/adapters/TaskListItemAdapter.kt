package com.example.managerpro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.managerpro.activities.TaskListActivity
import com.example.managerpro.databinding.TaskItemBinding
import com.example.managerpro.models.Task


open class TaskListItemAdapter(
    private val context : Context,
    private var taskList:ArrayList<Task>)
    :RecyclerView.Adapter<TaskListItemAdapter.ViewHolder>() {

        class ViewHolder(binding : TaskItemBinding):RecyclerView.ViewHolder(binding.root){
            val addTaskTitle=binding.addTaskTitleTV
            val taskTitle=binding.taskTitleTV
            val mainLinearLayout = binding.taskItemLL
            val taskTitleLL=binding.taskTitleLL
            val listNameCV=binding.listNameCV
            val cancelListItem=binding.cancelListIB
            val doneListItem=binding.listNameDoneIB
            val listNameET=binding.listNameET
            val deleteListName=binding.deleteListIB
            val editListName=binding.editListNameIB
            val editListNameCV=binding.editListNameCV
            val editListNameET=binding.EditListNameET
            val cancelEditTitle=binding.cancelEditListIB
            val listNameDoneEdit=binding.editListNameDoneIB
            val addCardBTN=binding.addCardTV
            val cardNameCV=binding.editCardNameCV
            val doneCardName=binding.editCardNameDoneIB
            val cancelCardName=binding.cancelCardNameIB
            val cardNameET=binding.cardNameET
            val cardList=binding.cardListRV
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TaskItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=taskList[position]
        if (position==taskList.size-1){
            holder.addTaskTitle.visibility=View.VISIBLE
            holder.mainLinearLayout.visibility=View.GONE
        }else{
            holder.addTaskTitle.visibility=View.GONE
            holder.mainLinearLayout.visibility=View.VISIBLE
        }
        holder.taskTitle.text=item.title
        holder.addTaskTitle.setOnClickListener {
            holder.addTaskTitle.visibility=View.GONE
            holder.listNameCV.visibility=View.VISIBLE
        }
        holder.cancelListItem.setOnClickListener {
            holder.addTaskTitle.visibility=View.VISIBLE
            holder.listNameCV.visibility=View.GONE
        }
        holder.doneListItem.setOnClickListener {
            if (holder.listNameET.text!!.isEmpty()){
                holder.listNameET.error="Cannot be empty"
            }else if (context is TaskListActivity){
               context.createTaskList(holder.listNameET.text.toString())
            }
        }

        holder.editListName.setOnClickListener{
            holder.editListNameET.setText(item.title)
            holder.taskTitleLL.visibility=View.GONE
            holder.editListNameCV.visibility=View.VISIBLE
        }

        holder.cancelEditTitle.setOnClickListener {
            holder.taskTitleLL.visibility=View.VISIBLE
            holder.editListNameCV.visibility=View.GONE
        }

        holder.deleteListName.setOnClickListener {
            if (context is TaskListActivity){
                context.deleteTask(position)
            }
        }
        holder.listNameDoneEdit.setOnClickListener {
            if (holder.editListNameET.text!!.isEmpty()){
                holder.editListNameET.error="Cannot test be empty"
            }else if (context is TaskListActivity){
               context.editTask(position,holder.editListNameET.text.toString(),item)
                //context.updateTaskTitle(position,holder.editListNameET.text.toString(),item)
            }
        }

        holder.addCardBTN.setOnClickListener {
            holder.addCardBTN.visibility=View.GONE
            holder.cardNameCV.visibility=View.VISIBLE
        }
        holder.cancelCardName.setOnClickListener {
            holder.addCardBTN.visibility=View.VISIBLE
            holder.cardNameCV.visibility=View.GONE
        }
        holder.doneCardName.setOnClickListener {
            if (holder.cardNameET.text!!.isEmpty()){
                holder.cardNameET.error="Cannot be empty"
            }else if(context is TaskListActivity){
                context.addCardList(position,holder.cardNameET.text.toString())
            }
        }
        holder.cardList.layoutManager=LinearLayoutManager(context)
        holder.cardList.setHasFixedSize(false)
        val adapter=CardItemAdapter(context,item.cardList)
        holder.cardList.adapter=adapter
    }

    override fun getItemCount(): Int {
       return taskList.size
    }
}



