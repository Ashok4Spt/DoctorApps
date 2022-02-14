package com.tulasimultispecialityhospital.adapters

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import com.tulasimultispecialityhospital.BookAppointmentActivity
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.DoctorModel
import com.tulasimultispecialityhospital.models.LocationsModel

class DoctorAppointmentLocationsAdapter(val locationsList: ArrayList<DoctorModel>, val context: Context) : RecyclerView.Adapter<DoctorAppointmentLocationsAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorAppointmentLocationsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_select_location, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: DoctorAppointmentLocationsAdapter.ViewHolder, position: Int) {
        holder.locationCheckedTextView.text = locationsList[position].clinic_location
        holder.locationCheckedTextView.isChecked= locationsList[position].isLocationSelected

        holder.locationCheckedTextView.setOnClickListener {
            locationsList[position].isLocationSelected=true;
            notifyDataSetChanged()
            (context as BookAppointmentActivity).setSelectedLocationToView(locationsList[position])
        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return locationsList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationCheckedTextView = itemView.findViewById(R.id.locationCheckedTextView) as CheckedTextView

        fun bindItems(locationsModel: LocationsModel) {
            val locationCheckedTextView = itemView.findViewById(R.id.locationCheckedTextView) as AppCompatTextView
            locationCheckedTextView.text = locationsModel.clinic_location
        }
    }
}