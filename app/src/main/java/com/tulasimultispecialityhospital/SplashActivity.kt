package com.tulasimultispecialityhospital

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import android.text.TextUtils
import android.view.Window
import android.widget.LinearLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.DoctorModel
import com.tulasimultispecialityhospital.models.GeneralRequest
import com.tulasimultispecialityhospital.models.UserModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_splash.*
import retrofit2.Response

class SplashActivity : AppCompatActivity(), ConnectionManager.LoginResponseListener,
    ConnectionManager.DoctorProfileResponseListener {
    private var handler: Handler? = null;
    private var myDoctorUtility: MyDoctorUtility? = null
    internal var runnable: Runnable = Runnable {

        if (!TextUtils.isEmpty(myDoctorUtility!!.getUserMobile())) {
            var generalRequest = GeneralRequest(
                mobile = myDoctorUtility!!.getUserMobile(),
                password = myDoctorUtility!!.getUserPassword(),
                doctor_id = resources.getString(R.string.doctor_id)
            )
            ConnectionManager.getInstance(this).getLoginData(generalRequest, this)
        } else {
            requestDoctorProfileServiceCall()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        myDoctorUtility = MyDoctorUtility.getInstance(this)
        handler = Handler();
        if (myDoctorUtility!!.getDoctorData() != null) {
            if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.doctor_image)) {
                Picasso.get().load(myDoctorUtility!!.getDoctorData()!!.doctor_image)
                    .error(ContextCompat.getDrawable(this, R.mipmap.ic_launcher)!!).into(doctorImage)
            }
            if (!TextUtils.isEmpty(myDoctorUtility!!.getDoctorData()!!.doctor_name)) {
                doctorName.text=myDoctorUtility!!.getDoctorData()!!.doctor_name
            }

        }
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {

                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token
                myDoctorUtility!!.setFCMToken(token!!)
            })



        handler!!.postDelayed(runnable, 1000)
    }


    public override fun onDestroy() {

        if (handler != null) {
            handler!!.removeCallbacks(runnable)
        }

        super.onDestroy()
    }


    private fun requestDoctorProfileServiceCall() {
        var generalRequest = GeneralRequest(
            doctor_id = resources.getString(R.string.doctor_id)
        )
        ConnectionManager.getInstance(this).getDoctorProfileData(generalRequest, this)
    }

    override fun onLoginResponseSuccess(response: Response<UserModel>?) {
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.user!!.size > 0) {
                myDoctorUtility!!.setUserData(response.body()!!.user!![0])
                requestDoctorProfileServiceCall()
            } else {
                myDoctorUtility!!.dismissProgressDialog(this)
                popAlert(response.body()!!.msg, false)
            }
        } else {
            myDoctorUtility!!.dismissProgressDialog(this)
            popAlert(response.body()!!.msg, false)
        }
    }


    override fun onLoginResponse(response: UserModel?) {
        popAlert(response!!.msg, false)
    }

    override fun onLoginResponseFailure(message: String?) {
        popAlert(message, false)
    }

    override fun onDoctorProfileResponseSuccess(response: Response<DoctorModel>?) {
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.user!!.size > 0) {
                myDoctorUtility!!.setDoctorData(response.body()!!.user!![0])
                if (TextUtils.isEmpty(myDoctorUtility!!.getUserMobile())) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                //   eduAlertsUtility.dismissProgressDialog(this@LoginActivity)
                popAlert(response.body()!!.msg, false)
            }
        } else {
            //  eduAlertsUtility.dismissProgressDialog(this@LoginActivity)
            popAlert(response.body()!!.msg, false)
        }
    }

    override fun onDoctorProfileResponse(response: DoctorModel?) {
        popAlert(response!!.msg, false)
    }

    override fun onDoctorProfileResponseFailure(message: String?) {
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
            titleTextView.setTextColor(ContextCompat.getColor(this, R.color.colorErrorRed))
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