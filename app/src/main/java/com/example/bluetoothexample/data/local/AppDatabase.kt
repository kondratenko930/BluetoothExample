package com.example.bluetoothexample.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bluetoothexample.model.BTDevice
import com.example.bluetoothexample.model.BTScan


@Database(entities = [BTDevice::class, BTScan::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun btdeviceDao(): BTDeviceDao
    abstract fun btscanDao(): BTScanDao
}