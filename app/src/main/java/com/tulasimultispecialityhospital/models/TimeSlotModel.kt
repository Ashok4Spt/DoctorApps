package com.tulasimultispecialityhospital.models

data class TimeSlotModel(
    var status: String? = null,
    var msg: String? = null,
    var slot_type: String? = null,
    var slot_timing: String? = null,
    var slots: String? = null,
    var isTimeSlotSelected:Boolean=false,
    var data: ArrayList<TimeSlotModel>? = null
)