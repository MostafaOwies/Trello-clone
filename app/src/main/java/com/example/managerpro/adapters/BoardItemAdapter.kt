package com.example.managerpro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.managerpro.R
import com.example.managerpro.databinding.ItemRowLayoutBinding
import com.example.managerpro.models.Board

class BoardItemAdapter (private var boardList:ArrayList<Board>,private val context: Context):RecyclerView.Adapter<BoardItemAdapter.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    class ViewHolder(binding:ItemRowLayoutBinding):RecyclerView.ViewHolder(binding.root){
        val boardImageV=binding.boardItemImage
        val boardName=binding.boardItemNameTV
        val boardCreator=binding.boardItemCreatorTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(ItemRowLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=boardList[position]
        Glide
            .with(context)
            .load(item.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.boardImageV)
        holder.boardName.text=item.name
        holder.boardCreator.text=item.createdBy
        holder.itemView.setOnClickListener{
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item)
            }
        }
    }

    override fun getItemCount(): Int {
        return boardList.size
    }

    interface OnClickListener {
        fun onClick(position: Int, item: Board)
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }
}