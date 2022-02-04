package com.example.bluetoothexample.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothexample.R
import com.example.bluetoothexample.model.BTDevice
import kotlinx.android.synthetic.main.item_bt_device.view.*


class BTDevicesAdapter(private val context: Context?, private val list: ArrayList<BTDevice>) :
        RecyclerView.Adapter<BTDevicesAdapter.BTDeviceViewHolder>() {

    class BTDeviceViewHolder(private val context: Context?, itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(device: BTDevice) {
            itemView.setOnClickListener {
//                val intent = Intent(context, DetailsActivity::class.java)
//                intent.putExtra(DetailsActivity.EXTRAS_MOVIE_ID, movie.id)
//                context.startActivity(intent)
            }
           itemView.bt_name.text  =  device.name
           itemView.bt_mac.text   =  device.mac

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BTDeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bt_device, parent, false)
        return BTDeviceViewHolder(context, view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: BTDeviceViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun updateData(newList: List<BTDevice>) {
        list.clear()
        val sortedList = newList.sortedBy { device -> device.id }
        list.addAll(sortedList)
        notifyDataSetChanged()
    }
}