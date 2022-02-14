package com.tulasimultispecialityhospital

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.tulasimultispecialityhospital.adapters.MedicineRemainderDateAdapter
import com.tulasimultispecialityhospital.database.MyDoctorDataBase
import com.tulasimultispecialityhospital.models.MedicineRemainderModel
import kotlinx.android.synthetic.main.fragment_medicine_reminder.view.*
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MedicineRemainderListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var activity = null
    var addCapsuleFabLayout: LinearLayout? = null
    var medicineRemainderDateAdapter: MedicineRemainderDateAdapter? = null
    var medicineRemainderArrayList = ArrayList<MedicineRemainderModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_medicine_reminder, container, false)

        addCapsuleFabLayout = rootView.findViewById(R.id.addCapsuleFabLayout) as LinearLayout

        medicineRemainderDateAdapter =
            MedicineRemainderDateAdapter(medicineRemainderArrayList, getActivity() as Context)
        rootView.medicineRemaindersRecycleView!!.layoutManager =
            LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)
        rootView.medicineRemaindersRecycleView.adapter = medicineRemainderDateAdapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        addCapsuleFabLayout!!.setOnClickListener {
            val intent = Intent(context, AddMedicineRemainderActivity::class.java)
            startActivity(intent)
        }

    }

   private fun addStaticMedicineData() {

        val dbHandler = MyDoctorDataBase(getActivity() as Context)

        var allMedicineRemaindersArrayList = ArrayList<MedicineRemainderModel>()
        allMedicineRemaindersArrayList = dbHandler.getRemaindersList(allMedicineRemaindersArrayList);

        for ((index, medicineRemainderModel: MedicineRemainderModel) in allMedicineRemaindersArrayList.withIndex()) {
            if (medicineRemainderModel.endDate!! > Calendar.getInstance().timeInMillis) {
                var isDateAlreadyExists = false
                for ((index, medicineReminderExistingModel: MedicineRemainderModel) in medicineRemainderArrayList.withIndex()) {

                    if (medicineReminderExistingModel.remindDate == medicineRemainderModel.remindDate) {
                        isDateAlreadyExists = true
                        break
                    }
                }
                if (!isDateAlreadyExists) {
                    medicineRemainderArrayList.add(medicineRemainderModel);
                }
            }
        }

        for ((index, medicineRemainderModel: MedicineRemainderModel) in medicineRemainderArrayList.withIndex()) {
            medicineRemainderModel.remindTimeArrayList = dbHandler.getRemainderTimingsList(medicineRemainderModel.remindDate)
            medicineRemainderArrayList.set(index, medicineRemainderModel)

        }
        for ((index, medicineRemainderModel: MedicineRemainderModel) in medicineRemainderArrayList.withIndex()) {
            for ((index, timeRemainderModel: MedicineRemainderModel) in medicineRemainderModel.remindTimeArrayList!!.withIndex()) {
                timeRemainderModel.medicineRemaindersList = dbHandler.getRemaindersOnDateAndTime(
                    medicineRemainderModel.remindDate,
                    timeRemainderModel.remindTime
                )
                medicineRemainderModel.remindTimeArrayList!!.set(index, timeRemainderModel)
            }
            medicineRemainderArrayList.set(index, medicineRemainderModel)
        }

        medicineRemainderDateAdapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        if(medicineRemainderDateAdapter!=null)
        {
            medicineRemainderArrayList.clear()
            addStaticMedicineData()
        }
    }
}
