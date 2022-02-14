package com.tulasimultispecialityhospital.util

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import java.util.*

class CalendarHelper {

    private val TAG = "CalendarHelper"
    val CALENDARHELPER_PERMISSION_REQUEST_CODE = 99


    fun MakeNewCalendarEntry(
        caller: Activity,
        title: String,
        description: String,
        startTime: Long,
        endTime: Long,
        allDay: Boolean,
        hasAlarm: Boolean,
        calendarId: Int,
        selectedReminderValue: Int
    ) {

        try {
            val cr = caller.contentResolver
            val values = ContentValues()
            values.put(CalendarContract.Events.DTSTART, startTime)
            values.put(CalendarContract.Events.DTEND, endTime)
            values.put(CalendarContract.Events.TITLE, title)
            values.put(CalendarContract.Events.DESCRIPTION, description)
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId)
            values.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED)

            if (allDay) {
                values.put(CalendarContract.Events.ALL_DAY, true)
            }
            if (hasAlarm) {
                values.put(CalendarContract.Events.HAS_ALARM, true)
            }

            //Get current timezone
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            Log.i(TAG, "Timezone retrieved=>" + TimeZone.getDefault().id)
            val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values)
            Log.i(TAG, "Uri returned=>" + uri!!.toString())
            // get the event ID that is the last element in the Uri
            val eventID = java.lang.Long.parseLong(uri.lastPathSegment!!)

            if (hasAlarm) {
                val reminders = ContentValues()
                reminders.put(CalendarContract.Reminders.EVENT_ID, eventID)
                reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                reminders.put(CalendarContract.Reminders.MINUTES, selectedReminderValue)

                val uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun requestCalendarReadWritePermission(caller: Activity) {
        val permissionList = ArrayList<String>()

        if (ContextCompat.checkSelfPermission(
                caller,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.WRITE_CALENDAR)

        }

        if (ContextCompat.checkSelfPermission(
                caller,
                Manifest.permission.READ_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.READ_CALENDAR)

        }

        if (permissionList.size > 0) {
            val permissionArray = arrayOfNulls<String>(permissionList.size)

            for (i in permissionList.indices) {
                permissionArray[i] = permissionList[i]
            }

            ActivityCompat.requestPermissions(
                caller,
                permissionArray,
                CALENDARHELPER_PERMISSION_REQUEST_CODE
            )
        }

    }

    fun listCalendarId(c: Context): Hashtable<String, String>? {

        if (haveCalendarReadWritePermissions(c as Activity)) {

            val projection = arrayOf("_id", "calendar_displayName")
            val calendars: Uri
            calendars = Uri.parse("content://com.android.calendar/calendars")

            val contentResolver = c.getContentResolver()
            val managedCursor = contentResolver.query(calendars, projection, null, null, null)

            if (managedCursor!!.moveToFirst()) {
                var calName: String
                var calID: String
                var cont = 0
                val nameCol = managedCursor.getColumnIndex(projection[1])
                val idCol = managedCursor.getColumnIndex(projection[0])
                val calendarIdTable = Hashtable<String, String>()

                do {
                    calName = managedCursor.getString(nameCol)
                    calID = managedCursor.getString(idCol)
                    Log.v(TAG, "CalendarName:$calName ,id:$calID")
                    calendarIdTable[calName] = calID
                    cont++
                } while (managedCursor.moveToNext())
                managedCursor.close()

                return calendarIdTable
            }

        }

        return null

    }

    fun haveCalendarReadWritePermissions(caller: Activity): Boolean {
        var permissionCheck = ContextCompat.checkSelfPermission(
            caller,
            Manifest.permission.READ_CALENDAR
        )

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            permissionCheck = ContextCompat.checkSelfPermission(
                caller,
                Manifest.permission.WRITE_CALENDAR
            )

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                permissionCheck = ContextCompat.checkSelfPermission(
                    caller,
                    Manifest.permission.GET_ACCOUNTS
                )
                if (permissionCheck == PackageManager.PERMISSION_GRANTED){
                    return true
                }

            }
        }

        return false
    }

}