package com.tulasimultispecialityhospital

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import android.text.Html
import android.text.TextUtils
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.DoctorModel
import com.tulasimultispecialityhospital.models.GeneralRequest
import com.tulasimultispecialityhospital.models.UserModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Response

class LoginActivity : AppCompatActivity(), ConnectionManager.LoginResponseListener,
    ConnectionManager.DoctorProfileResponseListener, ConnectionManager.FCMTokenResponseListener {



    private var myDoctorUtility: MyDoctorUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        myDoctorUtility = MyDoctorUtility.getInstance(this)
        if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.doctor_image)){
            Picasso.get().load(myDoctorUtility!!.getDoctorData()!!.doctor_image)
                .error(ContextCompat.getDrawable(this, R.mipmap.ic_launcher)!!).into(doctorImage)
        }
        if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.doctor_name)) {
            doctorName.text=myDoctorUtility!!.getDoctorData()!!.doctor_name
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            (findViewById<AppCompatTextView>(R.id.signup_here_text)).text =
                Html.fromHtml(getResources().getString(R.string.new_register_here), Html.FROM_HTML_MODE_COMPACT)
        else
            (findViewById<AppCompatTextView>(R.id.signup_here_text)).text =
                getResources().getString(R.string.new_register_here)



        login_button.setOnClickListener() {

            if (TextUtils.isEmpty(usr_name_edit_text.text.toString())) {
                Toast.makeText(this, "Mobile no can't be empty", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(password_edit_text.text.toString())) {
                Toast.makeText(this, "Password can't be empty", Toast.LENGTH_SHORT).show()
            } else {
                var generalRequest = GeneralRequest(
                    mobile = usr_name_edit_text.text.toString(),
                    password = password_edit_text.text.toString(),
                    doctor_id = resources.getString(R.string.doctor_id)
                )
                myDoctorUtility!!.showProgressDialog(this)
                ConnectionManager.getInstance(this).getLoginData(generalRequest, this)
            }
        }

        signup_here_text.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)

        }

    }

    private fun requestDoctorProfileServiceCall() {
        var generalRequest = GeneralRequest(
            doctor_id = resources.getString(R.string.doctor_id)
        )
        ConnectionManager.getInstance(this).getDoctorProfileData(generalRequest, this)
    }
    private fun sendRegistrationToServer() {
        var generalRequest = GeneralRequest(
            mobile = myDoctorUtility!!.getUserMobile(),
            doctor_id = resources.getString(R.string.doctor_id),
            device_id = myDoctorUtility!!.getFCMToken()
        )
        ConnectionManager.getInstance(this).postFCMTokenToServer(generalRequest, this)

    }
    override fun onLoginResponseSuccess(response: Response<UserModel>?) {
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.user!!.size > 0) {
                myDoctorUtility!!.setUserMobile(usr_name_text_input_layout.editText!!.text.toString().trim())
                myDoctorUtility!!.setUserPassword(password_text_input_layout.editText!!.text.toString().trim())
                myDoctorUtility!!.setUserData(response.body()!!.user!![0])
                if(!TextUtils.isEmpty(myDoctorUtility!!.getFCMToken()))
                sendRegistrationToServer()
                requestDoctorProfileServiceCall()
            } else {
                myDoctorUtility!!.dismissProgressDialog(this)
                   popAlert(response.body()!!.msg,false)
            }
        } else {
            myDoctorUtility!!.dismissProgressDialog(this)
            popAlert(response.body()!!.msg,false)
        }
    }


    override fun onLoginResponse(response: UserModel?) {
        popAlert(response!!.msg,false)
    }

    override fun onLoginResponseFailure(message: String?) {
        popAlert(message,false)
    }

    override fun onDoctorProfileResponseSuccess(response: Response<DoctorModel>?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.user!!.size > 0) {
                myDoctorUtility!!.setDoctorData(response.body()!!.user!![0])
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                //   eduAlertsUtility.dismissProgressDialog(this@LoginActivity)
                popAlert(response.body()!!.msg,false)
            }
        } else {
            //  eduAlertsUtility.dismissProgressDialog(this@LoginActivity)
            popAlert(response.body()!!.msg,false)
        }
    }

    override fun onDoctorProfileResponse(response: DoctorModel?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        popAlert(response!!.msg,false)
    }

    override fun onDoctorProfileResponseFailure(message: String?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        popAlert(message,false)
    }


    override fun onFCMTokenResponseSuccess(response: Response<UserModel>) {
        if (TextUtils.equals(response?.body()!!.status!!.toLowerCase(), "success")) {
            myDoctorUtility!!.setFCMTokenUpdated(true)
        }
    }

    override fun onFCMTokenResponse(response: UserModel?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFCMTokenResponseFailure(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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