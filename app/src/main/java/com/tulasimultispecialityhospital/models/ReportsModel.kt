package com.tulasimultispecialityhospital.models

import android.graphics.Bitmap
import java.util.*
import kotlin.collections.ArrayList

data class ReportsModel(
    var status: String? = null,
    var msg: String? = null,
    var report_id: String? = null,
    var reportName: String? = null,
    var reportTyp: String? = null,
    var reportBase64: Base64? = null,
    var reportLocalPath: String? = null,
    var reportRemotePath: String? = null,
    var imageBitmap: Bitmap? = null,
    var data: ArrayList<ReportsModel>? = null
)