package com.example.bluetoothexample.ui.dashboard

import androidx.lifecycle.*
import com.example.bluetoothexample.data.MainRepository
import com.example.bluetoothexample.model.BTDevice
import com.example.bluetoothexample.model.BoundedBTDevicesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.bluetoothexample.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel
class DashboardViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
    //var devicesUsingFlow2: LiveData<Result<BoundedBTDevicesResponse>>? = null

    val devicesUsingFlow: LiveData<Result<BoundedBTDevicesResponse>> = mainRepository.devicesFlow.asLiveData()

    val currentState: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

//    init {
//        getDevices()
//    }
//    fun getDevices(): LiveData<Result<BoundedBTDevicesResponse>> {
//        val devicesUsingFlow: LiveData<Result<BoundedBTDevicesResponse>> = mainRepository.devicesFlow.asLiveData()
//        devicesUsingFlow2 = devicesUsingFlow
//        return devicesUsingFlow
//    }

    fun insertDeleteBTDevice(btDevices:ArrayList<BTDevice>) {
        viewModelScope.launch {
            mainRepository.insertDeleteBTDevice(btDevices)
        }
    }


}