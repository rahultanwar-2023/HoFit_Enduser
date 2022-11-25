package com.hofit.hofituser.ui.CenterProcced

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
import com.hofit.hofituser.models.CenterImageModel
import com.hofit.hofituser.models.PromoModel
import com.hofit.hofituser.ui.UserHome.PromoAdapter

class CenterImageAdapter (private val context: Context,
private val centerImageList: ArrayList<CenterImageModel>,
private val viewPager2: ViewPager2
) : RecyclerView.Adapter<CenterImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.center_image_shower)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_centerimages_shower, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = centerImageList[position]

        Glide.with(context).load(image.url).into(holder.imageView)

        if (position == centerImageList.size - 1) {
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return centerImageList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private val runnable = Runnable {
        centerImageList.addAll(centerImageList)
        notifyDataSetChanged()
    }

}