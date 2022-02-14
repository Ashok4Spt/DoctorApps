package com.tulasimultispecialityhospital.util

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.os.Environment
import androidx.appcompat.app.AppCompatDialog
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import com.google.gson.Gson
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.DoctorModel
import com.tulasimultispecialityhospital.models.UserModel
import java.util.*
import java.io.File


class MyDoctorUtility private constructor(context: Context) {


    companion object {
        private var myDoctorUtility: MyDoctorUtility? = null
        private var context: Context? = null
        private var progressdialog: AppCompatDialog? = null
        fun getInstance(ctx: Context): MyDoctorUtility {
            context = ctx
            if (myDoctorUtility == null)
                myDoctorUtility = MyDoctorUtility(ctx)

            return myDoctorUtility as MyDoctorUtility
        }

    }


//    companion object {
//        private var context: Context? = null
//        private var myDoctorUtility: MyDoctorUtility? = null
//        private var progressdialog: AppCompatDialog? = null
//    }


    fun getUserMobile(): String? {
        val preferences =
            context!!.getSharedPreferences(context!!.getResources().getString(R.string.app_name), Context.MODE_PRIVATE)
        return preferences.getString("UserMobile", "")
    }

    fun setUserMobile(value: String) {
        val preferences =
            context!!.getSharedPreferences(context!!.getResources().getString(R.string.app_name), Context.MODE_PRIVATE)
        val prefsEditor = preferences.edit()
        prefsEditor.putString("UserMobile", value)
        prefsEditor.apply()
    }

    fun getFCMToken(): String? {
        val preferences =
            context!!.getSharedPreferences(context!!.getResources().getString(R.string.app_name), Context.MODE_PRIVATE)
        return preferences.getString("fcm_token", "")
    }

    fun setFCMToken(value: String) {
        val preferences =
            context!!.getSharedPreferences(context!!.getResources().getString(R.string.app_name), Context.MODE_PRIVATE)
        val prefsEditor = preferences.edit()
        prefsEditor.putString("fcm_token", value)
        prefsEditor.apply()
    }

    fun isFCMTokenUpdated(): Boolean? {
        val preferences =
            context!!.getSharedPreferences(context!!.getResources().getString(R.string.app_name), Context.MODE_PRIVATE)
        return preferences.getBoolean("isTokenUpdated",false)
    }

    fun setFCMTokenUpdated(value: Boolean) {
        val preferences =
            context!!.getSharedPreferences(context!!.getResources().getString(R.string.app_name), Context.MODE_PRIVATE)
        val prefsEditor = preferences.edit()
        prefsEditor.putBoolean("isTokenUpdated", value)
        prefsEditor.apply()
    }

    fun getUserPassword(): String? {
        val preferences =
            context!!.getSharedPreferences(context!!.getResources().getString(R.string.app_name), Context.MODE_PRIVATE)
        return preferences.getString("UserPassword", "")
    }

    fun setUserPassword(value: String) {
        val preferences =
            context!!.getSharedPreferences(context!!.getResources().getString(R.string.app_name), Context.MODE_PRIVATE)
        val prefsEditor = preferences.edit()
        prefsEditor.putString("UserPassword", value)
        prefsEditor.apply()
    }

