package com.tulasimultispecialityhospital.adapters

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.MedicineRemainderModel
import java.text.SimpleDateFormat


class MedicineRemainderTimeAdapter(
    val medicineRemainderArrayList: ArrayList<MedicineRemainderModel>?,
    val context: Context
) : RecyclerView.Adapter<MedicineRemainderTimeAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineRemainderTimeAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_medicine_remainder_time, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MedicineRemainderTimeAdapter.ViewHolder, position: Int) {
        holder.medicineRemainderTime.text =
            SimpleDateFormat("hh:mm aa").format(medicineRemainderArrayList?.get(position)!!.remindTime)
        holder.medicineRemainderListRecycleView!!.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        var medicineRemainderAdapter =
            medicineRemainderArrayList.get(position).medicineRemaindersList?.let {
                MedicineRemainderAdapter(
                    it,
                    context
                )
            }
        holder.medicineRemainderListRecycleView.adapter = medicineRemainderAdapter
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return medicineRemainderArrayList!!.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineRemainderTime = itemView.findViewById(R.id.medicineRemainderTime) as AppCompatTextView
        val medicineRemainderListRecycleView = itemView.findViewById(R.id.medicineRemainderListRecycleView) as RecyclerView

    }


    fun removeAt(position: Int) {
        medicineRemainderArrayList!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, medicineRemainderArrayList.size)

    }
}