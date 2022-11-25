package com.hofit.hofituser.ui.UserHome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hofit.hofituser.R
import com.hofit.hofituser.models.CentersModel

class CenterAdapter(val context: Context, private var center: List<CentersModel>) :
    RecyclerView.Adapter<CenterAdapter.CentersViewModel>() {


    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    fun setFilteredList(center: List<CentersModel>){
        this.center = center
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CentersViewModel {
        return CentersViewModel(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_centers, parent, false), mListener
        )
    }

    override fun onBindViewHolder(holder: CentersViewModel, position: Int) {
        val center = center[position]
        holder.centerName.text = center.center_name
        holder.centerType.text = center.center_category
        Glide.with(context)
            .load(center.center_image)
            .into(holder.centerImage)

        holder.centerRating.text = center.center_rating
    }

    override fun getItemCount(): Int {
        return center.size
    }

    class CentersViewModel(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val centerName: TextView = itemView.findViewById(R.id.center_name)
        val centerImage: ImageView = itemView.findViewById(R.id.center_image)
        val centerType: TextView = itemView.findViewById(R.id.center_category)
        val centerRating: TextView = itemView.findViewById(R.id.center_rating)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}