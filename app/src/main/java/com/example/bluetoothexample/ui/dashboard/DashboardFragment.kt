package com.example.bluetoothexample.ui.dashboard

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bluetoothexample.R
import com.example.bluetoothexample.databinding.FragmentDashboardBinding
import com.example.bluetoothexample.model.BTDevice
import com.example.bluetoothexample.model.Result
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DashboardFragment : Fragment() {



    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and  onDestroyView.
    private val binding get() = _binding!!

    private var bluetoothAdapter: BluetoothAdapter? = null
    private val listDev    = ArrayList<BTDevice>()


    private lateinit var btDevicesAdapter: BTDevicesAdapter

    private val dashboardViewModel by viewModels<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rv: RecyclerView       = binding.rvBoundedBTDevices
        val sr: SwipeRefreshLayout = binding.swipeRefreshLayout

        val layoutManager = LinearLayoutManager(getContext())
        rv.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(rv.context,layoutManager.orientation)
        rv.addItemDecoration(dividerItemDecoration)
        btDevicesAdapter = BTDevicesAdapter(getContext(), listDev)
        rv.adapter = btDevicesAdapter

        btDevicesAdapter.setOnItemClickListener(object : OnItemBTDeviceClick{
            override fun onItemBTDeviceClick(get: BTDevice) {
                //val bundle = bundleOf("macaddress" to get.mac)
                view?.let {
                    Navigation.findNavController(it).navigate(R.id.action_navigation_dashboard_to_navigation_home,
                    bundleOf("macaddress" to get.mac))
                };

        }}
        )

        sr.setOnRefreshListener {
            sr.isRefreshing = false
            refresh()
        }

        subscribeUi()

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            val bluetoothManager =requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter=bluetoothManager.getAdapter()
        }


    }


    private fun subscribeUi() {
        dashboardViewModel.boundedBTDevices_List.observe(viewLifecycleOwner, Observer { result ->

            when (result.status) {
                Result.Status.SUCCESS -> {
                    result.data?.results?.let { listDev ->
                        btDevicesAdapter.updateData(listDev)
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

    //https://qastack.ru/programming/20702333/refresh-fragment-at-reload
    @SuppressLint("MissingPermission")
    fun refresh() {
        listDev.clear()
        if (bluetoothAdapter != null) {
              for (device in bluetoothAdapter!!.bondedDevices) if (device.type != BluetoothDevice.DEVICE_TYPE_LE) {
                //https://developer.android.com/topic/libraries/architecture/coroutines
                //A LifecycleScopeопределяется для каждого Lifecycleобъекта.
                // Любая сопрограмма, запущенная в этой области, отменяется при Lifecycleуничтожении.
                // Вы можете получить доступ либо через , либо CoroutineScopeк свойствам.
                // Lifecyclelifecycle.coroutineScopelifecycleOwner.lifecycleScope
                viewLifecycleOwner.lifecycleScope.launch {
                    listDev.add(BTDevice(device.address, device.name, 0))
                }
             }
        }
        dashboardViewModel.insertDeleteBTDevice(listDev)
    }

    override fun onResume() {
        super.onResume()
//        if (bluetoothAdapter == null)
//            //setEmptyText("<bluetooth not supported>") else if (!bluetoothAdapter!!.isEnabled) setEmptyText(
//            //"<bluetooth is disabled>"
//        ) else
//            //setEmptyText("<no bluetooth devices found>")
        refresh()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}