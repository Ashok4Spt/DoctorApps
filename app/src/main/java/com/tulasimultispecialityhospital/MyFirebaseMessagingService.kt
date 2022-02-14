package com.tulasimultispecialityhospital

import android.text.TextUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.GeneralRequest
import com.tulasimultispecialityhospital.models.UserModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService(), ConnectionManager.FCMTokenResponseListener {


    private var myDoctorUtility: MyDoctorUtility? = null
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        myDoctorUtility = MyDoctorUtility.getInstance(this)
        myDoctorUtility!!.setFCMToken(token)
        if (!myDoctorUtility!!.isFCMTokenUpdated()!!) {
            if (!TextUtils.isEmpty(myDoctorUtility!!.getUserMobile()))
                sendRegistrationToServer(token)
        }
    }

    private fun sendRegistrationToServer(token: String) {
        var generalRequest = GeneralRequest(
            mobile = myDoctorUtility!!.getUserMobile(),
            doctor_id = resources.getString(R.string.doctor_id),
            device_id = token
        )
        ConnectionManager.getInstance(this).postFCMTokenToServer(generalRequest, this)

    }

    override fun onFCMTokenResponseSuccess(response: Response<UserModel>) {
        if (TextUtils.equals(response?.body()!!.status!!.toLowerCase(), "success")) {
            myDoctorUtility!!.setFCMTokenUpdated(true)
        }
    }

    override fun onFCMTokenResponse(response: UserModel?) {

    }

    override fun onFCMTokenResponseFailure(message: String) {

    }
}