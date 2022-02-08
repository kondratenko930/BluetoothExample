package com.example.bluetoothexample.ui.home

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bluetoothexample.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(),ServiceConnection {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel by viewModels<HomeViewModel>()

    //1.
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
//        getActivity()!!.bindService(
//            Intent(getActivity(), SerialService::class.java),
//            this,
//            Context.BIND_AUTO_CREATE
//        )
    }

    //2.
    override fun onStart() {
        super.onStart()
//        if (service != null) service.attach(this) else activity!!.startService(
//            Intent(
//                activity,
//                SerialService::class.java
//            )
//        ) // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    //3.
    //runOnUiThread :Запускает указанное действие в потоке пользовательского интерфейса.
    //Если текущий поток является потоком пользовательского интерфейса, то действие выполняется немедленно.
    //Если текущий поток не является потоком пользовательского интерфейса, действие отправляется в очередь
    //событий потока пользовательского интерфейса.
    override fun onResume() {
        super.onResume()
//        if (initialStart && service != null) {
//            initialStart = false
//            activity!!.runOnUiThread { this.connect() }
//        }
    }

    //4.
    override fun onStop() {
//        if (service != null && !activity!!.isChangingConfigurations) service.detach()
        super.onStop()
    }

    //5.
    override fun onDestroy() {
//        if (connected != de.kai_morich.simple_bluetooth_terminal.TerminalFragment.Connected.False) disconnect()
//        activity!!.stopService(Intent(activity, SerialService::class.java))
        super.onDestroy()
    }

    //6.
    override fun onDetach() {
//        try {
//            activity!!.unbindService(this)
//        } catch (ignored: Exception) {
//        }
        super.onDetach()
    }

    //7.
    //onServiceConnected : этот метод вызывается, когда служба будет подключена к деятельности
    //Кроме того, внутри onServiceConnected получим экземпляр класса IBinder,
    //который используется для получения экземпляра сервиса ,
    //для взаимодействия с ним из деятельности.
    //Для получения экземпляра серваиса используется метод .getService() из объекта binding, который он вернул.
    override fun onServiceConnected(name: ComponentName?, binder: IBinder) {
//        service = (binder as SerialBinder).getService()
//        service.attach(this)
//        if (initialStart && isResumed) {
//            initialStart = false
//            activity!!.runOnUiThread { this.connect() }
//        }
    }
    //8.
    //onServiceDisconnected : этот метод вызывается, когда  служба будет отключена от  деятельности
    override fun onServiceDisconnected(name: ComponentName?) {
       //service = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mac=arguments?.getString("macaddress")
        Toast.makeText(
            getContext(),
            "Clicked: ${mac}",
            Toast.LENGTH_SHORT
        ).show()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}