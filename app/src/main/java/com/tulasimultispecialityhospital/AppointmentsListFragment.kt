package com.tulasimultispecialityhospital

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.tulasimultispecialityhospital.adapters.AppointmentsAdapter
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.AppointmentModel
import com.tulasimultispecialityhospital.models.GeneralRequest
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import retrofit2.Response
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AppointmentsListFragment : Fragment(), ConnectionManager.AppointmentResponseListener {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var appointmentsAdapter: AppointmentsAdapter? = null
    var appointmentsModelArray = ArrayList<AppointmentModel>()
    var activity = null
    var appointmentsRecycleView: RecyclerView? = null
    var bookAppointmentFabLayout: LinearLayout? = null
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
        val rootView: View = inflater.inflate(R.layout.fragment_appointments_list, container, false)
        appointmentsAdapter = AppointmentsAdapter(appointmentsModelArray, getActivity() as Context)
        bookAppointmentFabLayout = rootView.findViewById(R.id.bookAppointmentFabLayout) as LinearLayout
        appointmentsRecycleView = rootView.findViewById(R.id.appointmentsRecycleView) as RecyclerView
        appointmentsRecycleView!!.layoutManager =
            LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false)
        appointmentsRecycleView!!.adapter = appointmentsAdapter
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        myDoctorUtility = MyDoctorUtility.getInstance(getActivity() as Context)

        getUserAppointmentsServiceCall(myDoctorUtility!!.getUserData().id)

        bookAppointmentFabLayout!!.setOnClickListener {
            val intent = Intent(context, BookAppointmentActivity::class.java)
            startActivity(intent)
        }

    }


    private fun getUserAppointmentsServiceCall(
        userId: String?
    ) {
        var generalRequest = GeneralRequest(
            doctor_id = resources.getString(R.string.doctor_id),
            user_id = userId
        )
        myDoctorUtility!!.showProgressDialog(getActivity() as Activity)
        ConnectionManager.getInstance(getActivity() as Context).getUserAppointmentsData(generalRequest, this)
    }

    override fun onAppointmentResponseSuccess(response: Response<AppointmentModel>?) {

        myDoctorUtility!!.dismissProgressDialog(getActivity() as Activity)
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.data!!.size > 0) {
                appointmentsModelArray = response.body()!!.data!!
                appointmentsAdapter = AppointmentsAdapter(appointmentsModelArray, getActivity() as Context)
                appointmentsRecycleView!!.adapter = appointmentsAdapter
                appointmentsAdapter!!.notifyDataSetChanged()
            }
        }
        else{
            popAlert(response?.body()!!.msg)
        }
    }

    override fun onAppointmentResponse(response: AppointmentModel?) {
        popAlert(response!!.msg)
    }

    override fun onAppointmentResponseFailure(message: String?) {
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
