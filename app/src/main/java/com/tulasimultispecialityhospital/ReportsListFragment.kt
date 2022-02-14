package com.tulasimultispecialityhospital

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.tulasimultispecialityhospital.adapters.ReportsAdapter
import com.tulasimultispecialityhospital.models.ReportsModel
import com.tulasimultispecialityhospital.util.MyDoctorUtility
import kotlinx.android.synthetic.main.fragment_reports.view.*
import java.io.File
import kotlin.collections.ArrayList


class ReportsListFragment : Fragment() {

    var activity = null
    var addReportFabLayout: LinearLayout? = null
    var reportsAdapter: ReportsAdapter? = null
    var reportsArrayList = ArrayList<ReportsModel>()
    private var myDoctorUtility: MyDoctorUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDoctorUtility = MyDoctorUtility.getInstance(context as Activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_reports, container, false)

        addReportFabLayout = rootView.findViewById(R.id.addReportFabLayout) as LinearLayout

        reportsAdapter = ReportsAdapter(reportsArrayList, getActivity() as Context)
        rootView.reportsRecycleView!!.layoutManager =
            LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false)
        rootView.reportsRecycleView.adapter = reportsAdapter

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        addReportFabLayout!!.setOnClickListener {
            val intent = Intent(context, AddReportsActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        if(reportsAdapter!=null){
            reportsArrayList.clear()
            File(myDoctorUtility!!.getFilesPath(context)).walkTopDown().forEach {
                reportsArrayList.add(ReportsModel(reportLocalPath = it.absolutePath))
                println("RealPath"+it)
            }
            if(reportsArrayList.size>0)
                reportsArrayList.removeAt(0)
            reportsAdapter?.notifyDataSetChanged()
        }
    }
}
