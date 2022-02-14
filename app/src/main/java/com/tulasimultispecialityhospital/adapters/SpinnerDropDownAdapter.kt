package com.tulasimultispecialityhospital.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.tulasimultispecialityhospital.R

class SpinnerDropDownAdapter(
    val context: Context,
    var listItemsTxt: ArrayList<String>,
    var isHintTypeAvailable: Boolean
) : BaseAdapter() {


    val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.spinner_menu_item, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        // setting adapter item height programatically.

//        val params = view.layoutParams
//        params.height = 60
//        view.layoutParams = params

        if (isHintTypeAvailable) {
            if (position == 0) {
                vh.label.setTextColor(ContextCompat.getColor(context, R.color.colorFormLabel))
                vh.imgDropDownMenuIcon.setColorFilter(
                    ContextCompat.getColor(context, R.color.colorFormLabel),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                vh.label.setTextColor(ContextCompat.getColor(context, R.color.colorBlack90))
                vh.imgDropDownMenuIcon.setColorFilter(
                    ContextCompat.getColor(context, R.color.colorBlack90),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        } else {
            vh.label.setTextColor(ContextCompat.getColor(context, R.color.colorBlack90))
            vh.imgDropDownMenuIcon.setColorFilter(
                ContextCompat.getColor(context, R.color.colorBlack90),
                android.graphics.PorterDuff.Mode.SRC_IN)
        }
        vh.label.text = listItemsTxt.get(position)
        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.spinner_drop_down_menu, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        // setting adapter item height programatically.

//        val params = view.layoutParams
//        params.height = 60
//        view.layoutParams = params

        if (isHintTypeAvailable) {
            if (position == 0) {
                vh.label.setTextColor(ContextCompat.getColor(context, R.color.colorFormLabel))
                vh.imgDropDownMenuIcon.setColorFilter(
                    ContextCompat.getColor(context, R.color.colorFormLabel),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                vh.label.setTextColor(ContextCompat.getColor(context, R.color.colorBlack90))
                vh.imgDropDownMenuIcon.setColorFilter(
                    ContextCompat.getColor(context, R.color.colorBlack90),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        } else {
            vh.label.setTextColor(ContextCompat.getColor(context, R.color.colorBlack90))
            vh.imgDropDownMenuIcon.setColorFilter(
                ContextCompat.getColor(context, R.color.colorBlack90),
                android.graphics.PorterDuff.Mode.SRC_IN)
        }
        vh.label.text = listItemsTxt.get(position)
        return view
    }

    override fun getItem(position: Int): Any? {

        return null

    }

    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {
        return listItemsTxt.size
    }

    private class ItemRowHolder(row: View?) {

        val label: AppCompatTextView
        val imgDropDownMenuIcon: AppCompatImageView

        init {
            this.label = row?.findViewById(R.id.txtDropDownLabel) as AppCompatTextView
            this.imgDropDownMenuIcon = row?.findViewById(R.id.imgDropDownMenuIcon) as AppCompatImageView
        }
    }
}