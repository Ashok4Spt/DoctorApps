package com.tulasimultispecialityhospital

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.tulasimultispecialityhospital.AboutDoctorFragment
import com.tulasimultispecialityhospital.adapters.AppointmentsAdapter
import com.tulasimultispecialityhospital.adapters.DoctorLocationsAdapter
import com.tulasimultispecialityhospital.connections.ConnectionManager
import com.tulasimultispecialityhospital.models.*
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ConnectionManager.DoctorProfileResponseListener, ConnectionManager.AppointmentResponseListener {


    private lateinit var doctorLocationsAdapter: DoctorLocationsAdapter
    var doctorModelArray = ArrayList<DoctorModel>()
    private var myDoctorUtility: MyDoctorUtility? = null
    private var doctorModel: DoctorModel? = null
    private var userModel: UserModel? = null
    private var appointmentsAdapter: AppointmentsAdapter? = null
    var appointmentsModelArray = ArrayList<AppointmentModel>()
    var activity = null
    var appointmentsRecycleView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        myDoctorUtility = MyDoctorUtility.getInstance(this)
        doctorModel = myDoctorUtility!!.getDoctorData()
        userModel = myDoctorUtility!!.getUserData()
        appointmentsRecycleView = findViewById(R.id.appointmentsRecycleView);
        appointmentsRecycleView!!.layoutManager =
            LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
            ) as RecyclerView.LayoutManager?
        appointmentsAdapter = AppointmentsAdapter(appointmentsModelArray, this)
        appointmentsRecycleView!!.adapter = appointmentsAdapter
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        var headerView: View = nav_view.getHeaderView(0)
        headerView.findViewById<AppCompatTextView>(R.id.userName).text = userModel!!.name
        headerView.findViewById<AppCompatTextView>(R.id.userPhone).text = userModel!!.mobile_number

        bookAppointment.setOnClickListener {

            val intent = Intent(this, BookAppointmentActivity::class.java)
            startActivity(intent)

        }
        reports.setOnClickListener {

            supportActionBar?.title = "Reports"
            supportFragmentManager.beginTransaction().replace(R.id.container, ReportsListFragment())
                .addToBackStack(null).commit()

        }
        callDoctor.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + myDoctorUtility!!.getDoctorData()!!.mobile))
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "No applications found for this", Toast.LENGTH_SHORT).show()
            }


        }
        doctorName.text = doctorModel?.doctor_name
        doctorSpecialization.text = doctorModel?.designation
        // doctorExperience.text=doctorModel?.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            appointmentFee.text = Html.fromHtml(
                "<b>Rs " + doctorModel?.appointment_fee + "</b> ( Single Consultation )",
                Html.FROM_HTML_MODE_LEGACY
            )
        else
            appointmentFee.text =
                Html.fromHtml("<b>Rs " + doctorModel?.appointment_fee + "</b> ( Single Consultation )")


        if (!TextUtils.isEmpty(doctorModel!!.doctor_image)) {
            Picasso.get().load(doctorModel!!.doctor_image)
                .centerCrop()
                .resize(
                    resources.getDimension(R.dimen._108sdp).roundToInt(),
                    resources.getDimension(R.dimen._108sdp).roundToInt()
                )
                .error(ContextCompat.getDrawable(this, R.mipmap.ic_launcher)!!).into(doctorImage)
        } else {
            doctorImage.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_launcher)!!)
        }
        serviceCallForDoctorClinics()
        getUserAppointmentsServiceCall(myDoctorUtility!!.getUserData().id)
    }

    private fun serviceCallForDoctorClinics() {
        var generalRequest = GeneralRequest(
            doctor_id = resources.getString(R.string.doctor_id)
        )
        myDoctorUtility!!.showProgressDialog(this)
        ConnectionManager.getInstance(this).getDoctorClinicsData(generalRequest, this)
    }

    private fun getUserAppointmentsServiceCall(
        userId: String?
    ) {
        var generalRequest = GeneralRequest(
            doctor_id = resources.getString(R.string.doctor_id),
            user_id = userId
        )
        myDoctorUtility!!.showProgressDialog(this)
        ConnectionManager.getInstance(this).getUserAppointmentsData(generalRequest, this)
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            if (supportFragmentManager.backStackEntryCount == 0)
                supportActionBar?.title = "Welcome To"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                supportActionBar?.title = "Welcome To"
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                // Handle the camera action
            }
            R.id.nav_profile -> {
                supportActionBar?.title = "About Doctor"
                if (supportFragmentManager.backStackEntryCount > 0)
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction().replace(R.id.container, AboutDoctorFragment())
                    .addToBackStack(null).commit()
                // Handle the camera action
            }
            R.id.nav_appointments -> {
                supportActionBar?.title = "Appointments"
                if (supportFragmentManager.backStackEntryCount > 0)
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction().replace(R.id.container, AppointmentsListFragment())
                    .addToBackStack(null).commit()
            }
            R.id.nav_upload_reports -> {
                supportActionBar?.title = "Reports"
                if (supportFragmentManager.backStackEntryCount > 0)
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction().replace(R.id.container, ReportsListFragment())
                    .addToBackStack(null).commit()
            }
            R.id.nav_medicine_remainder -> {
                supportActionBar?.title = "Medicine Reminders"
                if (supportFragmentManager.backStackEntryCount > 0)
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction().replace(R.id.container, MedicineRemainderListFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_testimonials -> {
                supportActionBar?.title = "Testimonials"
                if (supportFragmentManager.backStackEntryCount > 0)
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction().replace(R.id.container, TestiMonialsListFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_image_gallery -> {
                supportActionBar?.title = "Image Gallery"
                if (supportFragmentManager.backStackEntryCount > 0)
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager.beginTransaction().replace(R.id.container, AlbumImagesListFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDoctorProfileResponseSuccess(response: Response<DoctorModel>?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.data!!.size > 0) {
                doctorModelArray = response.body()!!.data!!
                myDoctorUtility?.setDoctorClinicsData(response.body()!!)
                doctorLocationsAdapter = DoctorLocationsAdapter(doctorModelArray, this)
                locationsRecycleView.layoutManager =
                    LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                locationsRecycleView.adapter = doctorLocationsAdapter
                doctorLocationsAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDoctorProfileResponse(response: DoctorModel?) {

    }

    override fun onDoctorProfileResponseFailure(message: String?) {

    }


    override fun onAppointmentResponseSuccess(response: Response<AppointmentModel>?) {
        myDoctorUtility!!.dismissProgressDialog(this)
        appointmentsModelArray.clear()
        if (TextUtils.equals(response?.body()!!.status, "success")) {
            if (response.body()!!.data!!.size > 0) {
                for ((index, appointmentModel: AppointmentModel) in response.body()!!.data!!.withIndex()) {
                    if (getIsCurrentBooking(appointmentModel))
                        appointmentsModelArray.add(appointmentModel)
                }
                appointmentsAdapter!!.notifyDataSetChanged()
            }
        }

        if (appointmentsModelArray.size == 0)
            upcomingAppointmentsCard.visibility = View.GONE
        else
            upcomingAppointmentsCard.visibility = View.VISIBLE
    }

    override fun onAppointmentResponse(response: AppointmentModel?) {
        upcomingAppointmentsCard.visibility = View.GONE
    }

    override fun onAppointmentResponseFailure(message: String?) {
        upcomingAppointmentsCard.visibility = View.GONE
    }

    fun getIsCurrentBooking(appointmentModel: AppointmentModel): Boolean {
        var calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = SimpleDateFormat("dd-MM-yyyy").parse(appointmentModel.slot_date).time
        var slotTiming: String = appointmentModel.slot_timing!!
        calendar.set(
            Calendar.HOUR_OF_DAY,
            Integer.parseInt(slotTiming!!.split("-")[1].split(":")[0].trim())
        )

        if (slotTiming!!.split("-")[1].split(":")[1].trim().startsWith("0"))
            calendar.set(
                Calendar.MINUTE,
                Integer.parseInt(
                    slotTiming!!.split("-")[1].split(":")[1].trim().substring(
                        1
                    )
                )
            )
        else
            calendar.set(
                Calendar.MINUTE,
                Integer.parseInt(slotTiming!!.split("-")[1].split(":")[1].trim())
            )
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)

        return calendar.timeInMillis > Calendar.getInstance().timeInMillis
    }


}
