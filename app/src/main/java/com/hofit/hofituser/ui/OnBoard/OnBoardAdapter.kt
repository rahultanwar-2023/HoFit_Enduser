package com.hofit.hofituser.ui.OnBoard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.hofit.hofituser.OnBoarding
import com.hofit.hofituser.R
import com.hofit.hofituser.models.OnBoardingData
import org.w3c.dom.Text

class OnBoardAdapter(private var context: Context, private var onBoardingData: List<OnBoardingData>) : PagerAdapter() {

    override fun getCount(): Int {
        return onBoardingData.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.onboarding_layout, null)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val title: TextView = view.findViewById(R.id.title)

        imageView.setImageResource(onBoardingData[position].imageUrl)
        title.text = onBoardingData[position].title

        container.addView(view)
        return view
    }
}