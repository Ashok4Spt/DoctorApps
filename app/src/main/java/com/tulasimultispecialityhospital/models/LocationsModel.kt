package com.tulasimultispecialityhospital.models

data class LocationsModel(
    var status: String? = null,
    var msg: String? = null,
    var clinic_id: String? = null,
    var clinic_location: String? = null,
    var data: ArrayList<LocationsModel>? = null,
    var isLocationSelected: Boolean? = null
)