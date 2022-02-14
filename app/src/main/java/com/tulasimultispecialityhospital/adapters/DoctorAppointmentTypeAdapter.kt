package com.tulasimultispecialityhospital.adapters

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tulasimultispecialityhospital.BookAppointmentActivity
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.AppointmentTypeModel

class DoctorAppointmentTypeAdapter(val appointmentTypeArrayList: ArrayList<AppointmentTypeModel>, val context: Context) : RecyclerView.Adapter<DoctorAppointmentTypeAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorAppointmentTypeAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_appointment_type, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: DoctorAppointmentTypeAdapter.ViewHolder, position: Int) {
        holder.appointmentTypeTextView.text = appointmentTypeArrayList[position].appointmentType


        holder.appointmentTypeTextView.setOnClickListener {
            (context as BookAppointmentActivity).setSelectedAppointmentTypeToView(appointmentTypeArrayList[position],position+1)
        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return appointmentTypeArrayList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appointmentTypeTextView = itemView.findViewById(R.id.appointmentTypeTextView) as AppCompatTextView

    }
}