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
import kotlinx.coroutines.flow.flatMapLatest

@HiltViewModel
class DashboardViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val _boundedBTDevices_List = MutableLiveData<Result<BoundedBTDevicesResponse>>()
    val boundedBTDevices_List = _boundedBTDevices_List


    val devicesUsingFlow: LiveData<List<BTDevice>> = mainRepository.devicesFlow.asLiveData()


    init {
        fetchBoundedBTDevices2()
    }

    private fun fetchBoundedBTDevices2() {
        viewModelScope.launch {
            mainRepository.fetchBoundedBTDevices2().collect {
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