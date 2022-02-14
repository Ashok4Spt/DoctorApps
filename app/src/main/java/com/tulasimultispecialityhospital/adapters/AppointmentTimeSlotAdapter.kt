package com.tulasimultispecialityhospital.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.TimeSlotModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AppointmentTimeSlotAdapter(val timeSlotArrayList: ArrayList<TimeSlotModel>, val context: Context) :
    RecyclerView.Adapter<AppointmentTimeSlotAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentTimeSlotAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_select_timeslot, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: AppointmentTimeSlotAdapter.ViewHolder, position: Int) {

        var timeDisplayStr: String = ""
        var calendar: Calendar?
        try {
            calendar = Calendar.getInstance()
            calendar.set(
                Calendar.HOUR_OF_DAY,
                Integer.parseInt(timeSlotArrayList[position].slot_timing!!.split("-")[0].split(":")[0].trim())
            )
            if (timeSlotArrayList[position].slot_timing!!.split("-")[0].split(":")[1].trim().startsWith("0"))
                calendar.set(
                    Calendar.MINUTE,
                    Integer.parseInt(
                        timeSlotArrayList[position].slot_timing!!.split("-")[0].split(":")[1].trim().substring(
                            1
                        )
                    )
                )
            else
                calendar.set(
                    Calendar.MINUTE,
                    Integer.parseInt(timeSlotArrayList[position].slot_timing!!.split("-")[0].split(":")[1].trim())
                )
            timeDisplayStr = SimpleDateFormat("hh:mm a").format(calendar.time)

            calendar.set(
                Calendar.HOUR_OF_DAY,
                Integer.parseInt(timeSlotArrayList[position].slot_timing!!.split("-")[1].split(":")[0].trim())
            )
            if (timeSlotArrayList[position].slot_timing!!.split("-")[1].split(":")[1].trim().startsWith("0"))
                calendar.set(
                    Calendar.MINUTE,
                    Integer.parseInt(
                        timeSlotArrayList[position].slot_timing!!.split("-")[1].split(":")[1].trim().substring(
                            1
                        )
                    )
                )
            else
                calendar.set(
                    Calendar.MINUTE,
                    Integer.parseInt(timeSlotArrayList[position].slot_timing!!.split("-")[1].split(":")[1].trim())
                )
            timeDisplayStr = timeDisplayStr + " - " + SimpleDateFormat("hh:mm a").format(calendar.time)
        } catch (e: Exception) {
            timeDisplayStr = timeSlotArrayList[position].slot_timing!!
        }

        holder.timeSlotText.text = timeDisplayStr
        if (TextUtils.equals(timeSlotArrayList[position].slots, "0")) {
            holder.timeSlotStatusText.text = "Queue full"
            holder.timeSlotSelectionItem.setBackgroundResource(R.drawable.timeslot_box_blocked)
        } else {
            if (timeSlotArrayList[position].isTimeSlotSelected) {
                holder.timeSlotSelectionItem.setBackgroundResource(R.drawable.timeslot_box_primary)
                holder.timeSlotText.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                holder.timeSlotStatusText.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            } else {
                holder.timeSlotSelectionItem.setBackgroundResource(R.drawable.timeslot_box_primary_line)
                holder.timeSlotText.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                holder.timeSlotStatusText.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
            }
            holder.timeSlotStatusText.text = timeSlotArrayList[position].slots + " booking(s) available"

        }

        holder.timeSlotSelectionItem.setOnClickListener {
            if (!TextUtils.equals(timeSlotArrayList[position].slots, "0")) {
                for ((index, timeSlotModel: TimeSlotModel) in timeSlotArrayList.withIndex()) {
                    timeSlotModel.isTimeSlotSelected = false
                    timeSlotArrayList.set(index, timeSlotModel)
                }
                timeSlotArrayList[position].isTimeSlotSelected = true
                notifyDataSetChanged()
            }
            // (context as BookAppointmentActivity).setSelectedAppointmentTypeToView(timeSlotArrayList[position])
        }

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return timeSlotArrayList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeSlotText = itemView.findViewById(R.id.timeSlotText) as AppCompatTextView
        val timeSlotStatusText = itemView.findViewById(R.id.timeSlotStatusText) as AppCompatTextView
        val timeSlotSelectionItem = itemView.findViewById(R.id.timeSlotSelectionItem) as LinearLayout

    }
}