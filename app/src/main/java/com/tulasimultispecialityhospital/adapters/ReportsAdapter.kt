package com.tulasimultispecialityhospital.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.ReportsModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class ReportsAdapter(val reportsArrayList: ArrayList<ReportsModel>, val context: Context) :
    RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {
    lateinit var dialog: Dialog
    private var myDoctorUtility: MyDoctorUtility? = null
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_reports, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ReportsAdapter.ViewHolder, position: Int) {
        myDoctorUtility = MyDoctorUtility.getInstance(context)
        holder.reportsUploadBy.text = "Receipt By " + myDoctorUtility!!.getUserData()!!.name
        holder.reportsUploadDate.text =
            "Updated on :- " + SimpleDateFormat("dd-MM-yyyy").format(File(reportsArrayList[position].reportLocalPath).lastModified())
        if (TextUtils.equals(getFileType(reportsArrayList[position].reportLocalPath!!), "pdf")) {
            holder.previewImageView.scaleType=ImageView.ScaleType.CENTER_INSIDE
            holder.previewImageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_pdf))
        } else {
            holder.previewImageView.scaleType=ImageView.ScaleType.FIT_XY
            Picasso.get().load(File(reportsArrayList[position].reportLocalPath))
                .error(ContextCompat.getDrawable(context, R.mipmap.ic_launcher)!!).into(holder.previewImageView)
        }

        if (position == reportsArrayList.size - 1)
            holder.reportsDivider.visibility = View.GONE

        holder.reportsItem.setOnClickListener {
            openFile(File(reportsArrayList[position].reportLocalPath))
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return reportsArrayList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reportsUploadBy = itemView.findViewById(R.id.reportsUploadBy) as AppCompatTextView
        val reportsUploadDate = itemView.findViewById(R.id.reportsUploadDate) as AppCompatTextView
        val previewImageView = itemView.findViewById(R.id.previewImageView) as AppCompatImageView
        val reportsItem = itemView.findViewById(R.id.reportsItem) as LinearLayout
        val reportsDivider = itemView.findViewById<View>(R.id.reportsDivider)

    }

    private fun getFileType(url: String): String {
        return if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
            "image"
        } else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe")
            || url.contains(".mp4") || url.contains(".avi") || url.contains(".m4a")
        ) {
            "video"
        } else if (url.contains(".wav") || url.contains(".mp3")) {
            "audio"
        } else if (url.contains("pdf")) {
            "pdf"
        } else if (url.contains(".doc") || url.contains(".docx")) {
            "doc"
        } else if (url.contains(".xls") || url.contains(".xlsx")) {
            "xls"
        } else if (url.contains(".ppt") || url.contains(".pptx")) {
            // Powerpoint file
            "ppt"
        } else {
            "unknown"
        }
    }

    fun openFile(url: File) {

        var uri: Uri =FileProvider.getUriForFile(context,"com.tulasimultispecialityhospital.fileProvider",url)

        var intent: Intent = Intent(Intent.ACTION_VIEW)
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(
                ".mpe"
            ) || url.toString().contains(".mp4") || url.toString().contains(".avi")
        ) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file
            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

}