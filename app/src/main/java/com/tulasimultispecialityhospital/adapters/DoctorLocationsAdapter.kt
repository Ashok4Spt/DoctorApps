package com.tulasimultispecialityhospital.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.DoctorModel

class DoctorLocationsAdapter(val locationsList: ArrayList<DoctorModel>, val context: Context) :
    RecyclerView.Adapter<DoctorLocationsAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorLocationsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_doctor_location, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: DoctorLocationsAdapter.ViewHolder, position: Int) {
        holder.doctorLocation.text = locationsList[position].clinic_location
        if (position == locationsList.size - 1) {
            holder.locationDivider.visibility = View.GONE
        }else{
            holder.locationDivider.visibility = View.VISIBLE
        }

        // if (position == 1) {
        holder.doctorAvailability.setTextColor(ContextCompat.getColor(context, R.color.colorAvailability))
        holder.doctorAvailability.text = "Available now"
        // } else
        //    holder.doctorAvailability.setTextColor(ContextCompat.getColor(context, R.color.colorErrorRed))

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return locationsList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorLocation = itemView.findViewById(R.id.doctorLocation) as AppCompatTextView
        val doctorAvailability = itemView.findViewById(R.id.doctorAvailability) as AppCompatTextView
        val locationDivider = itemView.findViewById(R.id.locationDivider) as View
    }
}