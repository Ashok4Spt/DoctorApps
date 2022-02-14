package com.tulasimultispecialityhospital.models

data class UserModel(
    var status: String? = null,
    var msg: String? = null,
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var mobile_number: String? = null,
    var dob: String? = null,
    var gender: String? = null,
    var user: ArrayList<UserModel>? = null
)