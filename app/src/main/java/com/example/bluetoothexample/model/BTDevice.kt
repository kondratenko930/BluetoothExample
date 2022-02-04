package com.example.bluetoothexample.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BTDevice(
    @NonNull
    @PrimaryKey
    val id: Int,
    val mac: String,
    val name: String
)