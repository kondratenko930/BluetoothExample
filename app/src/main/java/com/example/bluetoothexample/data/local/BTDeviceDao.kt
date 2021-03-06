package com.example.bluetoothexample.data.local

import androidx.room.*
import com.example.bluetoothexample.model.BTDevice
import com.example.bluetoothexample.model.BoundedBTDevicesResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface BTDeviceDao {

    @Query("SELECT * FROM btdevice order by mac DESC")
    fun getAllFlow(): Flow<List<BTDevice>>

    //В режиме REPLACE старая запись будет заменена новой.
    // Этот режим хорошо подходит, если вам надо вставить запись,
    // если ее еще нет в таблице или обновить запись, если она уже есть.
    //Также есть режим IGNORE.
    // В этом режиме будет оставлена старая запись и операция вставки не будет выполнена.
    //Более подробно об этих режимах можно прочесть здесь https://sqlite.org/lang_conflict.html
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBTDevices(devices: ArrayList<BTDevice>)

    @Query("DELETE FROM BTDevice WHERE mac NOT IN (:mac)")
    suspend fun deleteBTDevicesNotMacAddress(mac: List<String>)



}