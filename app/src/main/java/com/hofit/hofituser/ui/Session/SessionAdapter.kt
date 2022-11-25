package com.hofit.hofituser.ui.Session

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hofit.hofituser.R
import com.hofit.hofituser.models.SessionModel

class SessionAdapter(val context: Context, var session: ArrayList<SessionModel>) :
    RecyclerView.Adapter<SessionAdapter.SessionViewModel>() {


    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewModel {
        return SessionViewModel(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_session, parent, false), mListener
        )
    }

    override fun onBindViewHolder(holder: SessionViewModel, position: Int) {
        val session = session[position]
        holder.sessionTitle.text = session.session_title
        holder.sessionCenter.text = session.session_center

        val dataNTime = session.session_date + ", " + session.session_time

        holder.sessionDate.text = dataNTime
        holder.sessionPrice.text = "₹" + session.session_amount

        Glide.with(context)
            .load(session.url)
            .into(holder.sessionImage)
    }

    override fun getItemCount(): Int {
        return session.size
    }

    class SessionViewModel(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val sessionImage: ImageView = itemView.findViewById(R.id.session_image)
        val sessionTitle: TextView = itemView.findViewById(R.id.session_title)
        val sessionCenter: TextView = itemView.findViewById(R.id.session_center)
        val sessionDate: TextView = itemView.findViewById(R.id.session_time_date)
        val sessionPrice: TextView = itemView.findViewById(R.id.session_price)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}