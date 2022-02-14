package com.tulasimultispecialityhospital.models

data class AppointmentModel(
    var status: String? = null,
    var msg: String? = null,
    var reference_id: String? = null,
    var clinic_id: String? = null,
    var appointment_type: String? = null,
    var slot_date: String?= null,
    var slot_type: String?= null,
    var clinic_location: String?= null,
    var data: ArrayList<AppointmentModel>? = null,
    var slot_timing: String?= null
)