package com.example.managerpro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.managerpro.databinding.ItemCardBinding
import com.example.managerpro.models.Card

class CardItemAdapter( private val context:Context, private var cardList:ArrayList<Card>) :RecyclerView.Adapter<CardItemAdapter.ViewHolder>(){

    class ViewHolder (binding: ItemCardBinding):RecyclerView.ViewHolder(binding.root){
        val cardNameTV=binding.cardNameTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=cardList[position]
        holder.cardNameTV.text=item.name
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}