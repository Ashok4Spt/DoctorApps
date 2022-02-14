package com.tulasimultispecialityhospital.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.tulasimultispecialityhospital.ImagesFragment
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.ZoomImageViewActivity
import com.tulasimultispecialityhospital.models.AlbumModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class ImageRecycleViewAdapter(val imageModelArrayList: ArrayList<AlbumModel>, val context: Context,val isShowTotalImages:Boolean,
                              val galleryId:String) :
    RecyclerView.Adapter<ImageRecycleViewAdapter.ViewHolder>() {
    lateinit var dialog: Dialog
    private var myDoctorUtility: MyDoctorUtility? = null
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageRecycleViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_item_gallery_image, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ImageRecycleViewAdapter.ViewHolder, position: Int) {
        myDoctorUtility = MyDoctorUtility.getInstance(context)

        if (position == 4) {
            if (!isShowTotalImages)
                holder.moreImagesLayout.visibility = View.VISIBLE
            else
                holder.moreImagesLayout.visibility = View.GONE
        } else {
            holder.moreImagesLayout.visibility = View.GONE
        }
        ContextCompat.getDrawable(context, R.drawable.ic_gallery_placeholder)?.let {
            Picasso.get().load(imageModelArrayList.get(position).thumb_image)
                .centerCrop()
                .resize(140, 180)
                .error(it)
                .into(holder.galleryImageView)
        }

        holder.moreImagesLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("gallery_id", galleryId)
            //set Fragmentclass Arguments
            val imagesFragment = ImagesFragment()
            imagesFragment.arguments=bundle
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, imagesFragment)
                .commit()
        }

        holder.galleryImageView.setOnClickListener {
            if (!isShowTotalImages) {
                val albumModelArrayList = java.util.ArrayList<AlbumModel>()
                albumModelArrayList.add(imageModelArrayList[position])
                val intent = Intent(context, ZoomImageViewActivity::class.java)
                intent.putExtra("images_array", albumModelArrayList)
                intent.putExtra("image_url", imageModelArrayList[position].orginal_image)
                intent.putExtra("image_pos", position)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, ZoomImageViewActivity::class.java)
                intent.putExtra("images_array", imageModelArrayList)
                intent.putExtra("image_url", imageModelArrayList[position].orginal_image)
                intent.putExtra("image_pos", position)
                context.startActivity(intent)
            }
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return imageModelArrayList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewCountTextView = itemView.findViewById(R.id.imageViewCountTextView) as AppCompatTextView
        val galleryImageView = itemView.findViewById(R.id.galleryImageView) as AppCompatImageView
        val moreImagesLayout = itemView.findViewById(R.id.moreImagesLayout) as RelativeLayout
    }



}