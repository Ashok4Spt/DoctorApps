package com.tulasimultispecialityhospital.util

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AutoFitGridLayoutManager @JvmOverloads constructor(
    context: Context?,
    columnWidth: Int = 0
) : GridLayoutManager(context, columnWidth) {
    private var columnWidth: Int = 1
    private var columnWidthChanged = true

    init {
        setColumnWidth(columnWidth)
    }

    private fun setColumnWidth(newColumnWidth: Int): Int {
        if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
            columnWidth = newColumnWidth
            columnWidthChanged = true
        }

        return columnWidth
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        if (columnWidthChanged && columnWidth > 0) {
            val totalSpace: Int
            if (orientation === androidx.recyclerview.widget.RecyclerView.VERTICAL) {
                totalSpace = width - paddingRight - paddingLeft
            } else {
                totalSpace = height - paddingTop - paddingBottom
            }
            val spanCount = Math.max(1, totalSpace / columnWidth)
            setSpanCount(spanCount)
            columnWidthChanged = false
        }
        super.onLayoutChildren(recycler, state)
    }
}