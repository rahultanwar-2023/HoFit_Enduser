package com.hofit.hofituser.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hofit.hofituser.R
import com.hofit.hofituser.models.ServiceModel

class ServiceAdapter(private val service: List<ServiceModel>) :
    RecyclerView.Adapter<ServiceAdapter.ServiceDataViewModel>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceDataViewModel {
        return ServiceDataViewModel(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_service, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ServiceDataViewModel, position: Int) {
        val serviceHolder = service[position]
        holder.serviceTitle.text = serviceHolder.service
    }

    override fun getItemCount(): Int {
        return service.size
    }

    class ServiceDataViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceTitle: TextView = itemView.findViewById(R.id.center_service)
    }

}