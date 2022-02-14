package com.tulasimultispecialityhospital.adapters

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.MedicineRemainderModel
import java.text.SimpleDateFormat


class MedicineRemainderAddTimeAdapter(
    val medicineRemainderArrayList: ArrayList<MedicineRemainderModel>,
    val context: Context
) : RecyclerView.Adapter<MedicineRemainderAddTimeAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineRemainderAddTimeAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_medicine_remainder_add_time, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MedicineRemainderAddTimeAdapter.ViewHolder, position: Int) {
        holder.remainderTime.text = SimpleDateFormat("hh:mm aa").format(medicineRemainderArrayList[position].remindTime)
        holder.cancelTime.setOnClickListener {
            removeAt(position)
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return medicineRemainderArrayList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val remainderTime = itemView.findViewById(R.id.remainderTime) as AppCompatTextView
        val cancelTime = itemView.findViewById(R.id.cancelTime) as AppCompatImageView

    }


    fun removeAt(position: Int) {
        medicineRemainderArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, medicineRemainderArrayList.size)

    }
}