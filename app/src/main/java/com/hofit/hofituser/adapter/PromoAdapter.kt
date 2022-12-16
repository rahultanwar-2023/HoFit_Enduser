package com.hofit.hofituser.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.hofit.hofituser.R
import com.hofit.hofituser.models.PromoModel

class PromoAdapter (
    private val context: Context,
    private val keySliderList: ArrayList<PromoModel>,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<PromoAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_key)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = keySliderList[position]

        Glide.with(context).load(image.url).into(holder.imageView)

        if (position == keySliderList.size - 1) {
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return keySliderList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private val runnable = Runnable {
        keySliderList.addAll(keySliderList)
        notifyDataSetChanged()
    }

}