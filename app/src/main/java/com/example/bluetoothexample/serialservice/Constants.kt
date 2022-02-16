package com.example.bluetoothexample.serialservice

import com.example.bluetoothexample.BuildConfig

internal object Constants {
    // values have to be globally unique
    const val INTENT_ACTION_SENT = BuildConfig.APPLICATION_ID + ".Sent"
    const val INTENT_ACTION_START_SERVICE = BuildConfig.APPLICATION_ID + ".StartService"

    const val NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel"
    const val INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivity"

    // values have to be unique within each app
    const val NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001
}