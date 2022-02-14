package com.tulasimultispecialityhospital.adapters

import android.app.Dialog
import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.TestimonialModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import kotlin.collections.ArrayList

class TestimonialsAdapter(val testimonialsArratList: ArrayList<TestimonialModel>, val context: Context) :
    RecyclerView.Adapter<TestimonialsAdapter.ViewHolder>() {
    lateinit var dialog: Dialog
    private var myDoctorUtility: MyDoctorUtility? = null
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestimonialsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_testimonial, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: TestimonialsAdapter.ViewHolder, position: Int) {
        myDoctorUtility = MyDoctorUtility.getInstance(context)


        var testimonialModel = testimonialsArratList[position]
        holder.testimonialDescription.text =testimonialModel.description
        holder.testimonialAuthor.text =testimonialModel.name

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return testimonialsArratList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val testimonialDescription = itemView.findViewById(R.id.testimonialDescription) as AppCompatTextView
        val testimonialAuthor = itemView.findViewById(R.id.testimonialAuthor) as AppCompatTextView
       

    }

}