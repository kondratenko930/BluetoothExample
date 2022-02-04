package com.example.bluetoothexample.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BTScan(
    @NonNull
    @PrimaryKey
    val id: Int,
    val barcode: String,
    val excise: String,
    val status: Int
)