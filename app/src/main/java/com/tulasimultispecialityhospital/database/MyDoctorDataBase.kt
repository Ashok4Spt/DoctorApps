package com.tulasimultispecialityhospital.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.tulasimultispecialityhospital.models.MedicineRemainderModel


class MyDoctorDataBase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_MEDICINE_REMAINDER_TABLE = ("CREATE TABLE " + TABLE_MEDICINE_REMAINDER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_MEDICINENAME + " TEXT,"
                + KEY_INTAKE + " TEXT,"
                + KEY_DOSAGE_TYPE + " TEXT,"
                + KEY_WHEN_TO_TAKE + " TEXT,"
                + KEY_STRENGTH + " TEXT,"
                + KEY_STRENGTH_UNIT + " TEXT,"
                + KEY_START_FROM + " INTEGER,"
                + KEY_END_ON + " INTEGER,"
                + KEY_REMIND_ON + " INTEGER,"
                + KEY_REMARKS + " TEXT,"
                + KEY_REMIND_TIME + " INTEGER" + ")")
        db?.execSQL(CREATE_MEDICINE_REMAINDER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINE_REMAINDER)
        onCreate(db)
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "MyDoctorDatabase"
        private val TABLE_MEDICINE_REMAINDER = "MedicineRemainderTable"
        private val KEY_ID = "id"
        private val KEY_MEDICINENAME = "medicine_name"
        private val KEY_INTAKE = "intake"
        private val KEY_DOSAGE_TYPE = "dosage"
        private val KEY_WHEN_TO_TAKE = "when_to_take"
        private val KEY_STRENGTH = "strength"
        private val KEY_STRENGTH_UNIT = "strength_unit"
        private val KEY_START_FROM = "start_from"
        private val KEY_END_ON = "end_on"
        private val KEY_REMIND_ON = "remind_on"
        private val KEY_REMIND_TIME = "remind_time"
        private val KEY_REMARKS = "remarks"
    }


    //method to insert data
    fun insertRemainder(medicineRemainderModel: MedicineRemainderModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_MEDICINENAME, medicineRemainderModel.medicineName)
        contentValues.put(KEY_INTAKE, medicineRemainderModel.inTakeType)
        contentValues.put(KEY_DOSAGE_TYPE, medicineRemainderModel.dosage)
        contentValues.put(KEY_WHEN_TO_TAKE, medicineRemainderModel.whenToTake)
        contentValues.put(KEY_STRENGTH, medicineRemainderModel.strength)
        contentValues.put(KEY_STRENGTH_UNIT, medicineRemainderModel.strengthUnit)
        contentValues.put(KEY_START_FROM, medicineRemainderModel.startDate)
        contentValues.put(KEY_END_ON, medicineRemainderModel.endDate)
        contentValues.put(KEY_REMIND_ON, medicineRemainderModel.remindDate)
        contentValues.put(KEY_REMIND_TIME, medicineRemainderModel.remindTime)
        contentValues.put(KEY_REMARKS, medicineRemainderModel.remarks)
        // Inserting Row
        val success = db.insert(TABLE_MEDICINE_REMAINDER, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    fun getRemaindersList(remaindersArrayList: ArrayList<MedicineRemainderModel>): ArrayList<MedicineRemainderModel> {
        val selectQuery = "SELECT  * FROM $TABLE_MEDICINE_REMAINDER"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val medicineRemainderModel = MedicineRemainderModel(
                        id = cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        medicineName = cursor.getString(cursor.getColumnIndex(KEY_MEDICINENAME)),
                        inTakeType = cursor.getString(cursor.getColumnIndex(KEY_INTAKE)),
                        dosage = cursor.getString(cursor.getColumnIndex(KEY_DOSAGE_TYPE)),
                        whenToTake = cursor.getString(cursor.getColumnIndex(KEY_WHEN_TO_TAKE)),
                        strength = cursor.getString(cursor.getColumnIndex(KEY_STRENGTH)),
                        strengthUnit = cursor.getString(cursor.getColumnIndex(KEY_STRENGTH_UNIT)),
                        startDate = cursor.getLong(cursor.getColumnIndex(KEY_START_FROM)),
                        endDate = cursor.getLong(cursor.getColumnIndex(KEY_END_ON)),
                        remindDate = cursor.getLong(cursor.getColumnIndex(KEY_REMIND_ON)),
                        remindTime = cursor.getLong(cursor.getColumnIndex(KEY_REMIND_TIME)),
                        remarks = cursor.getString(cursor.getColumnIndex(KEY_REMARKS))
                    )
                    remaindersArrayList.add(medicineRemainderModel)
                } while (cursor.moveToNext())
            }
            return remaindersArrayList
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return remaindersArrayList
        }


    }

    fun getRemainderTimingsList(remindDate: Long?): ArrayList<MedicineRemainderModel> {
        var remaindersArrayList = ArrayList<MedicineRemainderModel>()
        val selectQuery = "SELECT  * FROM $TABLE_MEDICINE_REMAINDER WHERE $KEY_REMIND_ON=\"$remindDate\" ORDER BY $KEY_REMIND_TIME ASC"

        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val medicineRemainderModel = MedicineRemainderModel(
                        id = cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        medicineName = cursor.getString(cursor.getColumnIndex(KEY_MEDICINENAME)),
                        inTakeType = cursor.getString(cursor.getColumnIndex(KEY_INTAKE)),
                        dosage = cursor.getString(cursor.getColumnIndex(KEY_DOSAGE_TYPE)),
                        whenToTake = cursor.getString(cursor.getColumnIndex(KEY_WHEN_TO_TAKE)),
                        strength = cursor.getString(cursor.getColumnIndex(KEY_STRENGTH)),
                        strengthUnit = cursor.getString(cursor.getColumnIndex(KEY_STRENGTH_UNIT)),
                        startDate = cursor.getLong(cursor.getColumnIndex(KEY_START_FROM)),
                        endDate = cursor.getLong(cursor.getColumnIndex(KEY_END_ON)),
                        remindDate = cursor.getLong(cursor.getColumnIndex(KEY_REMIND_ON)),
                        remindTime = cursor.getLong(cursor.getColumnIndex(KEY_REMIND_TIME)),
                        remarks = cursor.getString(cursor.getColumnIndex(KEY_REMARKS))
                    )
                    var isTimeAlreadyExists = false
                    for ((index, medicineReminderExistingModel: MedicineRemainderModel) in remaindersArrayList.withIndex()) {
                        if (medicineReminderExistingModel.remindTime == medicineRemainderModel.remindTime) {
                            isTimeAlreadyExists = true
                            break
                        }
                    }
                    if (!isTimeAlreadyExists)
                        remaindersArrayList.add(medicineRemainderModel)
                } while (cursor.moveToNext())
            }
            return remaindersArrayList
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return remaindersArrayList
        }
    }

    fun getRemaindersOnDateAndTime(remindDate: Long?, remindTime: Long?): ArrayList<MedicineRemainderModel> {
        var remaindersArrayList = ArrayList<MedicineRemainderModel>()
        val selectQuery =
            "SELECT  * FROM $TABLE_MEDICINE_REMAINDER WHERE $KEY_REMIND_ON=\"$remindDate\" AND $KEY_REMIND_TIME=\"$remindTime\""

        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val medicineRemainderModel = MedicineRemainderModel(
                        id = cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        medicineName = cursor.getString(cursor.getColumnIndex(KEY_MEDICINENAME)),
                        inTakeType = cursor.getString(cursor.getColumnIndex(KEY_INTAKE)),
                        dosage = cursor.getString(cursor.getColumnIndex(KEY_DOSAGE_TYPE)),
                        whenToTake = cursor.getString(cursor.getColumnIndex(KEY_WHEN_TO_TAKE)),
                        strength = cursor.getString(cursor.getColumnIndex(KEY_STRENGTH)),
                        strengthUnit = cursor.getString(cursor.getColumnIndex(KEY_STRENGTH_UNIT)),
                        startDate = cursor.getLong(cursor.getColumnIndex(KEY_START_FROM)),
                        endDate = cursor.getLong(cursor.getColumnIndex(KEY_END_ON)),
                        remindDate = cursor.getLong(cursor.getColumnIndex(KEY_REMIND_ON)),
                        remindTime = cursor.getLong(cursor.getColumnIndex(KEY_REMIND_TIME)),
                        remarks = cursor.getString(cursor.getColumnIndex(KEY_REMARKS))
                    )
                    remaindersArrayList.add(medicineRemainderModel)
                } while (cursor.moveToNext())
            }
            return remaindersArrayList
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return remaindersArrayList
        }
    }


}