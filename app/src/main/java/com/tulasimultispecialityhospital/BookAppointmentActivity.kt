package com.tulasimultispecialityhospital

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.MenuItem
import android.view.Window
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tulasimultispecialityhospital.adapters.DoctorAppointmentLocationsAdapter
import com.tulasimultispecialityhospital.adapters.AppointmentTimeSlotAdapter
import com.tulasimultispecialityhospital.adapters.DoctorAppointmentTypeAdapter
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.*
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_book_appointment.*
import kotlinx.android.synthetic.main.dialog_conform_booking.*
import kotlinx.android.synthetic.main.dialog_select_location.*
import kotlinx.android.synthetic.main.dialog_select_timeslot.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentActivity : AppCompatActivity(), ConnectionManager.AppointmentTimeSlotsResponseListener,
    ConnectionManager.AppointmentResponseListener {


    private lateinit var doctorLocationsAdapter: DoctorAppointmentLocationsAdapter
    private lateinit var doctorAppointmentTypeAdapter: DoctorAppointmentTypeAdapter
    private lateinit var appointmentTimeSlotSlotAdapter: AppointmentTimeSlotAdapter
    private var myDoctorUtility: MyDoctorUtility? = null
    var doctorModelArray = ArrayList<DoctorModel>()
    val appointmentTypeArrayList = ArrayList<AppointmentTypeModel>()
    var timeSlotArrayList = ArrayList<TimeSlotModel>()
    lateinit var dialog: Dialog
    lateinit var calendar: Calendar
    var clinicId: String? = null
    var clinicLocation: String? = null
    var slotType: String? = null
    var slotTiming: String? = null
    var appointmentType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        myDoctorUtility = MyDoctorUtility.getInstance(this)

        if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.doctor_image)) {
            Picasso.get().load(myDoctorUtility!!.getDoctorData()!!.doctor_image)
                .error(ContextCompat.getDrawable(this, R.mipmap.ic_launcher)!!).into(doctorImage)
        }
        if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.doctor_name)) {
            doctorName.text = myDoctorUtility!!.getDoctorData()!!.doctor_name
        }
        if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.designation)) {
            doctorSpecialization.text = myDoctorUtility!!.getDoctorData()!!.designation
        }

        selectClinic.setOnClickListener {
            doctorModelArray = myDoctorUtility?.getDoctorClinicsData()?.data!!
            showSelectLocationDialog("Select Clinic", doctorModelArray)
        }

        selectAppointmentType.setOnClickListener {
            if (selectClinic.text.toString().equals("Select Clinic", false)) {
                Toast.makeText(this, "Please select clinic location", Toast.LENGTH_SHORT).show()
            } else {
                showSelectAppointmentTypeDialog("Select Appointment Type", appointmentTypeArrayList)
            }
        }
        appointmentMorning.setOnClickListener {
            if (selectAppointmentType.text.toString().equals("Select Appointment Type", false)) {
                Toast.makeText(this, "Please select appointment type", Toast.LENGTH_SHORT).show()
            } else {
                slotType = "1"
                clinicId?.let { it1 ->
                    getTimeSlotsServiceCall(
                        it1,
                        SimpleDateFormat("dd-MM-yyyy").format(calendar.time),
                        "1"
                    )
                }
            }
        }
        appointmentAfternoon.setOnClickListener {
            if (selectAppointmentType.text.toString().equals("Select Appointment Type", false)) {
                Toast.makeText(this, "Please select appointment type", Toast.LENGTH_SHORT).show()
            } else {
                slotType = "2"
                clinicId?.let { it1 ->
                    getTimeSlotsServiceCall(
                        it1,
                        SimpleDateFormat("dd-MM-yyyy").format(calendar.time),
                        "2"
                    )
                }
            }
        }
        appointmentEvening.setOnClickListener {
            if (selectAppointmentType.text.toString().equals("Select Appointment Type", false)) {
                Toast.makeText(this, "Please select appointment type", Toast.LENGTH_SHORT).show()
            } else {
                slotType = "3"
                clinicId?.let { it1 ->
                    getTimeSlotsServiceCall(
                        it1,
                        SimpleDateFormat("dd-MM-yyyy").format(calendar.time),
                        "3"
                    )
                }
            }
        }
        appointmentDate.setOnClickListener {
            showDatePickerDialog()
        }
        calendar = Calendar.getInstance()
        val myFormat = "dd MMM yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat)
        appointmentDate!!.text = "Today | " + sdf.format(Calendar.getInstance().getTime())
        if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.opening_time)
            && !TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.closing_time)
        ) {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < myDoctorUtility!!.getDoctorData()!!.opening_time!!.split(
                    ":"
                )[0].toInt()
                || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > myDoctorUtility!!.getDoctorData()!!.closing_time!!.split(
                    ":"
                )[0].toInt()
            ) {
                appointmentMorning.isEnabled = false
                appointmentAfternoon.isEnabled = false
                appointmentEvening.isEnabled = false
            } else {
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 12) {

                    appointmentMorning.isEnabled = false
                }
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 18) {
                    appointmentAfternoon.isEnabled = false
                }
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 22) {
                    appointmentEvening.isEnabled = false
                }
            }
        } else {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 12) {

                appointmentMorning.isEnabled = false
            }
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 18) {
                appointmentAfternoon.isEnabled = false
            }
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 22) {
                appointmentEvening.isEnabled = false
            }
        }

    }

    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            if (Calendar.getInstance().get(Calendar.YEAR) == year
                && Calendar.getInstance().get(Calendar.MONTH) == monthOfYear
                && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == dayOfMonth
            ) {
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat)
                appointmentDate!!.text = "Today | " + sdf.format(calendar.getTime())
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 12) {
                    appointmentMorning.isEnabled = false
                }
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 18) {
                    appointmentAfternoon.isEnabled = false
                }
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 22) {
                    appointmentEvening.isEnabled = false
                }

            } else if (Calendar.getInstance().get(Calendar.YEAR) == year
                && Calendar.getInstance().get(Calendar.MONTH) == monthOfYear
                && (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1) == dayOfMonth
            ) {
                val myFormat = "dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat)
                appointmentDate!!.text = "Tomorrow | " + sdf.format(calendar.getTime())
                appointmentMorning.isEnabled = true
                appointmentAfternoon.isEnabled = true
                appointmentEvening.isEnabled = true

            } else {
                val myFormat = "EEEE | dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                appointmentDate!!.text = sdf.format(calendar.getTime())
                appointmentMorning.isEnabled = true
                appointmentAfternoon.isEnabled = true
                appointmentEvening.isEnabled = true
            }
        }
    }


    private fun addTypeDataToList() {
        appointmentTypeArrayList.clear()
        appointmentTypeArrayList.add(AppointmentTypeModel("Consultation"))
        appointmentTypeArrayList.add(AppointmentTypeModel("Review"))
        appointmentTypeArrayList.add(AppointmentTypeModel("Followup"))
        appointmentTypeArrayList.add(AppointmentTypeModel("Vaccination"))
    }


    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()
    }


    private fun showSelectLocationDialog(title: String, locationsArrayList: ArrayList<DoctorModel>) {
        dialog = Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_select_location)
        doctorLocationsAdapter = DoctorAppointmentLocationsAdapter(doctorModelArray, this)

        dialog.locationsRecycleView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        dialog.locationsRecycleView.adapter = doctorLocationsAdapter
        doctorLocationsAdapter.notifyDataSetChanged()

        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        var window = dialog.window
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    }

    private fun showSelectAppointmentTypeDialog(
        title: String,
        appointmentTypeArrayList: ArrayList<AppointmentTypeModel>
    ) {
        dialog = Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_select_location)
        doctorAppointmentTypeAdapter = DoctorAppointmentTypeAdapter(appointmentTypeArrayList, this)

        dialog.titleTextView.text = title
        dialog.locationsRecycleView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        dialog.locationsRecycleView.adapter = doctorAppointmentTypeAdapter
        addTypeDataToList()
        doctorAppointmentTypeAdapter.notifyDataSetChanged()

        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        var window = dialog.window
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

    }

    private fun getTimeSlotsServiceCall(clinicId: String, slotDate: String, slotType: String) {
        var generalRequest = GeneralRequest(
            doctor_id = resources.getString(R.string.doctor_id),
            clinic_id = clinicId,
            slot_date = slotDate,
            slot_type = slotType
        )
        myDoctorUtility!!.showProgressDialog(this)
        ConnectionManager.getInstance(this).getAppointmentTimeSlotsData(generalRequest, this)
    }


    private fun showSelectTimeSlotDialog() {
        dialog = Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_select_timeslot)
        appointmentTimeSlotSlotAdapter = AppointmentTimeSlotAdapter(timeSlotArrayList, this)

        dialog.timeSlotsRecycleView.layoutManager = GridLayoutManager(this, 2)
        dialog.timeSlotsRecycleView.adapter = appointmentTimeSlotSlotAdapter
        appointmentTimeSlotSlotAdapter.notifyDataSetChanged()

        dialog.appointmentFee.text = "Rs " + myDoctorUtility!!.getDoctorData()!!.appointment_fee
        dialog.close.setOnClickListener {
            dialog.dismiss()
        }
        dialog.bookAppointmentButton.setOnClickListener {

            slotTiming = ""
            for ((index, timeSlotModel: TimeSlotModel) in timeSlotArrayList.withIndex()) {
                if (timeSlotModel.isTimeSlotSelected) {
                    slotTiming = timeSlotModel.slot_timing
                    break
                }
            }
            if (TextUtils.isEmpty(slotTiming))
                Toast.makeText(this, "Select appointment time", Toast.LENGTH_SHORT).show()
            else {
                dialog.dismiss()
                showConfirmDialog()
            }

        }

        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        var window = dialog.window
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

    }

    private fun showConfirmDialog() {
        dialog = Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_conform_booking)
        dialog.selectedSlotTime.text =
            SimpleDateFormat("dd/MM/yyyy").format(calendar.time) + " | " + formatSelectedTime(slotTiming)

        dialog.confirmCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.confirmText.setOnClickListener {
            dialog.dismiss()

            bookAppointmentServiceCall(
                clinicId, SimpleDateFormat("dd-MM-yyyy").format(calendar.time), slotType, slotTiming,
                appointmentType, myDoctorUtility!!.getUserData().id
            )
        }
        dialog.confirmClose.setOnClickListener {
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

    fun setSelectedLocationToView(locationsModel: DoctorModel) {
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }
        clinicId = locationsModel.clinic_id
        clinicLocation = locationsModel.clinic_location
        selectClinic.text = locationsModel.clinic_location
        selectClinic.setTextColor(ContextCompat.getColor(this, R.color.colorBlack90))
        val directionsDrawable: Drawable = AppCompatResources.getDrawable(this, R.drawable.ic_location_primary)!!
        selectClinic.setCompoundDrawablesWithIntrinsicBounds(null, null, directionsDrawable, null)
        val typeface = ResourcesCompat.getFont(this, R.font.biryani)
        selectClinic.typeface = typeface
    }

    fun setSelectedAppointmentTypeToView(
        appointmentTypeModel: AppointmentTypeModel,
        position: Int
    ) {
        if (dialog != null && dialog.isShowing) {
            dialog.dismiss()
        }
        appointmentType = "" + position
        selectAppointmentType.text = appointmentTypeModel.appointmentType
        selectAppointmentType.setTextColor(ContextCompat.getColor(this, R.color.colorBlack90))
        val directionsDrawable: Drawable = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_right)!!
        selectAppointmentType.setCompoundDrawablesWithIntrinsicBounds(null, null, directionsDrawable, null)
        val typeface = ResourcesCompat.getFont(this, R.font.biryani)
        selectAppointmentType.typeface = typeface
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun bookAppointmentServiceCall(
        clinicId: String?,
        slotDate: String?,
        slotType: String?,
        slotTiming: String?,
        appointmentType: String?,
        userId: String?
    ) {
        var generalRequest = GeneralRequest(
            doctor_id = resources.getString(R.string.doctor_id),
            clinic_id = clinicId,
            slot_date = slotDate,
            slot_type = slotType,
            slot_timing = slotTiming,
            appointment_type = appointmentType,
            user_id = userId
        )
        myDoctorUtility!!.showProgressDialog(this)
        ConnectionManager.getInstance(this).bookAppointmentData(generalRequest, this)
    }

    override fun onAppointmentTimeSlotsResponseSuccess(response: Response<TimeSlotModel>?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.data!!.size > 0) {
                timeSlotArrayList = response.body()!!.data!!
                showSelectTimeSlotDialog()
            }
        } else {
            popAlert(response?.body()!!.msg)
        }
    }

    override fun onAppointmentTimeSlotsResponse(response: TimeSlotModel?) {
    }

    override fun onAppointmentTimeSlotsResponseFailure(message: String?) {
    }


    override fun onAppointmentResponseSuccess(response: Response<AppointmentModel>?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            sendNotification(
                resources.getString(R.string.app_name),
                "Appointment with " + myDoctorUtility!!.getDoctorData()!!.doctor_name
                        + " scheduled for " + SimpleDateFormat("EEE, dd MMM yyyy").format(calendar.time) + formatSelectedTime(
                    slotTiming
                )
                        + " at " + clinicLocation + ". You will be alerted when there are 3 people"
            )
            finish()
        }

    }

    override fun onAppointmentResponse(response: AppointmentModel?) {
    }

    override fun onAppointmentResponseFailure(message: String?) {
    }

    private fun popAlert(msg: String?) {
        try {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_no_internet)
            val titleTextView = dialog.findViewById<AppCompatTextView>(R.id.titleTextView)
            val agePositiveButton = dialog.findViewById<AppCompatButton>(R.id.agePositiveButton)
            titleTextView.text = msg
            titleTextView.setTextColor(ContextCompat.getColor(this, R.color.colorErrorRed))
            agePositiveButton.setText("Okay")
            agePositiveButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            val window = dialog.window
            window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun sendNotification(title: String, data: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.image)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(data))
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "EduAlerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

}
