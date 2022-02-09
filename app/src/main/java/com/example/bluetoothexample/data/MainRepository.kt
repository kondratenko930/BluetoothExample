package com.example.bluetoothexample.data

import com.example.bluetoothexample.api.HttpClient
import com.example.bluetoothexample.data.local.BTDeviceDao
import com.example.bluetoothexample.model.BTDevice
import com.example.bluetoothexample.model.BTScan
import com.example.bluetoothexample.model.BoundedBTDevicesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import com.example.bluetoothexample.model.Result
import java.util.HashMap

class MainRepository @Inject constructor(private val btdeviceDao: BTDeviceDao) {
    suspend fun fetchBoundedBTDevices(): Flow<Result<BoundedBTDevicesResponse>?> {
        return flow {
            emit(fetchBoundedBTDevicesCached())
        }.flowOn(Dispatchers.IO)
    }

    private fun fetchBoundedBTDevicesCached(): Result<BoundedBTDevicesResponse>? =
        btdeviceDao.getAll()?.let {
            Result.success(BoundedBTDevicesResponse(it))

        }

    suspend fun insert(btDevice: BTDevice) {
        btdeviceDao.insert(btDevice)
    }

    suspend fun insertListBTDevices(btDevices:List<BTDevice>) {
        btdeviceDao.insertAll(btDevices)
    }

    suspend fun deleteAllBTDevices() {
        btdeviceDao.deleteAll()
    }

    suspend fun deleteListBTDevices(btDevices:List<BTDevice>) {
        btdeviceDao.deleteList(btDevices)
    }

    suspend fun sendScanData(scans:List<BTScan>){
        val apiResponse: HashMap<String, Any>? = HttpClient().sendScanData(
            scans,
            "mac address"
        );
    }
 }