    fun setUserData(userModel: UserModel) {
        val gson = Gson()
        val json = gson.toJson(userModel)
        val preferences =
            context!!.getSharedPreferences(context!!.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        val prefsEditor = preferences.edit()
        prefsEditor.putString("user", json)
        prefsEditor.apply()
    }

    fun getUserData(): UserModel {
        val preferences =
            context!!.getSharedPreferences(context!!.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString("user", "")
        return gson.fromJson<UserModel>(json, UserModel::class.java)
    }

    fun setDoctorData(doctorModel: DoctorModel) {
        val gson = Gson()
        val json = gson.toJson(doctorModel)
        val preferences =
            context!!.getSharedPreferences(context!!.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        val prefsEditor = preferences.edit()
        prefsEditor.putString("doctor", json)
        prefsEditor.apply()
    }

    fun getDoctorData(): DoctorModel? {
        val preferences =
            context!!.getSharedPreferences(context!!.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        val gson = Gson()
        if (!TextUtils.isEmpty(preferences.getString("doctor", ""))) {
            val json = preferences.getString("doctor", "")
            return gson.fromJson<DoctorModel>(json, DoctorModel::class.java)
        } else return null
    }

    fun setDoctorClinicsData(doctorModel: DoctorModel) {
        val gson = Gson()
        val json = gson.toJson(doctorModel)
        val preferences =
            context!!.getSharedPreferences(context!!.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        val prefsEditor = preferences.edit()
        prefsEditor.putString("Clinics", json)
        prefsEditor.apply()
    }

    fun getDoctorClinicsData(): DoctorModel {
        try {
            val preferences = context!!.getSharedPreferences(context!!.resources.getString(R.string.app_name), Context.MODE_PRIVATE)
            val gson = Gson()
            val json = preferences.getString("Clinics", "")
            return gson.fromJson<DoctorModel>(json, DoctorModel::class.java)
        } catch (e: Exception) {
            return DoctorModel()
        }
    }

    fun showProgressDialog(act: Activity) {
        Log.d("Progress", "showProgressDialog")
        try {
            if (progressdialog != null)
                if (progressdialog!!.isShowing())
                    progressdialog!!.dismiss()
            progressdialog = null
            if (progressdialog == null)
                progressdialog = AppCompatDialog(act)
            progressdialog!!.setContentView(R.layout.dialog_progress_custom)

            /*  AnimatedGifImageView pGif = (AnimatedGifImageView) progressdialog.findViewById(R.id.viewGif);
        pGif.setAnimatedGif(R.raw.anim_loading, AnimatedGifImageView.TYPE.FIT_CENTER);*/
            progressdialog!!.setCancelable(false)
            //  progressdialog.setProgress(0);
            //  progressdialog.setProgressStyle(R.style.AppTheme_NoActionBar);
            //  progressdialog.setIndeterminate(false);
            //  if (!(act).isFinishing())
            progressdialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //dismiss progress dialog
    fun dismissProgressDialog(activity: Activity) {
        Log.d("Progress", "dismissProgressDialog")
        try {
            if (progressdialog != null)
                if (progressdialog!!.isShowing())
                // if (!activity.isFinishing())
                    progressdialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        progressdialog = null
    }

    fun addNewEvent(context: Context, date: Date, description: String) {
        try {
            val calendarIdTable = CalendarHelper().listCalendarId(context) ?: return
            val oneHour = (1000 * 60 * 60).toLong()

            val SampleArrayList = ArrayList<String>()
            val pattern = Patterns.EMAIL_ADDRESS
            try {
                val account = AccountManager.get(context).accounts
                for (TempAccount in account) {
                    if (pattern.matcher(TempAccount.name).matches())
                        if (TempAccount.name.toLowerCase().contains("@gmail.com"))
                            SampleArrayList.add(TempAccount.name)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            var calendar_id = 0
            if (calendarIdTable.containsValue("1")) {
                calendar_id = 1
            } else {
                for (i in SampleArrayList.indices) {
                    calendar_id = Integer.parseInt(calendarIdTable[SampleArrayList[i]]!!)
                    if (calendar_id > 0)
                        break
                }
            }

            if (calendar_id > 0) {
                // int calendar_id = Integer.parseInt(calendarIdTable.get(key));
                CalendarHelper().MakeNewCalendarEntry(
                    context as Activity,
                    "Medicine Reminder",
                    description,
                    date.time,
                    date.time,
                    false,
                    true,
                    calendar_id,
                    0
                )

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun getFilename(context: Context): String {
        val file = File(
            Environment.getExternalStorageDirectory().path,
            context.resources.getString(R.string.app_name) + "/Reports"
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".png"

    }

    fun getFilesPath(context: Context?): String {
        val file = File(
            Environment.getExternalStorageDirectory().path,
            context!!.resources.getString(R.string.app_name) + "/Reports"
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/"

    }
}