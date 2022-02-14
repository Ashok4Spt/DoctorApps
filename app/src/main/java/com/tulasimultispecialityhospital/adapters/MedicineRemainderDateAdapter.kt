package com.tulasimultispecialityhospital.adapters

import android.content.Context
import androidx.appcompat.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.MedicineRemainderModel
import java.text.SimpleDateFormat


class MedicineRemainderDateAdapter(
    val medicineRemainderArrayList: ArrayList<MedicineRemainderModel>,
    val context: Context
) : RecyclerView.Adapter<MedicineRemainderDateAdapter.ViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineRemainderDateAdapter.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_medicine_remainder_date, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MedicineRemainderDateAdapter.ViewHolder, position: Int) {
        holder.remainderDate.visibility = View.VISIBLE
        holder.remainderDate.text =
            SimpleDateFormat("EEEE | dd MMM").format(medicineRemainderArrayList[position].remindDate)

        holder.medicineRemainderListRecycleView!!.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        var medicineRemainderTimeAdapter =MedicineRemainderTimeAdapter(medicineRemainderArrayList.get(position).remindTimeArrayList, context)


        holder.medicineRemainderListRecycleView.adapter = medicineRemainderTimeAdapter
        holder.medicineRemainderListRecycleView.setRecycledViewPool(viewPool)


    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return medicineRemainderArrayList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val remainderDate = itemView.findViewById(R.id.remainderDate) as AppCompatTextView
        val medicineRemainderListRecycleView =
            itemView.findViewById(R.id.medicineRemainderListRecycleView) as RecyclerView
    }


    fun removeAt(position: Int) {
        medicineRemainderArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, medicineRemainderArrayList.size)

    }
}