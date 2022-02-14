package com.tulasimultispecialityhospital.models

import java.io.Serializable

data class AlbumModel (
    var status: String? = null,
    var msg: String? = null,
    var gallery_id: String? = null,
    var gallery_name: String? = null,
    var orginal_image: String? = null,
    var thumb_image: String? = null,
    var thumb_url: String? = null,
    var video_url: String? = null,
    var data: ArrayList<AlbumModel>? = null,
    var images: ArrayList<AlbumModel>? = null,
    var slot_timing: String? = null
) :Serializable
