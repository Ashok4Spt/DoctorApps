package com.tulasimultispecialityhospital.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.AppointmentModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import kotlinx.android.synthetic.main.dialog_appointment_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AppointmentsAdapter(val appointmentArrayList: ArrayList<AppointmentModel>, val context: Context) :
    RecyclerView.Adapter<AppointmentsAdapter.ViewHolder>() {
    lateinit var dialog: Dialog
    private var myDoctorUtility: MyDoctorUtility? = null
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_booked_appointment, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: AppointmentsAdapter.ViewHolder, position: Int) {
        myDoctorUtility = MyDoctorUtility.getInstance(context)
        holder.doctorName.text = myDoctorUtility!!.getDoctorData()!!.doctor_name

        holder.appointmentTime.text = SimpleDateFormat("EEEE | dd MMM yyyy").format(
            SimpleDateFormat("dd-MM-yyyy").parse(appointmentArrayList[position].slot_date).time
        ) + ", " + formatSelectedTime(appointmentArrayList[position].slot_timing)

        holder.moreDetails.setOnClickListener {
            var calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = SimpleDateFormat("dd-MM-yyyy").parse(appointmentArrayList[position].slot_date).time
            var slotTiming: String = appointmentArrayList[position].slot_timing!!
            calendar.set(
                Calendar.HOUR_OF_DAY,
                Integer.parseInt(slotTiming!!.split("-")[1].split(":")[0].trim())
            )

            if (slotTiming!!.split("-")[1].split(":")[1].trim().startsWith("0"))
                calendar.set(
                    Calendar.MINUTE,
                    Integer.parseInt(
                        slotTiming!!.split("-")[1].split(":")[1].trim().substring(
                            1
                        )
                    )
                )
            else
                calendar.set(
                    Calendar.MINUTE,
                    Integer.parseInt(slotTiming!!.split("-")[1].split(":")[1].trim())
                )
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)

            if (calendar.timeInMillis > Calendar.getInstance().timeInMillis)
                showConfirmDialog("Upcoming Appointment", appointmentArrayList[position])
            else
                showConfirmDialog("Appointment Completed", appointmentArrayList[position])
        }
        if (getIsCurrentBooking(appointmentArrayList[position]))
            holder.appointmentItem.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
        else
            holder.appointmentItem.setBackgroundColor(ContextCompat.getColor(context, R.color.colorEditBox))

        if (position == appointmentArrayList.size - 1)
            holder.appointmentDivider.visibility = View.GONE
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return appointmentArrayList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorName = itemView.findViewById(R.id.doctorName) as AppCompatTextView
        val appointmentTime = itemView.findViewById(R.id.appointmentTime) as AppCompatTextView
        val moreDetails = itemView.findViewById(R.id.moreDetails) as CardView
        val appointmentItem = itemView.findViewById(R.id.appointmentItem) as LinearLayout
        val appointmentDivider = itemView.findViewById<View>(R.id.appointmentDivider)

    }

    private fun showConfirmDialog(
        title: String,
        appointmentModel1: AppointmentModel
    ) {
        dialog = Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_appointment_details)
        dialog.dialogAppointmentTitle.text = title
        dialog.dialogDoctorName.text= myDoctorUtility!!.getDoctorData()!!.doctor_name
        dialog.dialogAppointmentTime.text= SimpleDateFormat("dd/MM/yyyy").format(
            SimpleDateFormat("dd-MM-yyyy").parse(appointmentModel1.slot_date).time
        ) + " | " + formatSelectedTime(appointmentModel1.slot_timing)


        if (Integer.parseInt(appointmentModel1.slot_timing!!.split("-")[0].split(":")[0].trim()) < 12) {
            dialog.dialogAppointmentTime.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.ic_morning)!!, null, null, null)
        }
        if (Integer.parseInt(appointmentModel1.slot_timing!!.split("-")[0].split(":")[0].trim()) >= 12) {
            dialog.dialogAppointmentTime.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.ic_afternoon)!!, null, null, null)
        }
        if (Integer.parseInt(appointmentModel1.slot_timing!!.split("-")[0].split(":")[0].trim()) >=18) {
            dialog.dialogAppointmentTime.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(context, R.drawable.ic_evening)!!, null, null, null)
        }

        dialog.dialogDoctorPhone.text=myDoctorUtility!!.getDoctorData()!!.mobile
        dialog.dialogHospitalLocation.text=appointmentModel1.clinic_location

        if(!TextUtils.equals(title,"Upcoming Appointment")){
            dialog.dialogCancelAppointmentLayout.visibility=View.GONE
        }

        dialog.appointmentClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.dialogCancelAppointment.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        var window = dialog.window
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }


    fun formatSelectedTime(slotTiming: String?): String {
        var timeDisplayStr: String = "";
        try {
            var calendar = Calendar.getInstance()
            calendar.set(
                Calendar.HOUR_OF_DAY, Integer.parseInt(slotTiming!!.split("-")[0].split(":")[0].trim())
            )
            if (slotTiming!!.split("-")[0].split(":")[1].trim().startsWith("0"))
                calendar.set(
                    Calendar.MINUTE, Integer.parseInt(
                        slotTiming!!.split("-")[0].split(":")[1].trim().substring(1)
                    )
                )
            else
                calendar.set(
                    Calendar.MINUTE,
                    Integer.parseInt(slotTiming!!.split("-")[0].split(":")[1].trim())
                )
            timeDisplayStr = SimpleDateFormat("hh:mm a").format(calendar.time)

            calendar.set(
                Calendar.HOUR_OF_DAY,
                Integer.parseInt(slotTiming!!.split("-")[1].split(":")[0].trim())
            )
            if (slotTiming!!.split("-")[1].split(":")[1].trim().startsWith("0"))
                calendar.set(
                    Calendar.MINUTE,
                    Integer.parseInt(
                        slotTiming!!.split("-")[1].split(":")[1].trim().substring(
                            1
                        )
                    )
                )
            else
                calendar.set(
                    Calendar.MINUTE,
                    Integer.parseInt(slotTiming!!.split("-")[1].split(":")[1].trim())
                )
            timeDisplayStr = timeDisplayStr + " - " + SimpleDateFormat("hh:mm a").format(calendar.time)
        } catch (e: Exception) {
            timeDisplayStr = slotTiming!!
        }

        return timeDisplayStr
    }

    fun getIsCurrentBooking(appointmentModel: AppointmentModel):Boolean{
        var calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = SimpleDateFormat("dd-MM-yyyy").parse(appointmentModel.slot_date).time
        var slotTiming: String = appointmentModel.slot_timing!!
        calendar.set(
            Calendar.HOUR_OF_DAY,
            Integer.parseInt(slotTiming!!.split("-")[1].split(":")[0].trim())
        )

        if (slotTiming!!.split("-")[1].split(":")[1].trim().startsWith("0"))
            calendar.set(
                Calendar.MINUTE,
                Integer.parseInt(
                    slotTiming!!.split("-")[1].split(":")[1].trim().substring(
                        1
                    )
                )
            )
        else
            calendar.set(
                Calendar.MINUTE,
                Integer.parseInt(slotTiming!!.split("-")[1].split(":")[1].trim())
            )
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)

        return calendar.timeInMillis > Calendar.getInstance().timeInMillis
    }
}