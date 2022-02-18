package com.example.bluetoothexample.ui.dashboard

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothexample.BuildConfig
import com.example.bluetoothexample.databinding.FragmentDashboardBinding
import com.example.bluetoothexample.model.BTDevice
import com.example.bluetoothexample.model.Result
import com.example.bluetoothexample.serialservice.SerialService
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dashboard.*


@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private val listDev                             = ArrayList<BTDevice>()

    private val dashboardViewModel by viewModels<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding                  = FragmentDashboardBinding.inflate(inflater, container, false)
        val rv: RecyclerView         = binding.rvBoundedBTDevices
        val layoutManager            = LinearLayoutManager(getContext())

        rv.layoutManager             = layoutManager
        val dividerItemDecoration    = DividerItemDecoration(rv.context,layoutManager.orientation)
        rv.addItemDecoration(dividerItemDecoration)

        val adapter                         = BTDevicesAdapter(getContext(), listDev)
        binding.rvBoundedBTDevices.adapter  = adapter


        fun getServiceIntent(command: DeviceConnect) =
            Intent(getActivity(),SerialService::class.java).apply {
                putExtra(SERVICE_COMMAND, command as Parcelable)
            }

        adapter.setOnItemClickListener(object : OnItemBTDeviceClick{
            override fun onItemBTDeviceClick(device: BTDevice) {
                view?.let {
                    //программно перейти на HomeFragment с параметром macaddress
                   //Navigation.findNavController(it).navigate(R.id.action_navigation_dashboard_to_navigation_home,
                   //bundleOf("macaddress" to device.mac))
                    val deviceConnect = DeviceConnect(device.mac, device.name,ACTION_START_FOREGROUND_SERVICE)
//                    val intentStart = Intent(getActivity(),SerialService::class.java)
//                    intentStart.action = ACTION_START_FOREGROUND_SERVICE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //activity!!.startForegroundService(intentStart)
                        activity!!.startForegroundService(getServiceIntent(deviceConnect))
                    } else {
                         //activity!!.startService(intentStart)
                        activity!!.startService(getServiceIntent(deviceConnect))
                    }
                };
        }})

        adapter.setOnItemClickLongListener(object : OnItemBTDeviceLongClick{
            override fun onItemBTDeviceLongClick(device: BTDevice) {
                view?.let {
                    val deviceConnect = DeviceConnect(device.mac, device.name,ACTION_STOP_FOREGROUND_SERVICE)
                    activity!!.startService(getServiceIntent(deviceConnect))
//                  val intentStop = Intent(getActivity(), SerialService::class.java)
//                  intentStop.action = ACTION_STOP_FOREGROUND_SERVICE
//                  activity!!.startService(intentStop)
                 };
            }})

        refreshDevices()
        subscribeUi(adapter)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       if (requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            val bluetoothManager =requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter=bluetoothManager.getAdapter()
       }
   }


    @SuppressLint("MissingPermission")
    fun refreshDevices() {
        listDev.clear()
        if (bluetoothAdapter != null) {
             for (device in bluetoothAdapter!!.bondedDevices) if (device.type != BluetoothDevice.DEVICE_TYPE_LE) {
                  listDev.add(BTDevice(device.address, device.name, 0))
             }
        }
        dashboardViewModel.insertDeleteBTDevice(listDev)
    }

    override fun onResume() {
        super.onResume()
//       if (bluetoothAdapter == null)
//            //setEmptyText("<bluetooth not supported>") else if (!bluetoothAdapter!!.isEnabled) setEmptyText(
//            //"<bluetooth is disabled>"
//        ) else
//            //setEmptyText("<no bluetooth devices found>")
        refreshDevices()
    }

    private fun subscribeUi(adapter: BTDevicesAdapter) {
        dashboardViewModel.devicesUsingFlow.observe(viewLifecycleOwner, Observer { result ->
            when (result.status) {
                Result.Status.SUCCESS -> {
                    result.data?.results?.let { list ->
                        adapter.updateData(list)
                    }
                    loading.visibility = View.GONE
                }

                Result.Status.ERROR -> {
                    result.message?.let {
                        showError(it)
                    }
                    loading.visibility = View.GONE
                }

                Result.Status.LOADING -> {
                    loading.visibility = View.VISIBLE
                }
            }

        })
    }


    private fun showError(msg: String) {
        Snackbar.make(vParent, msg, Snackbar.LENGTH_INDEFINITE).setAction("DISMISS") {
        }.show()
    }

    companion object{
        const val SERVICE_COMMAND                   = "Command"

        const val  ACTION_START_FOREGROUND_SERVICE  = "${BuildConfig.APPLICATION_ID}.startforegroundservice"
        const val  ACTION_STOP_FOREGROUND_SERVICE   = "${BuildConfig.APPLICATION_ID}.stopforegroundservice"

        const val  ACTION_SENT_DATA_TO_SERVER       = "${BuildConfig.APPLICATION_ID}.sentdatatoserver"
        const val  ACTION_DISABLE_DEVICE            = "${BuildConfig.APPLICATION_ID}.disabledevice"
    }

}