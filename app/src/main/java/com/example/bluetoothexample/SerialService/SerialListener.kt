package com.example.bluetoothexample.SerialService

import java.lang.Exception

interface SerialListener {
    fun onSerialConnect()
    fun onSerialConnectError(e: Exception?)
    fun onSerialRead(data: ByteArray?)
    fun onSerialIoError(e: Exception?)
}