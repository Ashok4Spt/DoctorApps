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
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import com.tulasimultispecialityhospital.adapters.TestimonialsAdapter
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.GeneralRequest
import com.tulasimultispecialityhospital.models.TestimonialModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import kotlinx.android.synthetic.main.fragment_testimonials.view.*
import retrofit2.Response
import kotlin.collections.ArrayList


class TestiMonialsListFragment : Fragment(), ConnectionManager.TestimonialsResponseListener {


    var activity = null
    var testimonialsAdapter: TestimonialsAdapter? = null
    var testimonialsArrayList = ArrayList<TestimonialModel>()
    private var myDoctorUtility: MyDoctorUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDoctorUtility = MyDoctorUtility.getInstance(context as Activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_testimonials, container, false)


        testimonialsAdapter = TestimonialsAdapter(testimonialsArrayList, getActivity() as Context)
        rootView.testimonialsRecycleView!!.layoutManager =
            LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false)
        rootView.testimonialsRecycleView.adapter = testimonialsAdapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getTestimonialsServiceCall()


    }

    private fun getTestimonialsServiceCall() {
        var generalRequest = GeneralRequest(
            doctor_id = resources.getString(R.string.doctor_id)
        )
        myDoctorUtility!!.showProgressDialog(getActivity() as Activity)
        ConnectionManager.getInstance(getActivity() as Context).getTestimonials(generalRequest, this)
    }


    override fun onTestimonialsResponseSuccess(response: Response<TestimonialModel>) {
        myDoctorUtility!!.dismissProgressDialog(getActivity() as Activity)
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.data!!.size > 0) {
                testimonialsArrayList.addAll(response.body()!!.data!!)
                testimonialsAdapter!!.notifyDataSetChanged()
            }
        } else {
            popAlert(response?.body()!!.msg)
        }
    }

    override fun onTestimonialsResponse(response: TestimonialModel?) {
        popAlert(response!!.msg)
    }

    override fun onTestimonialsResponseFailure(message: String) {
        popAlert(message)
    }


    override fun onResume() {
        super.onResume()

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
