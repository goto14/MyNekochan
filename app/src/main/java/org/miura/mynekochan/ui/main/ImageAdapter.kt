package org.miura.mynekochan.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import org.miura.mynekochan.R
import org.miura.mynekochan.data.model.ImageData

class ImageAdapter(val context: Context, private val data: List<ImageData>) : BaseAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_item, null)
        view.findViewById<ImageView>(R.id.list_item_image)?.setImageBitmap(data[position].image)
        return view
    }

}