package com.tulasimultispecialityhospital.models

data class TestimonialModel(
    var status: String? = null,
    var msg: String? = null,
    var name: String? = null,
    var data: ArrayList<TestimonialModel>? = null,
    var description: String? = null

)