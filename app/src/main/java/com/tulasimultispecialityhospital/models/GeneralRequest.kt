package com.tulasimultispecialityhospital.models

data class GeneralRequest(
    var mobile: String? = null,
    var password: String? = null,
    var doctor_id: String? = null,
    var user_fullname: String? = null,
    var user_email: String? = null,
    var user_mobile: String? = null,
    var user_pwd: String? = null,
    var user_gender: String? = null,
    var user_dob: String? = null,
    var user_city: String? = null,
    var clinic_id: String?= null,
    var slot_date: String?= null,
    var slot_type: String?= null,
    var slot_timing: String?= null,
    var appointment_type: String?= null,
    var user_id: String?= null,
    var device_id: String?= null
)
