package com.tulasimultispecialityhospital

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tulasimultispecialityhospital.util.TouchImageView
import com.squareup.picasso.Picasso

class ZoomImagesFragment : Fragment() {
    private var rootView: View? = null
    private val categoryId: String? = null
    private var zoomImageView: TouchImageView? = null
    private var position: Int = 0
    private var imageUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(
            R.layout.full_image_item,
            container, false
        )

        zoomImageView = rootView!!.findViewById(R.id.full_bottle_image) as TouchImageView
        imageUrl = arguments!!.getString("image_url")

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState)

        position = arguments!!.getInt("image_pos")

        Picasso.get()
            .load(imageUrl)
            .error(ContextCompat.getDrawable(activity as Context, R.drawable.ic_gallery_placeholder)!!)
            .into(zoomImageView)
    }
}