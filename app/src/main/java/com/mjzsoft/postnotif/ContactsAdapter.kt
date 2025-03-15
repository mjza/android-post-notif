package com.mjzsoft.postnotif

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mjzsoft.postnotif.database.DataModel

class ContactsAdapter(private var dataList: List<DataModel>, private val type: String) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUnit: TextView = view.findViewById(R.id.tvUnit)
        val tvValue: TextView = view.findViewById(R.id.tvValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_table_row, parent, false) // Uses item_table_row.xml
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.tvUnit.text = item.unitNumber
        holder.tvValue.text = if (type == "Emails") item.email else item.telephone
    }

    override fun getItemCount() = dataList.size

    fun updateData(newList: List<DataModel>) {
        dataList = newList
        notifyDataSetChanged()
    }
}
