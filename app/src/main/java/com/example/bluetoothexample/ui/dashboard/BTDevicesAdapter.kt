package com.example.bluetoothexample.ui.dashboard

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothexample.R
import com.example.bluetoothexample.model.BTDevice
import kotlinx.android.synthetic.main.item_bt_device.view.*


class BTDevicesAdapter(
    private val context: Context?,
    private val list: ArrayList<BTDevice>
    ) : RecyclerView.Adapter<BTDevicesAdapter.ViewHolder>() {

    private var clickListener: OnItemBTDeviceClick? = null
    fun setOnItemClickListener(clickListener: OnItemBTDeviceClick) {
        this.clickListener = clickListener
    }

    private var clickLongListener: OnItemBTDeviceLongClick? = null
    fun setOnItemClickLongListener(clickLongListener: OnItemBTDeviceLongClick) {
        this.clickLongListener = clickLongListener
    }



    inner class ViewHolder(private val context: Context?, itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,View.OnLongClickListener {

        init {
            if (clickListener != null) {
                itemView.setOnClickListener(this)
            }
        }

        fun bind(device: BTDevice) {
           itemView.bt_name.text  =  device.name
           itemView.bt_mac.text   =  device.mac
       }

        override fun onClick(v: View?) {
            if (v != null) {
                clickListener?.onItemBTDeviceClick(list?.get(adapterPosition))
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (v != null) {
                clickLongListener?.onItemBTDeviceLongClick(list?.get(adapterPosition))
            }
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bt_device, parent, false)
        return ViewHolder(context, view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
   }

    fun updateData(newList: List<BTDevice>) {
        list.clear()
        val sortedList = newList.sortedBy { device -> device.mac }
        list.addAll(sortedList)
        notifyDataSetChanged()
    }
}