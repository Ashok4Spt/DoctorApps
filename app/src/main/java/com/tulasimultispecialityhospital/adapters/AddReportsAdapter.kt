package com.tulasimultispecialityhospital.adapters

import android.app.Dialog
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tulasimultispecialityhospital.AddReportsActivity
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.ReportsModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import com.squareup.picasso.Picasso
import java.io.File
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class AddReportsAdapter(val reportsArrayList: ArrayList<ReportsModel>, val context: Context) :
    RecyclerView.Adapter<AddReportsAdapter.ViewHolder>() {
    lateinit var dialog: Dialog
    private var myDoctorUtility: MyDoctorUtility? = null
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddReportsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_add_report, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: AddReportsAdapter.ViewHolder, position: Int) {
        myDoctorUtility = MyDoctorUtility.getInstance(context)
        holder.reportPageNumber.text = "Page " + (position + 1)

        var reportsModel = reportsArrayList[position]
        if (position == reportsArrayList.size - 1) {
            holder.reportAddImageView.visibility = View.VISIBLE
        } else {
            holder.reportAddImageView.visibility = View.GONE
            if (!TextUtils.isEmpty(reportsModel!!.reportLocalPath)) {
                Picasso.get().load(File(reportsModel!!.reportLocalPath))
                    .centerCrop()
                    .resize(
                        context.resources.getDimension(R.dimen._98sdp).roundToInt(),
                        context.resources.getDimension(R.dimen._118sdp).roundToInt()
                    )
                    .error(ContextCompat.getDrawable(context, R.mipmap.ic_launcher)!!).into(holder.reportImageView)
            } else {
                holder.reportImageView.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher)!!)
            }
        }
        holder.reportImageView.setOnClickListener {
            if (reportsArrayList.size - 1 == position) {
                (context as AddReportsActivity).addReportImages()
            }
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return reportsArrayList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reportImageView = itemView.findViewById(R.id.reportImageView) as AppCompatImageView
        val reportPageNumber = itemView.findViewById(R.id.reportPageNumber) as AppCompatTextView
        val reportCardView = itemView.findViewById(R.id.reportCardView) as CardView
        val reportAddImageView = itemView.findViewById(R.id.reportAddImageView) as AppCompatImageView

    }

}