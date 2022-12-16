package com.hofit.hofituser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hofit.hofituser.R
import com.hofit.hofituser.models.UpcomingBookingModel

class UpcomingBookingAdapter(val context: Context, private val pass: List<UpcomingBookingModel>) :
    RecyclerView.Adapter<UpcomingBookingAdapter.BookingViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewModel {
        return BookingViewModel(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_upcoming_booking, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BookingViewModel, position: Int) {
        val booking = pass[position]
        holder.mPassMonth.text = booking.booking_outlet
        holder.mPassMainPrice.text = booking.booking_category
        holder.mPassDiscountPrice.text = booking.booking_time
        holder.mPassEmiPrice.text = booking.booking_address

//        holder.mProceedBTN.setOnClickListener {
//            val intent = Intent(context.applicationContext, PaymentPass::class.java)
//            intent .putExtra("layout_id", "Key_Pass_ID_59")
//            intent.putExtra("pass_type", key.pass_month)
//            intent.putExtra("pass_session", key.pass_session_limit)
//            intent.putExtra("pass_actual", key.pass_actual_price)
//            intent.putExtra("pass_price", key.pass_discount_price)
//            context.startActivity(intent)
//        }
    }

    override fun getItemCount(): Int {
        return pass.size
    }

    class BookingViewModel(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val mPassMonth: TextView = itemView.findViewById(R.id.tv_outlet_name)
        val mPassMainPrice: TextView = itemView.findViewById(R.id.tv_category)
        val mPassDiscountPrice: TextView = itemView.findViewById(R.id.tv_time)
        val mPassEmiPrice: TextView = itemView.findViewById(R.id.tv_address)
    }
}