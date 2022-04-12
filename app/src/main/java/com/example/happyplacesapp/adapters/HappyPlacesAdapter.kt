package com.example.happyplacesapp.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplacesapp.R
import com.example.happyplacesapp.models.HappyPlaceModel

open class HappyPlacesAdapter(private val context: Context, private val list: ArrayList<HappyPlaceModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int, model: HappyPlaceModel)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_happy_place, parent, false)
        return MyViewHolder(view,mListener)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder){
            holder.itemView.findViewById<ImageView>(R.id.iv_place_image).setImageURI(Uri.parse(model.image))
            holder.itemView.findViewById<TextView>(R.id.tvTitle).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tvDescription).text = model.description

            holder.itemView.setOnClickListener {
                mListener.onItemClick(position,model)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view){

    }
}