package com.example.bluetoothexample.data.local

import androidx.room.*
import com.example.bluetoothexample.model.BTScan
import kotlinx.coroutines.flow.Flow

@Dao
interface BTScanDao {

    @Query("SELECT * FROM btscan order by id DESC")
    fun getAll(): List<BTScan>?
    //В режиме REPLACE старая запись будет заменена новой.
    // Этот режим хорошо подходит, если вам надо вставить запись,
    // если ее еще нет в таблице или обновить запись, если она уже есть.
    //Также есть режим IGNORE.
    // В этом режиме будет оставлена старая запись и операция вставки не будет выполнена.
    //Более подробно об этих режимах можно прочесть здесь https://sqlite.org/lang_conflict.html
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(scans: List<BTScan>)

    @Delete
    fun delete(scan: BTScan)

    @Delete
    fun deleteAll(scans: List<BTScan>)
}