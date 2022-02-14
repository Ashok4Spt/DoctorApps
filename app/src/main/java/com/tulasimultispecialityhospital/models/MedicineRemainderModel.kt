package com.tulasimultispecialityhospital.models

import kotlin.collections.ArrayList

data class MedicineRemainderModel(
    var medicineName: String? = null,
    var inTakeType: String? = null,
    var dosage: String? = null,
    var whenToTake: String? = null,
    var strength: String? = null,
    var strengthUnit: String? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var remindDate: Long? = null,
    var remindTime: Long? = null,
    var remindTimeArrayList: ArrayList<MedicineRemainderModel>? = null,
    var medicineRemaindersList: ArrayList<MedicineRemainderModel>? = null,
    var id: Int? = null,
    var remarks: String?=null,
    var isDateVisible:Boolean=true)