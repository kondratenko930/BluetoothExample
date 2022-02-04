package com.example.bluetoothexample.data.local

import androidx.room.*
import com.example.bluetoothexample.model.BTDevice
import kotlinx.coroutines.flow.Flow


@Dao
interface BTDeviceDao {

    @Query("SELECT * FROM btdevice order by id DESC")
    fun getAll(): List<BTDevice>?
    //fun getAll(): Flow<List<BTDevice>>

    //В режиме REPLACE старая запись будет заменена новой.
    // Этот режим хорошо подходит, если вам надо вставить запись,
    // если ее еще нет в таблице или обновить запись, если она уже есть.
    //Также есть режим IGNORE.
    // В этом режиме будет оставлена старая запись и операция вставки не будет выполнена.
    //Более подробно об этих режимах можно прочесть здесь https://sqlite.org/lang_conflict.html
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(devices: List<BTDevice>)


    @Query("DELETE FROM BTDevice")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteList(devices: List<BTDevice>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: BTDevice)

    @Delete
    suspend fun delete(device: BTDevice)




}