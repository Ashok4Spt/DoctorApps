package com.tulasimultispecialityhospital.adapters

import android.app.Dialog
import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.models.AlbumModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import kotlin.collections.ArrayList

class AlbumAdapter(val albumModelArrayList: ArrayList<AlbumModel>, val context: Context) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    lateinit var dialog: Dialog
    private var myDoctorUtility: MyDoctorUtility? = null
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_item_album, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: AlbumAdapter.ViewHolder, position: Int) {
        myDoctorUtility = MyDoctorUtility.getInstance(context)
        holder.albumTitleTextView.text=albumModelArrayList[position].gallery_name
        val mGrid = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.albumPhotosRecycleView.layoutManager = mGrid
        val imageRecycleViewAdapter = ImageRecycleViewAdapter(
            albumModelArrayList[position].images!!,context as Context, false, albumModelArrayList[position].gallery_id!!
        )
        holder.albumPhotosRecycleView.adapter = imageRecycleViewAdapter


    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return albumModelArrayList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumTitleTextView = itemView.findViewById(R.id.albumTitleTextView) as AppCompatTextView
        val albumDateTextView = itemView.findViewById(R.id.albumDateTextView) as AppCompatTextView
        val albumPhotosRecycleView = itemView.findViewById(R.id.albumPhotosRecycleView) as RecyclerView
    }

}