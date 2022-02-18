package com.example.bluetoothexample.ui.dashboard


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class DeviceConnect  (
        val mac: String,
        val name: String,
        val action: String
    ) : Parcelable

