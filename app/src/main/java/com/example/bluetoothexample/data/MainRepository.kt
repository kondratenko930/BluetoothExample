package com.example.bluetoothexample.data

import com.example.bluetoothexample.api.HttpClient
import com.example.bluetoothexample.data.local.BTDeviceDao
import com.example.bluetoothexample.model.BTDevice
import com.example.bluetoothexample.model.BTScan
import com.example.bluetoothexample.model.BoundedBTDevicesResponse
import com.example.bluetoothexample.model.Result
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    @DelicateCoroutinesApi
    suspend fun sendScanData(scans:List<BTScan>){
        GlobalScope.launch(Dispatchers.IO) {
            val apiResponse: HashMap<String, Any>? = HttpClient().sendScanData(
                scans,
                "mac address"
            );
            val error = apiResponse!!["error"] as String?
            val success = apiResponse["success"] as Int

            if (success == 1){
                //данные сканирования были успешно переданы на сервер.
                //на клиенте они больше не нужны и их можно удалить
            }
        }
    }
 }