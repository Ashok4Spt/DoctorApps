package com.tulasimultispecialityhospital

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import com.tulasimultispecialityhospital.adapters.ImageRecycleViewAdapter
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.AlbumModel
import com.tulasimultispecialityhospital.models.GeneralRequest
import com.tulasimultispecialityhospital.util.AutoFitGridLayoutManager
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import retrofit2.Response
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ImagesFragment : Fragment(), ConnectionManager.ImageGalleryResponseListener {


    var imageAlbumRecycleView: RecyclerView? = null
    private var imageModelArrayList: ArrayList<AlbumModel> = ArrayList()
    private var imageRecycleViewAdapter: ImageRecycleViewAdapter? = null
    private var no_data_title: AppCompatTextView? = null
    private var galleryId: String? = null
    private var myDoctorUtility: MyDoctorUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            galleryId = it.getString("gallery_id")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_image_album, container, false)
        galleryId = arguments?.getString("gallery_id")
        imageAlbumRecycleView = rootView.findViewById<RecyclerView>(R.id.imageAlbumRecycleView)

        imageAlbumRecycleView!!.setPadding(20, 10, 20, 10)
        val gridLayoutManager = AutoFitGridLayoutManager(activity, 280)
        imageAlbumRecycleView!!.setLayoutManager(gridLayoutManager)
        imageAlbumRecycleView!!.setHasFixedSize(true)
        imageAlbumRecycleView!!.setNestedScrollingEnabled(false)
        imageModelArrayList = java.util.ArrayList()
        imageRecycleViewAdapter = ImageRecycleViewAdapter(imageModelArrayList, activity as Context, true, "")
        imageAlbumRecycleView!!.setAdapter(imageRecycleViewAdapter)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        myDoctorUtility = MyDoctorUtility.getInstance(getActivity() as Context)
        getImagesServiceCall()

    }
    private fun getImagesServiceCall() {
        var generalRequest = GeneralRequest(
            doctor_id = resources.getString(R.string.doctor_id)
        )
        myDoctorUtility!!.showProgressDialog(getActivity() as Activity)
        ConnectionManager.getInstance(getActivity() as Context).getImagesGallery(generalRequest, this)
    }
    override fun onImageGalleryResponseSuccess(response: Response<AlbumModel>) {
        myDoctorUtility!!.dismissProgressDialog(getActivity() as Activity)
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.data!!.size > 0) {
                for (i in 0 until response.body()!!.data!!.size) {
                    if (TextUtils.equals(response.body()!!.data!![i].gallery_id, galleryId)) {
                        for (j in 0 until response.body()!!.data!![i].images!!.size) {
                            val albumModel = response.body()!!.data!![i].images!![j]
                            imageModelArrayList.add(albumModel)
                        }
                        break
                    }
                }
                imageRecycleViewAdapter!!.notifyDataSetChanged()
            }
        }else {
            popAlert(response?.body()!!.msg)
        }
    }

    override fun onImageGalleryResponse(response: AlbumModel?) {
        popAlert(response!!.msg)
    }

    override fun onImageGalleryResponseFailure(message: String) {
        popAlert(message)
    }

    private fun popAlert(msg: String?) {
        try {
            val dialog = Dialog(getActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_no_internet)
            val titleTextView = dialog.findViewById<AppCompatTextView>(R.id.titleTextView)
            val agePositiveButton = dialog.findViewById<AppCompatButton>(R.id.agePositiveButton)
            titleTextView.text = msg
            titleTextView.setTextColor(ContextCompat.getColor(getActivity() as Context, R.color.colorErrorRed))
            agePositiveButton.setText("Okay")
            agePositiveButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            val window = dialog.window
            window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}
