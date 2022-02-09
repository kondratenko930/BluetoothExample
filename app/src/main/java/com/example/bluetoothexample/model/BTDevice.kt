package com.example.bluetoothexample.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
    //(indices = [Index(value = ["mac"],unique = true)])
data class BTDevice(
    @NonNull
    @PrimaryKey
    //val id: Int,
    val mac: String,
    val name: String,
    val status: Int
)