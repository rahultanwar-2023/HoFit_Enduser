package com.hofit.hofituser.ui.OneKey

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hofit.hofituser.PaymentPass
import com.hofit.hofituser.R
import com.hofit.hofituser.models.PassModel

class OnePassAdapter(val context: Context, private val pass: List<PassModel>) :
    RecyclerView.Adapter<OnePassAdapter.PassViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassViewModel {
        return PassViewModel(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_pass_design, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PassViewModel, position: Int) {
        val key = pass[position]
        holder.mPassMonth.text = key.pass_month
        holder.mPassMainPrice.text = key.pass_actual_price
        holder.mPassDiscountPrice.text = key.pass_discount_price
        holder.mPassEmiPrice.text = key.pass_emi_cost
        holder.mBillType.text = key.pass_bill_type
        holder.mContent1.text = key.pass_content_1
        holder.mContent2.text = key.pass_content_2
        holder.mContent3.text = key.pass_content_3
        holder.mContent4.text = key.pass_content_4
        holder.mSessionLimit.text = key.pass_session_limit

        holder.mProceedBTN.setOnClickListener {
            val intent = Intent(context.applicationContext, PaymentPass::class.java)
            intent .putExtra("layout_id", "Key_Pass_ID_59")
            intent.putExtra("pass_type", key.pass_month)
            intent.putExtra("pass_session", key.pass_session_limit)
            intent.putExtra("pass_actual", key.pass_actual_price)
            intent.putExtra("pass_price", key.pass_discount_price)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return pass.size
    }

    class PassViewModel(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val mPassMonth: TextView = itemView.findViewById(R.id.pass_month)
        val mPassMainPrice: TextView = itemView.findViewById(R.id.pass_main_price)
        val mPassDiscountPrice: TextView = itemView.findViewById(R.id.pass_discount_price)
        val mPassEmiPrice: TextView = itemView.findViewById(R.id.pass_emi_price)
        val mBillType: TextView = itemView.findViewById(R.id.bill_type)
        val mContent1: TextView = itemView.findViewById(R.id.content_1)
        val mContent2: TextView = itemView.findViewById(R.id.content_2)
        val mContent3: TextView = itemView.findViewById(R.id.content_3)
        val mContent4: TextView = itemView.findViewById(R.id.content_4)
        val mSessionLimit: TextView = itemView.findViewById(R.id.session_limit)
        val mProceedBTN: Button = itemView.findViewById(R.id.buy_now)
    }
}
