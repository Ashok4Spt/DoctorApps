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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import com.tulasimultispecialityhospital.adapters.AlbumAdapter
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.AlbumModel
import com.tulasimultispecialityhospital.models.GeneralRequest
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import retrofit2.Response
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AlbumImagesListFragment : Fragment(), ConnectionManager.ImageGalleryResponseListener {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var albumAdapter: AlbumAdapter? = null
    var albumModelArrayList = ArrayList<AlbumModel>()
    var activity = null
    var imageAlbumRecycleView: RecyclerView? = null

    private var myDoctorUtility: MyDoctorUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_image_album, container, false)
        albumAdapter = AlbumAdapter(albumModelArrayList, getActivity() as Context)
        imageAlbumRecycleView = rootView.findViewById(R.id.imageAlbumRecycleView) as RecyclerView
        imageAlbumRecycleView!!.layoutManager =
            LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false)
        imageAlbumRecycleView!!.setHasFixedSize(true);
        imageAlbumRecycleView!!.setNestedScrollingEnabled(false);
        imageAlbumRecycleView!!.adapter = albumAdapter
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
                    if (i > 4)
                        break
                    val albumModel = response.body()!!.data!![i]
                    albumModelArrayList.add(albumModel)
                }
                albumAdapter!!.notifyDataSetChanged()
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
