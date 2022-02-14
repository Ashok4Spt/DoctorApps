package com.tulasimultispecialityhospital.adapters

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.MedicineRemainderModel


class MedicineRemainderAdapter(
    val medicineRemainderArrayList: ArrayList<MedicineRemainderModel>,
    val context: Context
) : RecyclerView.Adapter<MedicineRemainderAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineRemainderAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_medicine_remainder, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MedicineRemainderAdapter.ViewHolder, position: Int) {
        holder.medicineName.text = medicineRemainderArrayList[position].medicineName
        holder.medicineDetails.text = medicineRemainderArrayList[position].dosage + " ( " +
                medicineRemainderArrayList[position].strength + " " + medicineRemainderArrayList[position].strengthUnit + " ) " +
                medicineRemainderArrayList[position].whenToTake
        if(position==medicineRemainderArrayList.size-1)
            holder.medicineRemainderDivider.visibility=View.GONE
        else
            holder.medicineRemainderDivider.visibility=View.VISIBLE

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return medicineRemainderArrayList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineName = itemView.findViewById(R.id.medicineName) as AppCompatTextView
        val medicineDetails = itemView.findViewById(R.id.medicineDetails) as AppCompatTextView
        val medicineRemainderDivider = itemView.findViewById(R.id.medicineRemainderDivider) as View
    }


    fun removeAt(position: Int) {
        medicineRemainderArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, medicineRemainderArrayList.size)

    }
}