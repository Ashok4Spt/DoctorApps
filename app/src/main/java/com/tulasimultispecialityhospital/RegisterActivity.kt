package com.tulasimultispecialityhospital

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import android.text.TextUtils
import android.view.Window
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.GeneralRequest
import com.tulasimultispecialityhospital.models.UserModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity(), ConnectionManager.LoginResponseListener {
    lateinit var calendar: Calendar
    private var myDoctorUtility: MyDoctorUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        calendar = Calendar.getInstance()
        myDoctorUtility = MyDoctorUtility.getInstance(this)

        if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.doctor_image)){
            Picasso.get().load(myDoctorUtility!!.getDoctorData()!!.doctor_image)
                .error(ContextCompat.getDrawable(this, R.mipmap.ic_launcher)!!).into(doctorImage)
        }
        if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.doctor_name)) {
            doctorName.text=myDoctorUtility!!.getDoctorData()!!.doctor_name
        }
        usr_dob_edit_text.setOnClickListener {
            calendar = Calendar.getInstance()
            showFromDatePickerDialog(fromDateSetListener)
        }

        register_button.setOnClickListener() {
            if (TextUtils.isEmpty(usr_name_text_input_layout.editText!!.text.toString())) {
                Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(usr_email_text_input_layout.editText!!.text.toString())) {
                Toast.makeText(this, "Email can't be empty", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(usr_mobile_text_input_layout.editText!!.text.toString())) {
                Toast.makeText(this, "Mobile no can't be empty", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(password_text_input_layout.editText!!.text.toString())) {
                Toast.makeText(this, "Password can't be empty", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(usr_dob_text_input_layout.editText!!.text.toString())) {
                Toast.makeText(this, "Date of birth can't be empty", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(usr_city_text_input_layout.editText!!.text.toString())) {
                Toast.makeText(this, "City can't be empty", Toast.LENGTH_SHORT).show()
            } else {
                var genderString:String="0"
                if(femaleRadioButton.isChecked)
                    genderString="1"

                var generalRequest = GeneralRequest(
                    user_fullname = usr_name_text_input_layout.editText!!.text.toString(),
                    user_email = usr_email_text_input_layout.editText!!.text.toString(),
                    user_mobile = usr_mobile_text_input_layout.editText!!.text.toString(),
                    user_pwd = password_text_input_layout.editText!!.text.toString(),
                    user_gender = genderString,
                    user_dob = SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime()),
                    user_city = usr_city_text_input_layout.editText!!.text.toString(),
                    doctor_id = resources.getString(R.string.doctor_id)
                )
                myDoctorUtility!!.showProgressDialog(this)
                ConnectionManager.getInstance(this).postRegistrationData(generalRequest, this)
            }

        }

    }
    private fun showFromDatePickerDialog(fromDateSetListener: DatePickerDialog.OnDateSetListener) {
        val datePickerDialog = DatePickerDialog(
            this,
            fromDateSetListener,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        datePickerDialog.show()
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

            var myFormat = "dd - MM - yyyy" // mention the format you need
            var sdf = SimpleDateFormat(myFormat)
            usr_dob_text_input_layout.editText!!.setText(sdf.format(calendar.getTime()))


        }
    }

    override fun onLoginResponseSuccess(response: Response<UserModel>?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            popAlert(response.body()!!.msg, true)

        } else {
            popAlert(response.body()!!.msg, false)

        }
    }

    override fun onLoginResponse(response: UserModel?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        popAlert(response!!.msg, false)
    }

    override fun onLoginResponseFailure(message: String?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        popAlert(message, false)
    }

    private fun popAlert(msg: String?, isActivityFinish: Boolean) {
        try {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_no_internet)
            val titleTextView = dialog.findViewById<AppCompatTextView>(R.id.titleTextView)
            val agePositiveButton = dialog.findViewById<AppCompatButton>(R.id.agePositiveButton)
            titleTextView.text = msg
            titleTextView.setTextColor(ContextCompat.getColor(this,R.color.colorErrorRed))
            agePositiveButton.setText("Okay")
            agePositiveButton.setOnClickListener {
                dialog.dismiss()
                if (isActivityFinish)
                    finish()
            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            val window = dialog.window
            window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}