package com.tulasimultispecialityhospital.models

data class DoctorModel(
    var status: String? = null,
    var msg: String? = null,
    var doctor_image: String? = null,
    var doctor_name: String? = null,
    var designation: String? = null,
    var opening_time: String? = null,
    var closing_time: String? = null,
    var appointment_fee: String? = null,
    var mobile: String? = null,
    var email: String? = null,
    var user: ArrayList<DoctorModel>? = null,
    var clinic_id: String? = null,
    var clinic_location: String? = null,
    var data: ArrayList<DoctorModel>? = null,
    var description: String? = null,
    var isLocationSelected: Boolean = false
)