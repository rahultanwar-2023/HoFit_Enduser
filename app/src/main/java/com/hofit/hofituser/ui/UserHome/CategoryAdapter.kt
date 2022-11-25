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
import com.hofit.hofituser.models.ActivityCategoryData

class CategoryAdapter(val context: Context, private val category: List<ActivityCategoryData>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryDataViewModel>() {

    private val limit = 6
    private lateinit var mListener: onItemClickListener

    interface  onItemClickListener{

        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listener: onItemClickListener){mListener = listener}



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryDataViewModel {
        return CategoryDataViewModel(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_categories, parent, false), mListener
        )
    }

    override fun onBindViewHolder(holder: CategoryDataViewModel, position: Int) {
        val category = category[position]
        holder.categoryTitle.text = category.title

        Glide.with(context)
            .load(category.url)
            .into(holder.categoryImage)

    }

    override fun getItemCount(): Int {
        return if(category.size > limit){
            limit
        } else {
            category.size
        }
    }

    class CategoryDataViewModel(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val categoryTitle: TextView = itemView.findViewById(R.id.category_title)
        val categoryImage: ImageView = itemView.findViewById(R.id.category_image)
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}