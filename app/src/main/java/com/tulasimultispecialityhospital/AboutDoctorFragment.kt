package com.tulasimultispecialityhospital


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tulasimultispecialityhospital.R
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.DoctorModel
import com.tulasimultispecialityhospital.models.GeneralRequest
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import com.squareup.picasso.Picasso
import retrofit2.Response
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AboutDoctorFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AboutDoctorFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */


class AboutDoctorFragment : Fragment(), ConnectionManager.DoctorProfileResponseListener {

    private var rootView: View? = null
    private var myDoctorUtility: MyDoctorUtility? = null
    private var doctorModel: DoctorModel? = null
    var aboutDoctor: AppCompatTextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDoctorUtility = MyDoctorUtility.getInstance(activity as Context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_about_doctor, container, false)
        aboutDoctor = rootView?.findViewById(R.id.aboutDoctor) as AppCompatTextView
        rootView?.findViewById<AppCompatImageView>(R.id.callDoctor)?.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + myDoctorUtility!!.getDoctorData()!!.mobile))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity, "No applications found for this", Toast.LENGTH_SHORT).show()
            }
        }
        rootView?.findViewById<AppCompatImageView>(R.id.emailDoctor)?.setOnClickListener {
            try {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", myDoctorUtility!!.getDoctorData()!!.email, null
                    )
                )
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity, "No applications found for this", Toast.LENGTH_SHORT).show()
            }
        }
        rootView?.findViewById<AppCompatImageView>(R.id.clinicLocation)?.setOnClickListener {

            var doctorModelArray = ArrayList<DoctorModel>()
            if (myDoctorUtility?.getDoctorClinicsData()?.data != null) {
                doctorModelArray = myDoctorUtility?.getDoctorClinicsData()?.data!!

                if (doctorModelArray.size > 1) {

                } else {
//                var uri = String.format(Locale.ENGLISH, "geo:%f,%f", doctorModelArray[0], longitude);
//                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                startActivity(intent);
                }
            }
        }


        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        doctorModel = myDoctorUtility!!.getDoctorData()

        rootView?.findViewById<AppCompatTextView>(R.id.doctorName)?.text = doctorModel?.doctor_name
        rootView?.findViewById<AppCompatTextView>(R.id.doctorSpecialization)?.text = doctorModel?.designation
     //   rootView?.findViewById<AppCompatTextView>(R.id.doctorExperience)?.text = doctorModel?.designation



        if (!TextUtils.isEmpty(doctorModel!!.doctor_image)) {
            Picasso.get().load(doctorModel!!.doctor_image)
                .error(ContextCompat.getDrawable(activity as Context, R.mipmap.ic_launcher)!!)
                .into(rootView?.findViewById<AppCompatImageView>(R.id.doctorImage))
        } else {
            rootView?.findViewById<AppCompatImageView>(R.id.doctorImage)
                ?.setImageDrawable(ContextCompat.getDrawable(activity as Context, R.mipmap.ic_launcher)!!)
        }
        requestDoctorAboutServiceCall()
    }

    private fun requestDoctorAboutServiceCall() {
        var generalRequest = GeneralRequest(
            doctor_id = resources.getString(R.string.doctor_id)
        )
        myDoctorUtility!!.showProgressDialog(context as Activity)
        ConnectionManager.getInstance(activity as Context).getDoctorAboutData(generalRequest, this)
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AboutDoctorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutDoctorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onDoctorProfileResponseSuccess(response: Response<DoctorModel>?) {
        myDoctorUtility!!.dismissProgressDialog(context as Activity)
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.data!!.size > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    aboutDoctor?.text =
                        Html.fromHtml(response.body()!!.data!![0]?.description, Html.FROM_HTML_MODE_LEGACY)
                else
                    aboutDoctor?.text = Html.fromHtml(response.body()!!.data!![0]?.description)
            }
        }
    }

    override fun onDoctorProfileResponse(response: DoctorModel?) {
        myDoctorUtility!!.dismissProgressDialog(context as Activity)
    }

    override fun onDoctorProfileResponseFailure(message: String?) {
        myDoctorUtility!!.dismissProgressDialog(context as Activity)
    }
}
