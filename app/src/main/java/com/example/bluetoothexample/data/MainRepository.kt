package com.example.bluetoothexample.data

import androidx.lifecycle.MutableLiveData
import com.example.bluetoothexample.api.HttpClient
import com.example.bluetoothexample.data.local.BTDeviceDao
import com.example.bluetoothexample.model.BTDevice
import com.example.bluetoothexample.model.BTScan
import com.example.bluetoothexample.model.BoundedBTDevicesResponse
import com.example.bluetoothexample.model.Error
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

    //получить устройства
    suspend fun fetchBoundedBTDevices(): Flow<Result<BoundedBTDevicesResponse>?> {
        return flow {
            emit(fetchBoundedBTDevicesCached())
        }.flowOn(Dispatchers.IO)
    }
    private fun fetchBoundedBTDevicesCached(): Result<BoundedBTDevicesResponse>? =
        btdeviceDao.getAll()?.let {
            Result.success(BoundedBTDevicesResponse(it))

    }

    //добавить/удалить устройства
    suspend fun insertDeleteBTDevice (btDevices:ArrayList<BTDevice>) {
        val listMac    = ArrayList<String>()
        for (device in btDevices) {
            listMac.add(device.mac)
        }
        btdeviceDao.insertBTDevices(btDevices)              //добавить новые подключенные устройства
        btdeviceDao.deleteBTDevicesNotMacAddress(listMac)   //удалить старые отключенные устройства
    }


    //предполагается, что функция будет дёргаться из ViewModel какого-то фрагмента или активити
    //при нажатии на кнопку "отправить данные"
    //первым параметром ожидается список всех строк сканирования, подготовленных к отправке
    //а вторым параметром ожидается экземпляр объявленного во ViewModel MutableLiveData,
    //на который будет подписана Activity или Fragment. Через этот MutableLiveData мы в функции
    //будем оповещать о ходе и результате отправки данных на сервер.
    @DelicateCoroutinesApi
    fun sendScanData(scans:List<BTScan>, correspondenceLiveData: MutableLiveData<Result<Int>>){
        correspondenceLiveData.value = Result.loading(0)

        GlobalScope.launch(Dispatchers.IO) {
            val apiResponse: HashMap<String, Any>? = HttpClient().sendScanData(
                scans,
                "mac address"
            )
            val error = apiResponse!!["error"] as String
            val success = apiResponse["success"] as Int

            if (success == 1){
                //данные сканирования были успешно переданы на сервер.
                //на клиенте они больше не нужны и их можно удалить
                correspondenceLiveData.postValue(Result.success(success))
            } else {
                correspondenceLiveData.postValue(Result.error(error, Error(0, error)))
            }
        }
    }
 }