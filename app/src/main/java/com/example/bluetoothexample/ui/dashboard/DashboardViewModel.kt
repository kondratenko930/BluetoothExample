package com.example.bluetoothexample.ui.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothexample.data.MainRepository
import com.example.bluetoothexample.model.BTDevice
import com.example.bluetoothexample.model.BoundedBTDevicesResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.bluetoothexample.model.Result

@HiltViewModel
class DashboardViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val _boundedBTDevices_List = MutableLiveData<Result<BoundedBTDevicesResponse>>()
    val boundedBTDevices_List = _boundedBTDevices_List

    init {
        fetchBoundedBTDevices()
    }

    private fun fetchBoundedBTDevices() {
        viewModelScope.launch {
            mainRepository.fetchBoundedBTDevices().collect {
                _boundedBTDevices_List.value = it
            }
        }
    }


    fun insertDeleteBTDevice(btDevices:ArrayList<BTDevice>) {
        viewModelScope.launch {
            mainRepository.insertDeleteBTDevice(btDevices)
        }
    }


}