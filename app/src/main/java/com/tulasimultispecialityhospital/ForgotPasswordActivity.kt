package com.tulasimultispecialityhospital

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
import android.widget.LinearLayout
import android.widget.Toast
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.DoctorModel
import com.tulasimultispecialityhospital.models.GeneralRequest
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_forgot_password.*
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity(), ConnectionManager.DoctorProfileResponseListener {


    private var myDoctorUtility: MyDoctorUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        myDoctorUtility = MyDoctorUtility.getInstance(this)

        if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.doctor_image)){
            Picasso.get().load(myDoctorUtility!!.getDoctorData()!!.doctor_image)
                .error(ContextCompat.getDrawable(this, R.mipmap.ic_launcher)!!).into(doctorImage)
        }
        login_button.setOnClickListener {
            if(TextUtils.isEmpty(mobile_no_edit_text.text))
                Toast.makeText(this, "Mobile no can't be empty", Toast.LENGTH_SHORT).show()
            else{
                var generalRequest = GeneralRequest(
                    user_mobile = mobile_no_edit_text!!.text.toString(),
                    doctor_id = resources.getString(R.string.doctor_id)
                )
                myDoctorUtility!!.showProgressDialog(this)
                ConnectionManager.getInstance(this).postForgotPasswordData(generalRequest, this)
            }

        }


    }


    override fun onDoctorProfileResponseSuccess(response: Response<DoctorModel>?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            popAlert(response.body()!!.msg)

        } else {
            popAlert(response.body()!!.msg)

        }
    }

    override fun onDoctorProfileResponse(response: DoctorModel?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        popAlert(response!!.msg)
    }

    override fun onDoctorProfileResponseFailure(message: String?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        popAlert(message)
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
            titleTextView.setTextColor(ContextCompat.getColor(this,R.color.colorErrorRed))
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

}