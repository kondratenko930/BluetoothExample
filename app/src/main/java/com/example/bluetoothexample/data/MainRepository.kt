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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
/*
 https://habr.com/ru/post/568792/
Аннотация @Singleton говорит Hilt, что наш MainRepository будет привязан к SIngletonComponent,
то есть к Application. Первый раз, когда где-то будет создаваться MainRepository, он создаст сам экземпляр класса,
 в последующие разы будет доставаться тот же самый, созданный в 1 раз MainRepository.
https://developer.android.com/training/dependency-injection/hilt-jetpack
 If a single instance needs to be shared across various ViewModels,
 then it should be scoped using either @ActivityRetainedScoped or @Singleton.
 */
class MainRepository @Inject constructor(private val btdeviceDao: BTDeviceDao) {


    val devicesFlow: Flow<List<BTDevice>>
        get() = btdeviceDao.getAllFlow()

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


    //получить устройства2
    suspend fun fetchBoundedBTDevices2(): Flow<Result<BoundedBTDevicesResponse>?> {
        return flow {
            emit(fetchBoundedBTDevicesCached2())
        }.flowOn(Dispatchers.IO)
    }
    private suspend fun fetchBoundedBTDevicesCached2(): Result<BoundedBTDevicesResponse>? =
        btdeviceDao.getAllFlow().first().toList()?.let {
            Result.success(BoundedBTDevicesResponse(it))
        }


    //добавить/удалить устройства
    suspend fun insertDeleteBTDevice (btDevices:ArrayList<BTDevice>) {
        btdeviceDao.insertBTDevices(btDevices)                                  //добавить новые подключенные устройства
        btdeviceDao.deleteBTDevicesNotMacAddress(btDevices.map { it.mac })    //удалить старые отключенные устройства
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