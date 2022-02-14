package com.tulasimultispecialityhospital

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.tulasimultispecialityhospital.adapters.MedicineRemainderAddTimeAdapter
import com.tulasimultispecialityhospital.adapters.SpinnerDropDownAdapter
import com.tulasimultispecialityhospital.database.MyDoctorDataBase
import com.tulasimultispecialityhospital.models.MedicineRemainderModel
import com.tulasimultispecialityhospital.util.CalendarHelper
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import kotlinx.android.synthetic.main.activity_add_medicine_remainder.*
import kotlinx.android.synthetic.main.activity_book_appointment.toolbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddMedicineRemainderActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var medicineRemainderAddTimeAdapter: MedicineRemainderAddTimeAdapter? = null
    lateinit var dialog: Dialog
    lateinit var calendar: Calendar
    lateinit var toCalendar: Calendar
    var intakeTypeArrayList = ArrayList<String>();
    var dosageArrayList = ArrayList<String>();
    var whenToTakeArrayList = ArrayList<String>();
    var strengthArrayList = ArrayList<String>();
    var medicineRemindTimeArrayList = ArrayList<MedicineRemainderModel>();
    private var myDoctorUtility: MyDoctorUtility? = null
    var MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine_remainder)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        myDoctorUtility = MyDoctorUtility.getInstance(this)
        intakeTypeArrayList = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.IntakeType)))
        dosageArrayList = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.DosageType)))
        whenToTakeArrayList = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.WhenToTake)))
        strengthArrayList = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.Strength)))

        var intakeTypeAdapter: SpinnerDropDownAdapter = SpinnerDropDownAdapter(this!!, intakeTypeArrayList, true)
        var dosageAdapter: SpinnerDropDownAdapter = SpinnerDropDownAdapter(this!!, dosageArrayList, true)
        var whenToTakeAdapter: SpinnerDropDownAdapter = SpinnerDropDownAdapter(this!!, whenToTakeArrayList, true)
        var strengthAdapter: SpinnerDropDownAdapter = SpinnerDropDownAdapter(this!!, strengthArrayList, false)

        intakeTypeSpinner.adapter = intakeTypeAdapter
        dosageTypeSpinner.adapter = dosageAdapter
        whenToTakeSpinner.adapter = whenToTakeAdapter
        strengthTypeSpinner.adapter = strengthAdapter

        timeRecycleView!!.layoutManager =
            LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        medicineRemainderAddTimeAdapter = MedicineRemainderAddTimeAdapter(medicineRemindTimeArrayList, this)
        timeRecycleView.adapter = medicineRemainderAddTimeAdapter

        fromDateLayout.setOnClickListener {
            calendar = Calendar.getInstance()
            showFromDatePickerDialog(fromDateSetListener)
        }
        toDateLayout.setOnClickListener {
            showToDatePickerDialog(toDateSetListener)
        }
        addTimeImageView.setOnClickListener {
            showTimePickerDialog()
        }

        saveReminderButton.setOnClickListener {
            if (TextUtils.isEmpty(nameOfMedicine.text.toString()))
                Toast.makeText(this, "Enter medicine name", Toast.LENGTH_SHORT).show()
            else if (intakeTypeSpinner.selectedItemPosition == 0)
                Toast.makeText(this, "Select intake type", Toast.LENGTH_SHORT).show()
            else if (dosageTypeSpinner.selectedItemPosition == 0)
                Toast.makeText(this, "Select dosage type", Toast.LENGTH_SHORT).show()
            else if (whenToTakeSpinner.selectedItemPosition == 0)
                Toast.makeText(this, "Select when to take", Toast.LENGTH_SHORT).show()
            else if (TextUtils.isEmpty(strength.text.toString()))
                Toast.makeText(this, "Enter strength", Toast.LENGTH_SHORT).show()
            else if (TextUtils.equals(fromDayTextView.text.toString(), "-"))
                Toast.makeText(this, "Select start date", Toast.LENGTH_SHORT).show()
            else if (TextUtils.equals(toDayTextView.text.toString(), "-"))
                Toast.makeText(this, "Select end date", Toast.LENGTH_SHORT).show()
            else if (medicineRemindTimeArrayList.size == 0)
                Toast.makeText(this, "Select medicine time", Toast.LENGTH_SHORT).show()
            else {

                if (CalendarHelper().haveCalendarReadWritePermissions(this)) {

                    val dbHandler = MyDoctorDataBase(this)
                    val cal1 = calendar;
                    val cal2 = toCalendar;

                    var remindDates = getDates(cal1, cal2)

                    for ((index, date: Date) in remindDates.withIndex()) {
                        var remindCalendar = Calendar.getInstance()
                        remindCalendar.time = date

                        for ((index, medicineRemainderModel: MedicineRemainderModel) in medicineRemindTimeArrayList.withIndex()) {
//                        var remindTimeCalendar = Calendar.getInstance()
//                        remindTimeCalendar.set(
//                            Calendar.YEAR, remindCalendar.get(Calendar.YEAR),
//                            Calendar.MONTH, remindCalendar.get(Calendar.MONTH),
//                            Calendar.DAY_OF_MONTH, remindCalendar.get(Calendar.DAY_OF_MONTH)
//                        )
//                        remindTimeCalendar.timeInMillis = medicineRemainderModel.remindTime!!

                            var medicineRemainderModel = MedicineRemainderModel(
                                medicineName = nameOfMedicine.text.toString(),
                                inTakeType = intakeTypeArrayList[intakeTypeSpinner.selectedItemPosition],
                                dosage = dosageArrayList[dosageTypeSpinner.selectedItemPosition],
                                whenToTake = whenToTakeArrayList[whenToTakeSpinner.selectedItemPosition],
                                strength = strength.text.toString(),
                                strengthUnit = strengthArrayList[strengthTypeSpinner.selectedItemPosition],
                                startDate = calendar.timeInMillis,
                                endDate = toCalendar.timeInMillis,
                                remindDate = remindCalendar.timeInMillis,
                                remindTime = medicineRemainderModel.remindTime,
                                remarks = remarks.text.toString()
                            )
                            var rowId = dbHandler.insertRemainder(medicineRemainderModel)

                            if (rowId != null) {
                                addRemainder(medicineRemainderModel)
                            }
                        }
                    }
                    Toast.makeText(this, "Remainder Added", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.READ_CALENDAR,
                            Manifest.permission.WRITE_CALENDAR
                        ),
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS
                    )
                }
            }
        }
        addListners()
    }

    fun addListners() {
        intakeTypeSpinner.setOnItemSelectedListener(this)
        dosageTypeSpinner.setOnItemSelectedListener(this)
        whenToTakeSpinner.setOnItemSelectedListener(this)
        strengthTypeSpinner.setOnItemSelectedListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveReminderButton.performClick()
                } else {
                    Toast.makeText(this, "Permissions required to add reminder", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

  private  val fromDateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            var myFormat = "dd" // mention the format you need
            var sdf = SimpleDateFormat(myFormat)
            fromDayTextView!!.text = sdf.format(calendar.getTime())

            myFormat = "MMM" // mention the format you need
            sdf = SimpleDateFormat(myFormat)
            fromMonthTextView!!.text = sdf.format(calendar.getTime())

            myFormat = "EEEE" // mention the format you need
            sdf = SimpleDateFormat(myFormat, Locale.US)
            fromWeekTextView!!.text = sdf.format(calendar.getTime())

        }
    }
    private val toDateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            toCalendar = Calendar.getInstance()
            toCalendar.set(Calendar.YEAR, year)
            toCalendar.set(Calendar.MONTH, monthOfYear)
            toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            toCalendar.set(Calendar.HOUR_OF_DAY, 23)
            toCalendar.set(Calendar.MINUTE, 59)
            toCalendar.set(Calendar.SECOND, 59)
            toCalendar.set(Calendar.MILLISECOND, 999)
            var myFormat = "dd" // mention the format you need
            var sdf = SimpleDateFormat(myFormat)
            toDayTextView!!.text = sdf.format(toCalendar.getTime())

            myFormat = "MMM" // mention the format you need
            sdf = SimpleDateFormat(myFormat)
            toMonthTextView!!.text = sdf.format(toCalendar.getTime())

            myFormat = "EEEE" // mention the format you need
            sdf = SimpleDateFormat(myFormat, Locale.US)
            toWeekTextView!!.text = sdf.format(toCalendar.getTime())

        }
    }


    private fun showFromDatePickerDialog(fromDateSetListener: DatePickerDialog.OnDateSetListener) {
        val datePickerDialog = DatePickerDialog(
            this,
            fromDateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()
    }

    private fun showToDatePickerDialog(fromDateSetListener: DatePickerDialog.OnDateSetListener) {
        val datePickerDialog = DatePickerDialog(
            this,
            fromDateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val c = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            c.set(Calendar.HOUR_OF_DAY, hour)
            c.set(Calendar.MINUTE, minute)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            val medicineRemainderModel = MedicineRemainderModel();
            medicineRemainderModel.remindTime = c.timeInMillis
            medicineRemindTimeArrayList.add(medicineRemainderModel)
            medicineRemainderAddTimeAdapter?.notifyDataSetChanged()
            //textView.text = SimpleDateFormat("HH:mm").format(cal.time)
        }

        val timePickerDialog = TimePickerDialog(
            this,
            timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false
        )
        timePickerDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        if (p1?.id == R.id.intakeTypeSpinner) {

        }
        if (p1?.id == R.id.dosageTypeSpinner) {

        }
        if (p1?.id == R.id.whenToTakeSpinner) {

        }
        if (p1?.id == R.id.strengthTypeSpinner) {

        }
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

    private fun getDates(cal1: Calendar, cal2: Calendar): ArrayList<Date> {
        val dates = ArrayList<Date>()

        while (!cal1.after(cal2)) {
            dates.add(cal1.time)
            cal1.add(Calendar.DATE, 1)
        }
        return dates
    }

    fun addRemainder(medicineRemainderModel: MedicineRemainderModel) {

        var calendar = Calendar.getInstance()
        calendar.timeInMillis = medicineRemainderModel.remindDate!!
        var timeCalender = Calendar.getInstance()
        timeCalender.timeInMillis = medicineRemainderModel.remindTime!!
        calendar.set(Calendar.HOUR_OF_DAY, timeCalender.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, timeCalender.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        myDoctorUtility!!.addNewEvent(
            this,
            calendar.time,
            medicineRemainderModel.medicineName + " " + medicineRemainderModel.strength + medicineRemainderModel.strengthUnit + " " + medicineRemainderModel.dosage + " " + medicineRemainderModel.whenToTake

        )

    }

}


